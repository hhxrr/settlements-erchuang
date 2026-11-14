package dev.breezes.settlements.village;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 村庄定位：寻找附近<b>非废弃、且非僵尸村庄</b>的村庄（用于「扩建 / 修铁路到下一个村庄」）。
 * <p>
 * 判定思路：
 * <ul>
 *   <li>用 POI 管理器扫描范围内的「集会点(meeting)」——每个正常村庄都有钟/集会 POI；</li>
 *   <li>对每个候选，统计周边床(HOME)数量：床数低于阈值视为<b>废弃村庄</b>，跳过；</li>
 *   <li><b>排除僵尸村庄</b>：僵尸村庄（以及结构生成的 abandoned village）虽然也有床的 POI，
 *       但<b>没有活着的村民</b>。因此再额外要求候选点附近存在至少一个活村民，否则跳过；</li>
 *   <li>排除与当前村庄过近（同一座村）的点，返回最近的一个。</li>
 * </ul>
 * 全部包裹在防御式 try/catch 中：若运行环境 API 与预期不符，返回 {@link Optional#empty()}，
 * 由上层行为优雅降级（本次不修铁路）。若 POI 扫描只覆盖已加载区块，则在远距离时需要先让区块加载。
 */
public final class VillageLocator {

    /** 搜索半径（方块）。越大越能找到远处村庄，但越慢。 */
    private static final int SEARCH_RADIUS = 256;
    /** 判定为「非废弃」所需的最少床数。 */
    private static final int MIN_BEDS = 3;
    /** 与原点小于此距离的点视为「同一座村庄」，跳过。 */
    private static final int SAME_VILLAGE_DIST = 48;
    /** 判定「有活村民」的扫描半径（以候选村庄中心为基准）。 */
    private static final int VILLAGER_CHECK_RADIUS = 48;

    private VillageLocator() {
    }

    /**
     * 寻找最近的非废弃、非僵尸村庄中心。
     *
     * @param level 世界（需为服务端）
     * @param origin 当前村庄中心
     * @return 非废弃村庄中心坐标（空表示未找到）
     */
    public static Optional<BlockPos> findNearestNonAbandoned(@Nullable Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) {
            return Optional.empty();
        }
        try {
            PoiManager poi = server.getPoiManager();
            Stream<PoiRecord> meetings = poi.getInRange(
                    VillageLocator::isMeeting,
                    origin,
                    SEARCH_RADIUS,
                    PoiManager.Occupancy.ANY);

            return meetings
                    .map(PoiRecord::getPos)
                    .filter(p -> p.distManhattan(origin) > SAME_VILLAGE_DIST)
                    .filter(p -> countBedsNear(server, p) >= MIN_BEDS)
                    .filter(p -> hasLivingVillagerNear(server, p))
                    .min(Comparator.comparingInt(p -> p.distManhattan(origin)));
        } catch (Throwable t) {
            // API 差异或临时失败：优雅降级。
            return Optional.empty();
        }
    }

    /**
     * 候选村庄中心附近是否存在<b>活着的村民</b>（用于排除僵尸村庄 / 结构废弃村庄）。
     * <p>
     * 僵尸村庄的床(POI)齐全，但村民都是僵尸村民，不会算作正常 Villager；本方法据此将它们排除，
     * 保证铁路/扩建只通往有人居住的村庄。
     */
    private static boolean hasLivingVillagerNear(ServerLevel server, BlockPos center) {
        try {
            AABB box = new AABB(
                    center.getX() - VILLAGER_CHECK_RADIUS, center.getY() - 16, center.getZ() - VILLAGER_CHECK_RADIUS,
                    center.getX() + VILLAGER_CHECK_RADIUS, center.getY() + 32, center.getZ() + VILLAGER_CHECK_RADIUS);
            for (Villager e : server.getEntitiesOfClass(Villager.class, box)) {
                if (!e.isRemoved()) {
                    return true;
                }
            }
            return false;
        } catch (Throwable t) {
            // 扫描失败时不武断排除（避免一刀切阻断所有铁路修建），交由上层处理。
            return true;
        }
    }

    private static boolean isMeeting(Holder<PoiType> holder) {
        if (holder.is(PoiTypes.MEETING)) {
            return true;
        }
        // 兜底：按注册名判断（不同版本常量名可能不同）。
        try {
            String name = holder.unwrapKey().map(k -> k.location().getPath()).orElse("");
            return name.contains("meeting") || name.contains("village");
        } catch (Throwable t) {
            return false;
        }
    }

    private static int countBedsNear(ServerLevel server, BlockPos center) {
        try {
            PoiManager poi = server.getPoiManager();
            return (int) poi.getInRange(
                            holder -> {
                                return holder.is(PoiTypes.HOME);
                            },
                            center,
                            32,
                            PoiManager.Occupancy.ANY)
                    .count();
        } catch (Throwable t) {
            return 0;
        }
    }

    @SuppressWarnings("unused")
    private static Registry<PoiType> placeholder() {
        return null;
    }
}
