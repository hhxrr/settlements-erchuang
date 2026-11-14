package dev.breezes.settlements.presentation.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.breezes.settlements.application.ai.modeldialogue.mock.MockBlueprintLibrary;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import dev.breezes.settlements.bootstrap.registry.entities.EntityRegistry;
import dev.breezes.settlements.compat.create.build.BuildPlan;
import dev.breezes.settlements.compat.create.build.ShandongHousePlan;
import dev.breezes.settlements.infrastructure.minecraft.entities.transit.BusEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 整村民房 / 聚落升级的建造调度器。
 * <p>
 * 修复「悬空 / 遁地」：以前用玩家或钟所在<b>单点</b>的 heightmap 高度当作整个圆范围的统一 Y，
 * 而村庄房屋、钟楼会污染 heightmap 把中心 Y 抬高，导致整片房子生成在半空或陷进地里。
 * 现在 {@link #terrainTopY} 在村庄结构<b>之外的 8 个方向采样</b>空地、取中位数得到真实地形顶层，
 * 清挖从地形顶层之上开始、建筑地板正好落在地形顶层的上一格 —— 每栋房都完美贴地。
 * <p>
 * 三个阶段共用真实链路：{@link MockBlueprintLibrary} 蓝图 → {@link BlueprintParser} →
 * {@link BuildPlan} → 按高度从低到高逐格 setBlock（因此天花板在屋顶之前铺好、可见）。
 * 建造时生成真实村民，手持当前要盖的方块、站在工地边上随进度移动挥臂；
 * 手持方块用 {@link Display.ItemDisplay} 在村民手侧同步显示，斜视能看见「村民拿着方块在搭」。
 */
@EventBusSubscriber
public final class SettlementScaleDemo {

    /** 每 tick 放置格数；近景演示会临时调慢，让「逐格搭建」在视频里看得清。 */
    private static volatile int blocksPerTick = 80;
    private static final int MAX_BLUEPRINT_BLOCKS = 2000;
    public static final int VILLAGE_RADIUS = 56;
    public static final int CITY_RADIUS = 62;
    private static final int DEMOLISH_TOP = 18;
    private static final int BUILDER_COUNT = 8;

    private static final BlueprintParser PARSER = BlueprintParser.create();
    private static final Queue<Placement> PENDING = new ConcurrentLinkedQueue<>();
    /** 当前世界的施工村民（同时只会有一个世界在跑）。 */
    private static final List<Builder> BUILDERS = new ArrayList<>();
    private static int builderCursor = 0;
    /** 当前施工点：村民围在它外圈，而不是挤进建筑内部被墙挡住。 */
    private static BlockPos buildCenter = null;

    private record Placement(ServerLevel level, BlockPos pos, BlockState state) {
    }

    private static final class Builder {
        final Villager villager;
        final Display.ItemDisplay held;

        Builder(Villager villager, Display.ItemDisplay held) {
            this.villager = villager;
            this.held = held;
        }
    }

    private SettlementScaleDemo() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("scaledemo")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("village").executes(SettlementScaleDemo::runVillage))
                .then(Commands.literal("yard").executes(SettlementScaleDemo::runYard))
                .then(Commands.literal("town").executes(SettlementScaleDemo::runTown))
                .then(Commands.literal("city").executes(SettlementScaleDemo::runCity))
                .then(Commands.literal("status").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal(
                            "[scaledemo] 待搭建方块: " + PENDING.size() + "，施工村民: " + BUILDERS.size()), false);
                    return Command.SINGLE_SUCCESS;
                })));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Placement last = null;
        for (int i = 0; i < blocksPerTick; i++) {
            Placement placement = PENDING.poll();
            if (placement == null) {
                break;
            }
            placement.level().setBlock(placement.pos(), placement.state(), 3);
            last = placement;
        }
        // 每个 tick 用本批最后一个方块更新一次村民，避免村民每格都闪烁
        if (last != null) {
            updateBuilders(last);
        }
    }

    // ===================== 贴地：真实地形高度 =====================

    /**
     * 采样村庄结构之外 8 个方向的空地高度，取中位数作为真实地形顶层。
     * 不能用中心点 heightmap —— 村庄房屋/钟楼会把高度抬到屋顶。
     */
    public static int terrainTopY(ServerLevel level, BlockPos center) {
        int[] ys = new int[8];
        int r = 96; // 大于 CITY_RADIUS(62)，落在村庄结构之外
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4.0;
            int x = center.getX() + (int) Math.round(Math.cos(a) * r);
            int z = center.getZ() + (int) Math.round(Math.sin(a) * r);
            ys[i] = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        }
        Arrays.sort(ys);
        return ys[ys.length / 2];
    }

    private static BlockPos groundCenter(ServerLevel level, BlockPos at) {
        return new BlockPos(at.getX(), terrainTopY(level, at), at.getZ());
    }

    // ===================== 世界生成钩子 =====================

    /** 世界生成期：整座村庄替换为三合院山东民房，村心留广场。 */
    public static void regenerateVillage(ServerLevel level, BlockPos center) {
        blocksPerTick = 80;
        enqueueDemolish(level, center, VILLAGE_RADIUS, 10);
        enqueueRoads(level, center, VILLAGE_RADIUS, 16);
        int step = 16;
        int half = 4;
        int r2 = (VILLAGE_RADIUS + 2) * (VILLAGE_RADIUS + 2);
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
                int ox = i * step;
                int oz = j * step;
                if (ox * ox + oz * oz > r2) {
                    continue;
                }
                if (Math.abs(ox) < 8 && Math.abs(oz) < 8) {
                    continue; // 村中心广场留给钟
                }
                ShandongHousePlan.Layout layout = (i + j) % 5 == 0
                        ? ShandongHousePlan.Layout.TWO_STORY
                        : (i - j) % 4 == 0 ? ShandongHousePlan.Layout.WORKSHOP
                        : ShandongHousePlan.Layout.COURTYARD;
                enqueuePlan(level, center, ShandongHousePlan.of(layout), ox - 7, oz - 7, 1);
            }
        }
        ensureBuilders(level, center);
    }

    private static int runVillage(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos center = groundCenter(level, player.blockPosition());
        blocksPerTick = 80;
        regenerateVillage(level, center);
        return ok(ctx, "[scaledemo] 整村三合院民房：贴地重建 " + PENDING.size() + " 格");
    }

    /** 近景实拍：只重建镜头前的一个三合院，让村民逐格搭建看得清楚。 */
    private static int runYard(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos center = groundCenter(level, player.blockPosition());
        // 单院约 2400 格，调慢到 6 格/tick，逐格搭建过程约 12 秒，视频可见
        blocksPerTick = 6;
        buildCenter = new BlockPos(center.getX(), center.getY(), center.getZ());
        enqueueDemolish(level, center, 11, DEMOLISH_TOP);
        enqueueRoads(level, center, 18, 16);
        enqueuePlan(level, center, ShandongHousePlan.of(ShandongHousePlan.Layout.COURTYARD), -7, -7, 1);
        ensureBuilders(level, center);
        return ok(ctx, "[scaledemo] 单个三合院：村民逐格搭建 " + PENDING.size() + " 格");
    }

    private static int runTown(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos center = groundCenter(level, player.blockPosition());
        blocksPerTick = 80;
        buildCenter = new BlockPos(center.getX(), center.getY(), center.getZ());
        enqueueDemolish(level, center, VILLAGE_RADIUS, DEMOLISH_TOP);
        enqueueRoads(level, center, VILLAGE_RADIUS, 12);
        int step = 12;
        int half = 4;
        int r2 = (VILLAGE_RADIUS + 2) * (VILLAGE_RADIUS + 2);
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
                int ox = i * step;
                int oz = j * step;
                if (ox * ox + oz * oz > r2) {
                    continue;
                }
                if (Math.abs(ox) <= 6 && Math.abs(oz) <= 6) {
                    continue;
                }
                enqueueBlueprint(level, center, "town_house", ox - 4, oz - 4);
            }
        }
        enqueueBlueprint(level, center, "car_factory", -4, -4);
        enqueueBlueprint(level, center, "bus_stop", -24, 0);
        enqueueBlueprint(level, center, "bus_stop", 24, 0);
        enqueueBlueprint(level, center, "bus_stop", 0, -24);
        enqueueBlueprint(level, center, "bus_stop", 0, 24);
        spawnBuses(level, center, 3);
        ensureBuilders(level, center);
        return ok(ctx, "[scaledemo] 全村拆除重建 → 小镇：" + PENDING.size() + " 格");
    }

    private static int runCity(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        BlockPos center = groundCenter(level, player.blockPosition());
        blocksPerTick = 80;
        buildCenter = new BlockPos(center.getX(), center.getY(), center.getZ());
        enqueueDemolish(level, center, CITY_RADIUS, DEMOLISH_TOP);
        enqueueRoads(level, center, CITY_RADIUS, 15);
        int step = 15;
        int half = 4;
        int r2 = CITY_RADIUS * CITY_RADIUS;
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
                int ox = i * step;
                int oz = j * step;
                if (ox * ox + oz * oz > r2) {
                    continue;
                }
                enqueueBlueprint(level, center, "urban_block", ox - 5, oz - 5);
            }
        }
        enqueueBlueprint(level, center, "car_factory", -4, -4);
        enqueueBlueprint(level, center, "subway_station", -26, 0);
        enqueueBlueprint(level, center, "subway_station", 26, 0);
        enqueueBlueprint(level, center, "subway_station", 0, 26);
        enqueueBlueprint(level, center, "bus_stop", -24, 12);
        enqueueBlueprint(level, center, "bus_stop", 24, 12);
        enqueueSubwayTunnel(level, center);
        ensureBuilders(level, center);
        return ok(ctx, "[scaledemo] 全城拆除重建 → 城市砖瓦街区：" + PENDING.size() + " 格");
    }

    private static int ok(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        return Command.SINGLE_SUCCESS;
    }

    // ===================== 蓝图链路 =====================

    private static void enqueueBlueprint(ServerLevel level, BlockPos center, String archetype, int ox, int oz) {
        BuildPlan plan = PARSER.fromDto(MockBlueprintLibrary.build(archetype), MAX_BLUEPRINT_BLOCKS);
        if (plan == null || plan.size() == 0) {
            return;
        }
        enqueuePlan(level, center, plan, ox, oz, 1);
    }

    /** 按 y 从低到高排序入队：地板 → 墙 → 天花板 → 屋顶，逐格搭建有层次。 */
    private static void enqueuePlan(ServerLevel level, BlockPos center, BuildPlan plan, int ox, int oz, int oy) {
        BlockPos origin = center.offset(ox, oy, oz);
        List<BuildPlan.PlaceEntry> entries = new ArrayList<>(plan.entries());
        entries.sort(Comparator.comparingInt(e -> e.offset().getY()));
        for (BuildPlan.PlaceEntry entry : entries) {
            PENDING.add(new Placement(level, entry.toWorld(origin), entry.state()));
        }
    }

    /** 逐格清挖：从真实地形顶层的上一格开始，绝不挖掉地面本身。 */
    private static void enqueueDemolish(ServerLevel level, BlockPos center, int radius, int top) {
        BlockState air = Blocks.AIR.defaultBlockState();
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > r2) {
                    continue;
                }
                for (int dy = 1; dy <= top; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.getBlockState(pos).isAir()) {
                        PENDING.add(new Placement(level, pos, air));
                    }
                }
            }
        }
    }

    /** 重修路面：井字形石板村道（亮色中心 + 石砖路缘 + 交叉口），铺在贴地铺装层。 */
    private static void enqueueRoads(ServerLevel level, BlockPos center, int radius, int step) {
        int r2 = radius * radius;
        int halfStep = step / 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > r2) {
                    continue;
                }
                int mx = Math.floorMod(dx, step);
                int mz = Math.floorMod(dz, step);
                boolean roadX = Math.abs(mx - halfStep) <= 1; // 竖向主路
                boolean roadZ = Math.abs(mz - halfStep) <= 1; // 横向主路
                if (!roadX && !roadZ) {
                    continue;
                }
                BlockState st;
                if (roadX && roadZ) {
                    st = Blocks.STONE_BRICKS.defaultBlockState(); // 十字路口
                } else {
                    int m = roadX ? mx : mz;
                    if (m == halfStep) {
                        st = ((dx / 7 + dz / 7) % 5 == 0)
                                ? Blocks.GRAVEL.defaultBlockState()
                                : Blocks.POLISHED_ANDESITE.defaultBlockState();
                    } else {
                        st = Blocks.COBBLESTONE.defaultBlockState(); // 路缘
                    }
                }
                PENDING.add(new Placement(level, center.offset(dx, 1, dz), st));
                if (roadX && roadZ && (dx / step + dz / step) % 3 == 0) {
                    PENDING.add(new Placement(level, center.offset(dx, 2, dz),
                            Blocks.LANTERN.defaultBlockState())); // 路灯
                }
            }
        }
    }

    // ===================== 村民持方块逐格搭建 =====================

    private static void ensureBuilders(ServerLevel level, BlockPos center) {
        buildCenter = new BlockPos(center.getX(), center.getY(), center.getZ());
        if (!BUILDERS.isEmpty()) {
            Builder first = BUILDERS.get(0);
            if (first.villager.isAlive() && first.villager.level() == level) {
                return;
            }
            BUILDERS.clear();
        }
        for (int i = 0; i < BUILDER_COUNT; i++) {
            try {
                double a = i * 2 * Math.PI / BUILDER_COUNT;
                double x = center.getX() + Math.cos(a) * 9 + 0.5;
                double z = center.getZ() + Math.sin(a) * 9 + 0.5;
                // 地板铺在 center.y+1；村民站在地板之上 = center.y+2（与家具同层）
                double y = center.getY() + 2.0;
                Villager v = new Villager(EntityType.VILLAGER, level);
                v.setPos(x, y, z);
                v.setNoGravity(true); // 固定在贴地高度，绝不被重力拽进地里
                v.setPersistenceRequired();
                v.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Blocks.STONE_BRICKS));
                level.addFreshEntity(v);

                Display.ItemDisplay held = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
                held.getSlot(0).set(new ItemStack(Blocks.STONE_BRICKS));
                held.setPos(x + 0.35, y + 1.15, z + 0.35);
                level.addFreshEntity(held);
                BUILDERS.add(new Builder(v, held));
            } catch (Throwable t) {
                System.out.println("[settlements] 施工村民生成跳过: " + t);
            }
        }
    }

    /** 每个 tick：把村民移到刚放置的方块边上，手持该方块、挥臂，手持展示同步跟随。 */
    private static void updateBuilders(Placement last) {
        if (BUILDERS.isEmpty()) {
            return;
        }
        try {
            ItemStack stack = new ItemStack(last.state().getBlock().asItem());
            if (stack.is(Items.AIR)) {
                return;
            }
            int n = BUILDERS.size();
            BlockPos site = buildCenter != null ? buildCenter : last.pos();
            // 用缓存的施工点地形高度（不再每 tick 重采样，避免采到挖空的坑而偏低）
            // 地板层 = center.y+1，村民站地板之上 = center.y+2
            int baseY = (buildCenter != null ? buildCenter.getY() : site.getY()) + 2;
            for (int i = 0; i < n; i++) {
                Builder b = BUILDERS.get(i);
                // 固定站在施工点外圈（院落外的开阔道路上），不被院墙/房屋挡住
                double a = i * 2 * Math.PI / n;
                double x = site.getX() + Math.cos(a) * 9 + 0.5;
                double z = site.getZ() + Math.sin(a) * 9 + 0.5;
                double y = baseY;
                b.villager.setPos(x, y, z);
                b.villager.setItemSlot(EquipmentSlot.MAINHAND, stack);
                b.villager.swing(InteractionHand.MAIN_HAND, true);
                b.villager.getLookControl().setLookAt(
                        last.pos().getX() + 0.5, y + 1.0, last.pos().getZ() + 0.5);
                b.held.getSlot(0).set(stack);
                b.held.setPos(x + 0.35, y + 1.15, z + 0.35);
            }
            builderCursor = (builderCursor + 1) % Math.max(1, n);
        } catch (Throwable t) {
            // 村民表现层异常绝不影响方块建造
        }
    }

    // ===================== 公交 / 地铁 =====================

    private static void spawnBuses(ServerLevel level, BlockPos center, int count) {
        for (int i = 0; i < count; i++) {
            BusEntity bus = new BusEntity(EntityRegistry.BUS.get(), level);
            bus.setPos(center.getX() + (i % 3 - 1) * 9 + 0.5,
                    center.getY() + 2.0,
                    center.getZ() + (i / 3) * 9 + 0.5);
            level.addFreshEntity(bus);
        }
    }

    private static void enqueueSubwayTunnel(ServerLevel level, BlockPos center) {
        int tunnelY = center.getY() - 5;
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState rail = Blocks.POWERED_RAIL.defaultBlockState();
        for (int dx = -30; dx <= 30; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    PENDING.add(new Placement(level,
                            new BlockPos(center.getX() + dx, tunnelY + dy, center.getZ() + dz), air));
                }
            }
            PENDING.add(new Placement(level,
                    new BlockPos(center.getX() + dx, tunnelY, center.getZ()), rail));
        }
    }
}
