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
 * 做饭：到达三餐时段（07:00 / 12:00 / 18:00）走到家中厨房（灶台/烟囱附近）开始做饭，
 * 期间把家附近烟囱标记为「正在做饭」，触发 {@code ChimneyBlockEntity} 升起炊烟；做完熄火。
 */
@CustomLog(topic = "behavior")
public class CookMealBehavior extends AbstractBehavior<BaseVillager> {

    private int dwell = 0;
    private static final int DWELL_TICKS = ClockTicks.seconds(25).getTicksAsInt();

    public CookMealBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(3).asTickable(), ClockTicks.minutes(40).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isMealTime(world)) {
            this.requestStop("Not meal time");
            return;
        }
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        // 厨房：就近找烟囱（优先）或炉灶作为做饭点
        BlockPos kitchen = DomesticUtil.findNearest(world, home, 16,
                DomesticUtil.isBlock(Blocks.FURNACE).or(DomesticUtil.isBlock(Blocks.CAMPFIRE)).or(DomesticUtil.isBlock(Blocks.SMOKER)));
        if (kitchen == null) {
            kitchen = home;
        }
        DomesticUtil.setChimneyCooking(world, home, 16, true);
        villager.getNavigation().moveTo(kitchen.getX() + 0.5, kitchen.getY(), kitchen.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isMealTime(world)) {
            this.finishCooking(world, villager);
            return;
        }
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            this.dwell += delta;
            if (this.dwell >= DWELL_TICKS) {
                this.finishCooking(world, villager);
            }
        }
    }

    private void finishCooking(@Nonnull Level world, @Nonnull BaseVillager villager) {
        DomesticUtil.homeOf(villager).ifPresent(home -> DomesticUtil.setChimneyCooking(world, home.pos(), 16, false));
        this.requestStop("Cooked");
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        DomesticUtil.homeOf(villager).ifPresent(home -> DomesticUtil.setChimneyCooking(world, home.pos(), 16, false));
        villager.getNavigation().stop();
    }

    private static boolean isMealTime(@Nonnull Level world) {
        long t = world.getDayTime() % 24000L;
        long[] marks = {TimeOfDay.AT_07_00.getMinecraftTick(), TimeOfDay.AT_12_00.getMinecraftTick(), TimeOfDay.AT_18_00.getMinecraftTick()};
        for (long m : marks) {
            if (Math.abs(t - m) <= 1500L) { // ±15 游戏分钟窗口
                return true;
            }
        }
        return false;
    }
}
