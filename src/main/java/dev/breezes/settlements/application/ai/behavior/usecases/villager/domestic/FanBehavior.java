package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import dev.breezes.settlements.infrastructure.minecraft.blocks.furniture.FurnitureBlock;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 空闲扇扇子：夏天（此处以「白天空闲」近似，具体季节需季节模组）走到院中或屋内，手持扇子轻摇纳凉。
 * <p>
 * 行为上：找到就近的扇子方块（或任意院子位置）后原地停留并周期性小幅抖动表现「扇风」。
 */
@CustomLog(topic = "behavior")
public class FanBehavior extends AbstractBehavior<BaseVillager> {

    private int dwell = 0;
    private static final int DWELL_TICKS = ClockTicks.seconds(25).getTicksAsInt();

    public FanBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(5).asTickable(), ClockTicks.minutes(60).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        BlockPos fan = DomesticUtil.findNearest(world, home, 20,
                state -> state.getBlock() instanceof FurnitureBlock f && f.getKind() == FurnitureBlock.Kind.FAN);
        BlockPos target = fan != null ? fan : home;
        villager.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            // 原地「扇风」：周期性轻微上下浮动
            this.dwell += delta;
            if (this.dwell % ClockTicks.seconds(1).getTicksAsInt() == 0) {
                if (villager.onGround()) {
                    villager.setDeltaMovement(villager.getDeltaMovement().x, 0.04, villager.getDeltaMovement().z);
                }
            }
            if (this.dwell >= DWELL_TICKS) {
                this.requestStop("Done fanning");
            }
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }
}
