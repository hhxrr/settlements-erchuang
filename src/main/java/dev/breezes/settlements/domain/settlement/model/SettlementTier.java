package dev.breezes.settlements.domain.settlement.model;

import javax.annotation.Nonnull;

/**
 * 聚落规模等级：村庄 → 小镇 → 城市。
 * <p>
 * 等级完全由聚落人口（名册人数）驱动，阈值集中在此处，方便统一调参。
 * 当人口越过下一档阈值时，{@link dev.breezes.settlements.application.settlement.scaling.SettlementScalingService}
 * 会触发对应的「升级改造」：村庄扩建为小镇（模型生成房屋、造车厂、公交），小镇再扩建为城市（城建方块、地铁）。
 */
public enum SettlementTier {

    /**
     * 村庄：初始形态，少量村民、零散房屋。
     */
    VILLAGE,

    /**
     * 小镇：人口达 {@link #TOWN_POPULATION} 后由村庄改造而来，出现模型生成的成片房屋、机械动力造车厂与公交。
     */
    TOWN,

    /**
     * 城市：人口达 {@link #CITY_POPULATION} 后由小镇改造而来，新增城建方块与地铁。
     */
    CITY;

    /** 村庄升级为小镇所需的人口阈值。 */
    public static final int TOWN_POPULATION = 12;

    /** 小镇升级为城市所需的人口阈值。 */
    public static final int CITY_POPULATION = 30;

    /**
     * 按人口计算聚落应处的等级。
     */
    public static SettlementTier forPopulation(int population) {
        if (population >= CITY_POPULATION) {
            return CITY;
        }
        if (population >= TOWN_POPULATION) {
            return TOWN;
        }
        return VILLAGE;
    }

    /**
     * 本等级是否不低于 {@code other}（用于判断是否需要继续升级）。
     */
    public boolean isAtLeast(@Nonnull SettlementTier other) {
        return this.ordinal() >= other.ordinal();
    }
}
