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
 * 睡觉：入夜后（20:00 之后）回到家里的床，并把家附近的红石灯<b>关掉</b>（通过把拉杆拨到断电）。
 * <p>
 * 对应作息第 10 步「躺到床上，关红石灯」。床附近的拉杆控制红石灯；村民「拉拉杆」即熄灭。
 */
@CustomLog(topic = "behavior")
public class SleepHomeBehavior extends AbstractBehavior<BaseVillager> {

    private boolean lampOff = false;

    public SleepHomeBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(2).asTickable(), ClockTicks.minutes(15).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isNight(world)) {
            this.requestStop("Not night");
            return;
        }
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        // 关灯（拉拉杆断电）——对应「关红石灯」
        DomesticUtil.setLeverNear(world, home, 16, false);
        this.lampOff = true;
        // 走到床（就近的床）并躺下
        BlockPos bed = DomesticUtil.findNearest(world, home, 16, DomesticUtil.isBlock(Blocks.RED_BED));
        BlockPos target = bed != null ? bed : home;
        villager.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!isNight(world)) {
            this.requestStop("Night over");
            return;
        }
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            // 已到床边：保持躺卧直到天亮（行为在早晨被起床行为取代）
            this.requestStop("Asleep");
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        // 若天未亮就被中断（如被吵醒），重新开灯以便活动
        if (!isNight(world) && this.lampOff) {
            DomesticUtil.homeOf(villager).ifPresent(home -> DomesticUtil.setLeverNear(world, home.pos(), 16, true));
        }
        villager.getNavigation().stop();
    }

    private static boolean isNight(@Nonnull Level world) {
        long t = world.getDayTime() % 24000L;
        return t >= TimeOfDay.AT_20_00.getMinecraftTick() || t < TimeOfDay.AT_06_00.getMinecraftTick();
    }
}
