package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.domain.time.TimeOfDay;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

/**
 * 卫生间：清晨起床后（07:00–07:30）走到家中的「卫生间」（就近的水/堆肥桶附近）刷牙洗脸，停留片刻。
 */
@CustomLog(topic = "behavior")
public class BathroomBehavior extends AbstractBehavior<BaseVillager> {

    private int dwell = 0;
    private static final int DWELL_TICKS = ClockTicks.seconds(20).getTicksAsInt();

    public BathroomBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(2).asTickable(), ClockTicks.minutes(30).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isBathroomTime(world)) {
            this.requestStop("Not bathroom time");
            return;
        }
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        // 卫生间：就近找水（水或堆肥桶/炼药锅）作为洗漱点
        BlockPos wash = DomesticUtil.findNearest(world, home, 12,
                DomesticUtil.isBlock(Blocks.WATER).or(DomesticUtil.isBlock(Blocks.COMPOSTER)).or(DomesticUtil.isBlock(Blocks.CAULDRON)));
        BlockPos target = wash != null ? wash : home;
        villager.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isBathroomTime(world)) {
            this.requestStop("Bathroom time over");
            return;
        }
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            this.dwell += delta;
            if (this.dwell >= DWELL_TICKS) {
                this.requestStop("Washed up");
            }
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }

    private static boolean isBathroomTime(@Nonnull Level world) {
        long t = world.getDayTime() % 24000L;
        return t >= TimeOfDay.AT_07_00.getMinecraftTick() && t <= TimeOfDay.AT_07_30.getMinecraftTick();
    }
}
