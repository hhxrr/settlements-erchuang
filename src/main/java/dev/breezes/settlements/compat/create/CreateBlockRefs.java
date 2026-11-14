package dev.breezes.settlements.compat.create;

import com.simibubi.create.AllBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * 机械动力方块引用。
 * <p>
 * Create 6.x 把方块统一挂在 {@link AllBlocks} 的静态字段上。
 * 如果你使用的 Create 版本字段名不同（例如旧版用 {@code AllBlocks.CREATIVE_CRATE.get()}），
 * 以你本地 Create 源码为准微调即可。
 */
public final class CreateBlockRefs {

    private CreateBlockRefs() {
    }

    @Nullable
    public static Block creativeCrate() {
        try {
            return AllBlocks.CREATIVE_CRATE.get();
        } catch (Throwable t) {
            return null;
        }
    }

    @Nullable
    public static Block schematicannon() {
        try {
            return AllBlocks.SCHEMATICANNON.get();
        } catch (Throwable t) {
            return null;
        }
    }

    /** 判断某个方块状态是否是创造板条箱（用于自动供料识别）。 */
    public static boolean isCreativeCrate(@Nullable BlockState state) {
        Block crate = creativeCrate();
        return crate != null && state != null && state.is(crate);
    }

    /**
     * 按候选字段名依次尝试从 {@link AllBlocks} 解析一个 Create 方块（ casing 等）。
     * 不同 Create 版本字段名略有差异，故用反射逐个探测，全部失败时返回 {@code null}。
     */
    @Nullable
    private static Block resolve(String... candidateNames) {
        try {
            for (String name : candidateNames) {
                try {
                    java.lang.reflect.Field f = AllBlocks.class.getField(name);
                    Object value = f.get(null);
                    if (value instanceof Block b) {
                        return b;
                    }
                    if (value instanceof Supplier<?> s) {
                        Object got = s.get();
                        if (got instanceof Block b2) {
                            return b2;
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                    // 试下一个候选名
                }
            }
        } catch (Throwable t) {
            return null;
        }
        return null;
    }

    /** 机械动力城墙外壳：优先 Create 的安山岩 casing，缺失则退化为原版安山岩。 */
    @Nullable
    public static Block wallCasing() {
        Block b = resolve("ANDESITE_CASING", "ANDESITE_ALLOY_CASING", "COPPER_CASING", "BRASS_CASING");
        return b != null ? b : net.minecraft.world.level.block.Blocks.ANDESITE;
    }

    /** 机械动力城墙装饰/齿垛：优先黄铜/铜，缺失则退化为原版铜块。 */
    @Nullable
    public static Block wallTrim() {
        Block b = resolve("COPPER_CASING", "BRASS_CASING", "COPPER_BLOCK");
        return b != null ? b : net.minecraft.world.level.block.Blocks.COPPER_BLOCK;
    }

    /** 机械动力动力源（城墙角楼用）：优先蒸汽引擎/熔炉引擎，缺失则退化为红石块。 */
    @Nullable
    public static Block wallPower() {
        Block b = resolve("STEAM_ENGINE", "FURNACE_ENGINE", "CREATIVE_MOTOR");
        return b != null ? b : net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK;
    }
}
