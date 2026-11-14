package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.bootstrap.registry.attachments.AttachmentRegistry;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import dev.breezes.settlements.infrastructure.minecraft.blocks.domestic.ChimneyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * 乡村生活行为的共享工具：定位家、就近查找方块、开关拉杆（控制红石灯）。
 */
public final class DomesticUtil {

    private DomesticUtil() {
    }

    /**
     * 村民所属「家」的方块坐标（家设置块位置）。未登记则返回空。
     */
    @Nonnull
    public static java.util.Optional<GlobalPos> homeOf(@Nonnull BaseVillager villager) {
        if (villager.hasData(AttachmentRegistry.VILLAGER_HOME)) {
            return java.util.Optional.of(villager.getData(AttachmentRegistry.VILLAGER_HOME));
        }
        return java.util.Optional.empty();
    }

    /**
     * 在以 {@code center} 为圆心、{@code radius} 为半径内查找首个满足 {@code matcher} 的方块坐标。
     */
    @Nullable
    public static BlockPos findNearest(@Nonnull Level level, @Nonnull BlockPos center, int radius,
                                      @Nonnull Predicate<BlockState> matcher) {
        BlockPos best = null;
        int bestDist = Integer.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (matcher.test(level.getBlockState(p))) {
                        int d = dx * dx + dy * dy + dz * dz;
                        if (d < bestDist) {
                            bestDist = d;
                            best = p;
                        }
                    }
                }
            }
        }
        return best;
    }

    @Nonnull
    public static Predicate<BlockState> isBlock(@Nonnull Block block) {
        return state -> state.is(block);
    }

    /**
     * 在家的附近找一个拉杆并设置为指定通电状态（村民「拉拉杆」开/关红石灯）。
     */
    public static void setLeverNear(@Nonnull Level level, @Nonnull BlockPos center, int radius, boolean powered) {
        BlockPos lever = findNearest(level, center, radius, isBlock(Blocks.LEVER));
        if (lever == null) {
            return;
        }
        BlockState state = level.getBlockState(lever);
        boolean now = state.getValue(BlockStateProperties.POWERED);
        if (now != powered) {
            // 模拟拉杆被扳动：翻转折叠 POWERED 并触发邻居更新（点亮/熄灭红石灯）
            level.setBlock(lever, state.cycle(BlockStateProperties.POWERED), Block.UPDATE_ALL);
            level.updateNeighbourForOutputSignal(lever, Blocks.LEVER);
        }
    }

    /**
     * 设置家附近烟囱的「正在做饭」标记（升起炊烟）。
     */
    public static void setChimneyCooking(@Nonnull Level level, @Nonnull BlockPos center, int radius, boolean cooking) {
        BlockPos chimney = findNearest(level, center, radius, isBlock(BlockRegistry.CHIMNEY.get()));
        if (chimney == null) {
            return;
        }
        if (level.getBlockEntity(chimney) instanceof ChimneyBlockEntity be) {
            be.setCooking(cooking);
        }
    }
}
