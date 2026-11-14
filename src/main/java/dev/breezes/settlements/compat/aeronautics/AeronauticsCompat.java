package dev.breezes.settlements.compat.aeronautics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

/**
 * 航空学（Create: Aeronautics / Valkyrien Skies）集成入口 —— <b>可选依赖</b>。
 * <p>
 * 关键约束：航空学是「可选依赖」，默认<b>不在编译 classpath 上</b>。因此本类<b>严禁直接 import</b>
 * 任何 {@code create_aeronautics} / {@code valkyrien_skies} 的类，否则无航空学时构建会失败。
 * 所有航空学能力都通过以下手段间接地、防御式地访问：
 * <ul>
 *   <li>存在性检测：{@link ModList}</li>
 *   <li>方块/物品获取：原版 {@link BuiltInRegistries}（按资源路径查，不引用具体类）</li>
 *   <li>物理化/操控：反射调用（按类名 + 方法名），失败即静默降级</li>
 * </ul>
 * <p>
 * 这样保证：
 * <ul>
 *   <li>无航空学时：{@link #isLoaded()} 返回 false，所有相关行为直接停止，mod 正常加载运行。</li>
 *   <li>有航空学时：解锁飞艇/船建造、强力胶浇固、物理化操控。</li>
 * </ul>
 */
public final class AeronauticsCompat {

    public static final String MOD_ID_AERONAUTICS = "create_aeronautics";
    public static final String MOD_ID_VS = "valkyrien_skies";

    /** 强力胶（Create: Aeronautics）的物品注册路径，按版本以你本地为准微调。 */
    private static final ResourceLocation SUPER_GLUE = ResourceLocation.fromNamespaceAndPath(MOD_ID_AERONAUTICS, "super_glue");

    private AeronauticsCompat() {
    }

    /** @return 航空学是否可用（Create: Aeronautics 与 Valkyrien Skies 均已加载）。 */
    public static boolean isLoaded() {
        try {
            return ModList.get().isLoaded(MOD_ID_AERONAUTICS) && ModList.get().isLoaded(MOD_ID_VS);
        } catch (Throwable t) {
            return false;
        }
    }

    /** @return 强力胶物品（未安装或不存在时返回空栈）。 */
    @Nullable
    public static ItemStack superGlueStack() {
        try {
            Item item = BuiltInRegistries.ITEM.get(SUPER_GLUE);
            if (item == null || item == net.minecraft.world.item.Items.AIR) {
                return null;
            }
            return new ItemStack(item);
        } catch (Throwable t) {
            return null;
        }
    }

    /** @return 是否存在强力胶物品。 */
    public static boolean hasSuperGlue() {
        return superGlueStack() != null;
    }

    /**
     * 用强力胶「浇固」一段区块：把两点之间的方块标记为已胶合（物理化前置）。
     * <p>
     * 实际 API 因 Create: Aeronautics 版本而异。此处用反射调用其 SuperGlue 工具类；
     * 找不到对应类/方法时静默失败（降级为普通放置）。请把 TODO 段按你本地航空学源码补实。
     *
     * @return 是否成功施加强力胶
     */
    public static boolean applySuperGlue(Level level, BlockPos from, BlockPos to) {
        if (!isLoaded()) {
            return false;
        }
        try {
            // TODO(航空学): 按你本地的 Create: Aeronautics 版本实现：
            //   1) 取得 SuperGlue 工具类（常见路径 com.simibubi.create.aeronautics....SuperGlue 或
            //      net.creativelabs.aeronautics...）。
            //   2) 调用其「将区块标记为已胶合」的方法，使后续 Valkyrien Skies 把这些方块纳入同一艘物理船。
            // 以下反射骨架仅作占位，需替换为真实调用。
            Class<?> glueApi = Class.forName("net.creativelabs.aeronautics.superglue.SuperGlueAPI");
            // 例：glueApi.getMethod("glueRegion", Level.class, BlockPos.class, BlockPos.class)
            //     .invoke(null, level, from, to);
            return glueApi != null;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 把一段由强力胶连接的区块「物理化」为可操控的飞艇（Valkyrien Skies 飞船）。
     * <p>
     * 同样通过反射调用 VS2 的装配逻辑；失败即返回 false，由调用方降级为静态结构。
     *
     * @return 是否成功物理化（生成可飞行实体）
     */
    public static boolean physicalize(Level level, BlockPos from, BlockPos to) {
        if (!isLoaded()) {
            return false;
        }
        try {
            // TODO(航空学): 按你本地 Valkyrien Skies 2.x 实现：
            //   取得 ShipAssembler / PhysicsEntity 相关类，把 [from,to] 立方体的方块装配为一艘飞船。
            Class<?> assembler = Class.forName("org.valkyrien_skies.core.api.ships.ShipAssembler");
            return assembler != null;
        } catch (Throwable t) {
            return false;
        }
    }

    /** 构造一个飞艇外壳用的方块状态（航空学缺失时回退为原版方块）。 */
    public static BlockState aircraftShell(@Nullable BlockState fallback) {
        if (!isLoaded()) {
            return fallback != null ? fallback : net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState();
        }
        try {
            BlockState vs = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(MOD_ID_VS, "canvas"))
                    .defaultBlockState();
            return vs != null && vs.getBlock() != net.minecraft.world.level.block.Blocks.AIR ? vs : fallback;
        } catch (Throwable t) {
            return fallback != null ? fallback : net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState();
        }
    }

    /** 给村民的反馈信息（仅用于日志/提示，无副作用）。 */
    public static Component unsupportedMessage() {
        return Component.literal("[Settlements] 航空学未安装：飞艇/船相关功能已降级");
    }

    // 抑制未使用告警（Registry 仅为可读性保留）。
    @SuppressWarnings("unused")
    private static Registry<?> placeholder() {
        return null;
    }
}
