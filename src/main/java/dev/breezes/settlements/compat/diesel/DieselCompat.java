package dev.breezes.settlements.compat.diesel;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 机械动力：柴油动力（Create: Diesel Generators）集成入口 —— <b>可选依赖</b>。
 * <p>
 * 与 {@code AeronauticsCompat} 同理：柴油动力默认<b>不在编译 classpath 上</b>，因此本类<b>严禁直接 import</b>
 * 任何柴油动力相关的具体类。所有能力通过 {@link ModList} 存在性检测 + 原版
 * {@link BuiltInRegistries} 按资源路径查询间接、防御式地访问，缺失时静默降级。
 * <p>
 * 注意：该附属的真实 modid 在不同版本可能不同（常见为 {@code create_diesel}、{@code dieselgenerators}、
 * {@code diesel_generators}）。下方以候选列表依次探测，命中任意一个即视为已加载。
 * <p>
 * 这样保证：未安装柴油动力时，相关方块在蓝图解析时退化为空气（造车厂蓝图依旧可盖，只是少了柴油机组），
 * 已安装时则能解锁柴油引擎 / 发电机等方块。
 */
public final class DieselCompat {

    /** 柴油动力候选 modid（命中任意一个即视为可用）。 */
    public static final String[] MOD_IDS = {"create_diesel", "dieselgenerators", "diesel_generators"};

    /** 柴油发电机组候选方块路径（按 命名空间:路径 探测）。 */
    private static final String[] GENERATOR_PATHS = {
            "diesel_engine", "diesel_generator", "generator", "diesel_generator_block"
    };

    private DieselCompat() {
    }

    /** @return 柴油动力是否已加载（任一候选 modid 在 ModList 中即可）。 */
    public static boolean isLoaded() {
        try {
            ModList modList = ModList.get();
            for (String id : MOD_IDS) {
                if (modList.isLoaded(id)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable t) {
            return false;
        }
    }

    /** @return 命中的柴油动力 modid，未加载时返回 {@code null}。 */
    @Nullable
    public static String loadedModId() {
        try {
            ModList modList = ModList.get();
            for (String id : MOD_IDS) {
                if (modList.isLoaded(id)) {
                    return id;
                }
            }
        } catch (Throwable t) {
            // 忽略
        }
        return null;
    }

    /**
     * 按候选命名空间 + 候选路径探测一个柴油动力方块；全部未命中返回 {@code null}。
     */
    @Nullable
    public static Block dieselBlock(@Nonnull String... candidatePaths) {
        String ns = loadedModId();
        if (ns == null) {
            return null;
        }
        for (String path : candidatePaths) {
            try {
                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ns, path));
                if (block != null && block != Blocks.AIR) {
                    return block;
                }
            } catch (Throwable t) {
                // 试下一个
            }
        }
        return null;
    }

    /** @return 柴油发电机组方块（未安装时返回 {@code null}）。 */
    @Nullable
    public static Block dieselGenerator() {
        return dieselBlock(GENERATOR_PATHS);
    }

    /** 给村民的反馈信息（仅用于日志/提示，无副作用）。 */
    public static Component unsupportedMessage() {
        return Component.literal("[Settlements] 柴油动力未安装：造车厂蓝图中的柴油机组已降级为空");
    }

    /** 抑制未使用告警。 */
    @SuppressWarnings("unused")
    private static Registry<?> placeholder() {
        return null;
    }

    /** 探测某方块状态是否为柴油动力方块（保守判断：命名空间命中候选之一即可）。 */
    public static boolean isDiesel(@Nullable BlockState state) {
        if (state == null) {
            return false;
        }
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (rl == null) {
            return false;
        }
        String ns = rl.getNamespace();
        for (String id : MOD_IDS) {
            if (id.equals(ns)) {
                return true;
            }
        }
        return false;
    }
}
