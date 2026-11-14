package dev.breezes.settlements.application.settlement.scaling;

import dev.breezes.settlements.application.ai.modeldialogue.mock.MockBlueprintLibrary;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintParser;
import dev.breezes.settlements.compat.create.build.BuildPlan;
import dev.breezes.settlements.domain.settlement.model.Settlement;
import dev.breezes.settlements.domain.settlement.model.SettlementTier;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.CustomLog;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聚落规模升级服务（服务端单例）。
 * <p>
 * 职责：
 * <ol>
 *   <li>按人口计算聚落应处的 {@link SettlementTier}；</li>
 *   <li>维护「已完成的升级」去重表，避免同一聚落被反复改造；</li>
 *   <li>把离线/模型蓝图原型（{@link MockBlueprintLibrary}）转换为村民可建造的 {@link BuildPlan}。</li>
 * </ol>
 * <p>
 * 真正的「开挖→重建→生成公交/地铁」动作由 {@code ScaleSettlementBehavior} 与 {@code BuildSubwayBehavior}
 * 在村民被调度时执行；本服务只负责判定与产出蓝图。
 */
@CustomLog
@Singleton
public final class SettlementScalingService {

    /** 升级改造所用蓝图的最大方块数（高于此值直接拒绝，避免一次改造过久）。 */
    private static final int SCALING_MAX_BLOCKS = 2000;

    /** 已完成的升级等级：settlementId → 已改造到的最高等级。 */
    private final Map<UUID, SettlementTier> upgradedTo = new ConcurrentHashMap<>();

    /** 已铺设地铁的聚落。 */
    private final Map<UUID, Boolean> subwayBuilt = new ConcurrentHashMap<>();

    private final BlueprintParser blueprintParser;

    @Inject
    public SettlementScalingService(@Nonnull BlueprintParser blueprintParser) {
        this.blueprintParser = blueprintParser;
    }

    /**
     * 聚落人口（名册人数）。
     */
    public int populationOf(@Nonnull Settlement settlement) {
        return settlement.roster().entries().size();
    }

    /**
     * 聚落按人口应处的等级。
     */
    @Nonnull
    public SettlementTier tierOf(@Nonnull Settlement settlement) {
        return SettlementTier.forPopulation(populationOf(settlement));
    }

    /**
     * 本服务已为该聚落完成改造到的最高等级（未改造过则为 {@link SettlementTier#VILLAGE}）。
     */
    @Nonnull
    public SettlementTier recordedTier(@Nonnull UUID settlementId) {
        return this.upgradedTo.getOrDefault(settlementId, SettlementTier.VILLAGE);
    }

    /**
     * 聚落是否需要升级：当前应处等级高于已改造等级。
     */
    public boolean shouldScale(@Nonnull Settlement settlement) {
        return tierOf(settlement).ordinal() > recordedTier(settlement.id()).ordinal();
    }

    /**
     * 记录该聚落已改造到 {@code tier}（及其以下所有等级）。
     */
    public void recordScaled(@Nonnull UUID settlementId, @Nonnull SettlementTier tier) {
        this.upgradedTo.merge(settlementId, tier, (oldVal, newVal) ->
                newVal.ordinal() > oldVal.ordinal() ? newVal : oldVal);
    }

    /**
     * 该聚落是否还需要铺设地铁（仅城市需要，且只铺一次）。
     */
    public boolean needsSubway(@Nonnull UUID settlementId) {
        return !Boolean.TRUE.equals(this.subwayBuilt.get(settlementId));
    }

    /**
     * 标记该聚落地铁已铺设。
     */
    public void markSubway(@Nonnull UUID settlementId) {
        this.subwayBuilt.put(settlementId, Boolean.TRUE);
    }

    /**
     * 把蓝图原型（如 {@code "town_house"}、{@code "car_factory"}、{@code "subway_station"}）解析为可建造的
     * {@link BuildPlan}；无法解析时返回 {@code null}。
     */
    @Nonnull
    public BuildPlan planFor(@Nonnull String archetype) {
        return this.blueprintParser.fromDto(MockBlueprintLibrary.build(archetype), SCALING_MAX_BLOCKS);
    }
}
