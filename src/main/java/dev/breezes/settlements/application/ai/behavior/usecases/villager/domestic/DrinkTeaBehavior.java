package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 空闲喝茶：白天空闲时走到院中茶具（茶具 + 折叠桌）旁坐下喝茶。
 */
@CustomLog(topic = "behavior")
public class DrinkTeaBehavior extends AbstractBehavior<BaseVillager> {

    private int dwell = 0;
    private static final int DWELL_TICKS = ClockTicks.seconds(30).getTicksAsInt();

    public DrinkTeaBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(5).asTickable(), ClockTicks.minutes(60).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        BlockPos tea = DomesticUtil.findNearest(world, home, 20, DomesticUtil.isBlock(BlockRegistry.TEA_SET.get()));
        if (tea == null) {
            this.requestStop("No tea set");
            return;
        }
        villager.getNavigation().moveTo(tea.getX() + 0.5, tea.getY(), tea.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            this.dwell += delta;
            if (this.dwell >= DWELL_TICKS) {
                this.requestStop("Finished tea");
            }
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }
}
