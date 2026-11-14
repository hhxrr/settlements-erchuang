package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.domain.time.TimeOfDay;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 起床：清晨（06:00–07:00）从床上起身，走到家门口。红石灯在起床时点亮（由睡觉行为负责熄灭）。
 */
@CustomLog(topic = "behavior")
public class WakeUpBehavior extends AbstractBehavior<BaseVillager> {

    public WakeUpBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(2).asTickable(), ClockTicks.minutes(20).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isMorning(world)) {
            this.requestStop("Not morning");
            return;
        }
        DomesticUtil.homeOf(villager).ifPresentOrElse(home -> {
            BlockPos door = home.pos().above();
            villager.getNavigation().moveTo(door.getX() + 0.5, door.getY(), door.getZ() + 0.5, 1.0);
        }, () -> this.requestStop("No home"));
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isMorning(world)) {
            this.requestStop("Morning over");
            return;
        }
        // 走到门口即完成起床
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            this.requestStop("Woke up");
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }

    private static boolean isMorning(@Nonnull Level world) {
        long t = world.getDayTime() % 24000L;
        long start = TimeOfDay.AT_06_00.getMinecraftTick();
        long end = TimeOfDay.AT_07_00.getMinecraftTick();
        return t >= start && t <= end;
    }
}
