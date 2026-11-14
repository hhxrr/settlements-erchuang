package dev.breezes.settlements.application.settlement.generation;

import dev.breezes.settlements.presentation.commands.SettlementScaleDemo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界生成期整村民房：玩家进入世界后，附近村庄<b>在生成阶段就整体</b>呈现为山东农村风貌，
 * 而不是等到后期由村民一间间改造。
 * <p>
 * 实现方式：服务端 tick 里周期性用 POI（集会点/钟）定位玩家附近的村庄，找到后调用
 * {@link SettlementScaleDemo#regenerateVillage} —— 把<b>整座村庄</b>半径内旧房逐格清挖，
 * 再按网格铺满 {@code ShandongHousePlan} 的带院砖瓦房，村中心保留集会广场。
 * 每座村庄按坐标去重，只生成一次，避免把玩家后续升级出的小镇/城市重新刷回民房。
 * <p>
 * 这是阶段三「山东民房」整改的收口：以前只翻新单栋村民住房，现在改成整村统一生成。
 */
@EventBusSubscriber
public final class ShandongVillageGenerator {

    private static final Set<String> GENERATED = ConcurrentHashMap.newKeySet();
    private static final int CHECK_INTERVAL = 40;
    private static final int SEARCH_RADIUS = 128;
    private static int ticks = 0;

    private ShandongVillageGenerator() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (++ticks % CHECK_INTERVAL != 0) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        ServerLevel level = server.overworld();
        if (level == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Optional<BlockPos> found = nearestVillage(level, player.blockPosition());
            if (found.isEmpty()) {
                continue;
            }
            BlockPos poi = found.get();
            // 用村庄外围空地采样得到的真实地形顶层，避免钟楼/集会点结构污染 heightmap
            int groundY = SettlementScaleDemo.terrainTopY(level, poi);
            BlockPos center = new BlockPos(poi.getX(), groundY, poi.getZ());
            String key = level.dimension().location() + ":" + center.getX() + "," + center.getZ();
            if (!GENERATED.add(key)) {
                continue;
            }
            SettlementScaleDemo.regenerateVillage(level, center);
            System.out.println("[settlements] 世界生成：整村山东民房 @ " + key);
            return;
        }
    }

    /** 找玩家附近最近的村庄集会点（钟）作为村庄中心。 */
    private static Optional<BlockPos> nearestVillage(ServerLevel level, BlockPos origin) {
        try {
            return level.getPoiManager().getInRange(
                            holder -> holder.is(PoiTypes.MEETING),
                            origin, SEARCH_RADIUS, PoiManager.Occupancy.ANY)
                    .map(PoiRecord::getPos)
                    .min(Comparator.comparingInt(p -> p.distManhattan(origin)));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
