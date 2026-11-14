package dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic;

import dev.breezes.settlements.application.ai.behavior.runtime.AbstractBehavior;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.bootstrap.registry.attachments.AttachmentRegistry;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import lombok.CustomLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * 结婚 / 同居行为：当一名村民已被登记到某个「家设置块」({@code VILLAGER_HOME})，
 * 且附近有另一位<b>同住该家</b>的村民时，双方互写 {@code VILLAGER_SPOUSE} 结成配偶。
 * <p>
 * 玩家把一对村民都通过家设置块登记到同一块，即视为「结婚」；此后两人的 {@code VILLAGER_HOME}
 * 相同，作息中的睡觉/回家行为会把他们导航到同一个家，实现「搬到一起住」。
 * <p>
 * 该行为一次性完成配对（配对后双方都有配偶附件，下一次运行会因已配对而早退），冷却较长避免刷屏。
 */
@CustomLog(topic = "behavior")
public class MarryMoveInBehavior extends AbstractBehavior<BaseVillager> {

    private static final double SEARCH_RADIUS = 48.0;

    public MarryMoveInBehavior(@Nonnull BehaviorSupport support) {
        super(log, ClockTicks.minutes(5).asTickable(), ClockTicks.minutes(30).asTickable());
    }

    @Override
    public void doStart(@Nonnull Level world, @Nonnull BaseVillager villager) {
        if (!villager.hasData(AttachmentRegistry.VILLAGER_HOME)) {
            this.requestStop("No home registered");
            return;
        }
        GlobalPos home = villager.getData(AttachmentRegistry.VILLAGER_HOME);
        UUID selfId = villager.getUUID();

        // 已结婚则无需再配对（除非配偶已不在，则清空以便再婚）
        if (villager.hasData(AttachmentRegistry.VILLAGER_SPOUSE)) {
            UUID current = villager.getData(AttachmentRegistry.VILLAGER_SPOUSE);
            if (current != null && !current.equals(net.minecraft.Util.NIL_UUID)
                    && world.getEntitiesOfClass(BaseVillager.class, new AABB(villager.blockPosition()).inflate(64))
                            .stream().anyMatch(v -> v.getUUID().equals(current))) {
                this.requestStop("Already married");
                return;
            }
        }

        BlockPos center = home.pos();
        List<BaseVillager> cohabitants = world.getEntitiesOfClass(BaseVillager.class,
                new AABB(center).inflate(SEARCH_RADIUS)).stream()
                .filter(other -> !other.getUUID().equals(selfId))
                .filter(other -> other.hasData(AttachmentRegistry.VILLAGER_HOME)
                        && other.getData(AttachmentRegistry.VILLAGER_HOME).equals(home))
                .toList();

        if (cohabitants.isEmpty()) {
            this.requestStop("No cohabitant at home");
            return;
        }

        BaseVillager partner = cohabitants.get(0);
        UUID partnerId = partner.getUUID();
        villager.setData(AttachmentRegistry.VILLAGER_SPOUSE, partnerId);
        partner.setData(AttachmentRegistry.VILLAGER_SPOUSE, selfId);
        log.info("Villager {} and {} are now married (home at {})", selfId, partnerId, home);
        this.requestStop("Married");
    }

    @Override
    public void tickBehavior(int delta, @Nonnull Level world, @Nonnull BaseVillager villager) {
        // 配对逻辑全在 doStart 完成；此处为空（无逐 tick 工作）。
    }

    @Override
    public void doStop(@Nonnull Level world, @Nonnull BaseVillager villager) {
        villager.getNavigation().stop();
    }
}
