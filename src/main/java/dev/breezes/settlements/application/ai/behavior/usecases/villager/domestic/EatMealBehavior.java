package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.domain.time.TimeOfDay;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 吃饭：做饭之后（饭菜端上桌）走到院中折叠桌旁用餐，停留片刻后离席。
 */
@CustomLog(topic = "behavior")
public class EatMealBehavior extends AbstractBehavior<BaseVillager> {

    private int dwell = 0;
    private static final int DWELL_TICKS = ClockTicks.seconds(20).getTicksAsInt();

    public EatMealBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(3).asTickable(), ClockTicks.minutes(40).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isEatTime(world)) {
            this.requestStop("Not eat time");
            return;
        }
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        BlockPos table = DomesticUtil.findNearest(world, home, 16, DomesticUtil.isBlock(BlockRegistry.FOLDING_TABLE.get()));
        BlockPos target = table != null ? table : home;
        villager.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isEatTime(world)) {
            this.requestStop("Eat time over");
            return;
        }
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            this.dwell += delta;
            if (this.dwell >= DWELL_TICKS) {
                this.requestStop("Ate");
            }
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }

    private static boolean isEatTime(@Nonnull Level world) {
        long t = world.getDayTime() % 24000L;
        long[] marks = {TimeOfDay.AT_07_30.getMinecraftTick(), TimeOfDay.AT_12_30.getMinecraftTick(), TimeOfDay.AT_18_30.getMinecraftTick()};
        for (long m : marks) {
            if (Math.abs(t - m) <= 1500L) {
                return true;
            }
        }
        return false;
    }
}
