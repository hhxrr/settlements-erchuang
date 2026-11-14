package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 空闲打牌：几个村民聚到院中扑克牌旁打牌。每局随机产生一名「赢家」，
 * 赢家会一直高兴地跳（周期性小跳），直到牌局结束。
 * <p>
 * 触发条件：附近存在扑克牌方块且至少有 2 名村民在场（含自己）。
 */
@CustomLog(topic = "behavior")
public class PlayCardsBehavior extends AbstractBehavior<BaseVillager> {

    private static final int RADIUS = 8;
    private int dwell = 0;
    private static final int DWELL_TICKS = ClockTicks.seconds(40).getTicksAsInt();
    private UUID winner = null;
    private int jumpCd = 0;

    public PlayCardsBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(6).asTickable(), ClockTicks.minutes(60).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        BlockPos home = DomesticUtil.homeOf(villager).map(g -> g.pos()).orElse(null);
        if (home == null) {
            this.requestStop("No home");
            return;
        }
        BlockPos cards = DomesticUtil.findNearest(world, home, 24, DomesticUtil.isBlock(BlockRegistry.PLAYING_CARDS.get()));
        if (cards == null) {
            this.requestStop("No cards");
            return;
        }
        List<BaseVillager> players = world.getEntitiesOfClass(BaseVillager.class, new AABB(cards).inflate(RADIUS));
        if (players.size() < 2) {
            this.requestStop("Not enough players");
            return;
        }
        // 随机选一名赢家
        this.winner = players.get(world.random.nextInt(players.size())).getUUID();
        villager.getNavigation().moveTo(cards.getX() + 0.5, cards.getY(), cards.getZ() + 0.5, 1.0);
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        if (villager.getNavigation().isDone() || !villager.getNavigation().isInProgress()) {
            this.dwell += delta;
            // 赢家一直跳
            if (this.winner != null && this.winner.equals(villager.getUUID())) {
                this.jumpCd -= delta;
                if (this.jumpCd <= 0 && villager.onGround()) {
                    villager.setDeltaMovement(villager.getDeltaMovement().x, 0.45, villager.getDeltaMovement().z);
                    this.jumpCd = ClockTicks.seconds(1).getTicksAsInt();
                }
            }
            if (this.dwell >= DWELL_TICKS) {
                this.requestStop("Game over");
            }
        }
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }
}
