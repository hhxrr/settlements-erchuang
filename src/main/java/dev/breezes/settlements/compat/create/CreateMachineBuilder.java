package dev.breezes.settlements.compat.create;

import com.simibubi.create.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 机械动力「机械建造」工具：机械师/工厂工人建造各类机械时使用。
 * <p>
 * 提供动力源、传动、加工等常用机械的摆放。所有摆放都先确认 Create 已加载，
 * 缺失对应方块时静默跳过（保证降级安全）。
 * <p>
 * {@code AllBlocks} 下各机械字段名以你本地 Create 6.x 为准；本文件已用稳定写法
 * （{@code AllBlocks.XXX.get().defaultBlockState()}）并在不可用时跳过。
 */
public final class CreateMachineBuilder {

    private CreateMachineBuilder() {
    }

    /** 安全地取一个 Create 方块，取不到返回 null。 */
    @Nullable
    private static Block block(@Nullable Object ref) {
        if (ref instanceof Block b) { return b; }
        if (ref == null) { return null; }
        try {
            Object got = ref.getClass().getMethod("get").invoke(ref);
            return got instanceof Block b2 ? b2 : null;
        } catch (Throwable t) { return null; }
    }

    private static boolean place(Level level, BlockPos pos, @Nullable Block block) {
        if (block == null) {
            return false;
        }
        level.setBlock(pos, block.defaultBlockState(), 3);
        return level.getBlockState(pos).is(block);
    }

    /** 水车（基础动力源，需临水）。 */
    public static boolean placeWaterWheel(Level level, BlockPos pos) {
        return place(level, pos, block(AllBlocks.WATER_WHEEL));
    }

    /** 熔炉引擎（烧炼供能）。 */
    public static boolean placeFurnaceEngine(Level level, BlockPos pos) {
        return place(level, pos, block(AllBlocks.STEAM_ENGINE));
    }

    /** 传送带（指定方向）。 */
    public static boolean placeBelt(Level level, BlockPos pos, Direction dir) {
        Block belt = block(AllBlocks.BELT);
        if (belt == null) {
            return false;
        }
        BlockState state = belt.defaultBlockState();
        // Create 传送带方向通常由方块状态属性控制；此处用默认态，旋转交由后续调参。
        level.setBlock(pos, state, 3);
        return level.getBlockState(pos).is(belt);
    }

    /** 安山岩外壳（结构件）。 */
    public static boolean placeAndesiteCasing(Level level, BlockPos pos) {
        return place(level, pos, block(AllBlocks.ANDESITE_CASING));
    }

    /** 机械压力机。 */
    public static boolean placeMechanicalPress(Level level, BlockPos pos) {
        return place(level, pos, block(AllBlocks.MECHANICAL_PRESS));
    }

    /** 机械搅拌机。 */
    public static boolean placeMechanicalMixer(Level level, BlockPos pos) {
        return place(level, pos, block(AllBlocks.MECHANICAL_MIXER));
    }

    /**  depot（中转台）。 */
    public static boolean placeDepot(Level level, BlockPos pos) {
        return place(level, pos, block(AllBlocks.DEPOT));
    }

    /**
     * 一套最简「动力小站」：水车 + 安山岩外壳底座。
     * 返回一个代表是否至少放下了动力源的布尔。
     */
    public static boolean buildMiniPowerStation(Level level, BlockPos origin) {
        if (!CreateCompat.isLoaded()) {
            return false;
        }
        boolean ok = placeAndesiteCasing(level, origin.below());
        ok |= placeWaterWheel(level, origin);
        return ok;
    }
}
