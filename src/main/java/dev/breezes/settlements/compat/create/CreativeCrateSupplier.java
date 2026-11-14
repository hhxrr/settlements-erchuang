package dev.breezes.settlements.compat.create;

import dev.breezes.settlements.compat.create.CreateBlockRefs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 创造板条箱（Create 的 Creative Crate）供料器。
 * <p>
 * 用户需求：「建造时方块自动用创造板条箱」。创造板条箱是无限供给源，
 * 因此本类负责：① 在建造点附近找到创造板条箱；② 视为可无限提供任意方块，
 * 建造时直接取用、无需真实消耗。
 * <p>
 * 仅依赖方块类型判定（{@link CreateBlockRefs#isCreativeCrate}），不深入访问 BE 内部，
 * 以保证在 Create 版本差异下仍能编译/运行。
 */
public final class CreativeCrateSupplier {

    private CreativeCrateSupplier() {
    }

    /** 在 radius 范围内是否存在创造板条箱。 */
    public static boolean hasCrateNear(Level level, BlockPos center, int radius) {
        if (!CreateCompat.isLoaded()) {
            return false;
        }
        int r = Math.max(1, radius);
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockState state = level.getBlockState(center.offset(dx, dy, dz));
                    if (CreateBlockRefs.isCreativeCrate(state)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 从创造板条箱「取用」一个目标方块。
     * 因创造板条箱无限供给，这里直接返回一个对应的 ItemStack（数量 1），不真实扣减。
     *
     * @return 可放置的方块物品；若不可用则返回空 stack
     */
    public static ItemStack take(@Nullable Block block) {
        if (block == null || !CreateCompat.isLoaded()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(block);
    }
}
