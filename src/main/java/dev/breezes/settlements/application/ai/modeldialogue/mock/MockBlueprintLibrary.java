package dev.breezes.settlements.application.ai.modeldialogue.mock;

import dev.breezes.settlements.application.ai.modeldialogue.parse.BlueprintDTO;
import dev.breezes.settlements.application.ai.modeldialogue.parse.BlockSpecDTO;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 离线模拟器的「程序化蓝图库」：根据玩家意图关键词，生成一份小型建筑蓝图（{@link BlueprintDTO}）。
 * <p>
 * 仅使用原版方块，规模控制在数百方块内，确保村民可以在合理时间内逐格盖完。真实模型接入后，
 * 这份库仅作为「未配置端点时的兜底演示」，不再被调用。
 */
public final class MockBlueprintLibrary {

    private MockBlueprintLibrary() {
    }

    @Nonnull
    public static BlueprintDTO build(@Nonnull String archetype) {
        return switch (archetype.toLowerCase()) {
            case "tower", "ta", "瞭望塔", "塔" -> tower();
            case "well", "井", "水井" -> well();
            case "wall", "墙", "围墙", "围栏" -> wall();
            case "farm", "农田", "农场", "庄稼" -> farm();
            case "forge", "熔炉", "锻造", "铁匠" -> forge();
            case "courtyard", "四合院", "中式", "院子", "庭院" -> courtyard();
            case "twostory", "两层", "二楼", "小楼", "楼房" -> twoStory();
            // ===== 聚落自动升级：小镇 / 城市原型 =====
            case "townhouse", "town_house", "town", "小镇", "镇", "联排" -> townHouse();
            case "urban", "urban_block", "urbanblock", "城建", "街区", "高楼" -> urbanBlock();
            case "busstop", "bus_stop", "bus", "公交", "车站", "公交站" -> busStop();
            case "subway", "subway_station", "subwaystation", "地铁", "地铁站" -> subwayStation();
            case "carfactory", "car_factory", "car", "造车厂", "车厂" -> carFactory();
            default -> cottage();
        };
    }

    private static BlueprintDTO cottage() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 5;
        // 地板
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 0, z, "minecraft:oak_planks"));
        // 墙体（y=1,2），前墙留门、两侧留窗
        for (int y = 1; y <= 2; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                if (!edge) continue;
                boolean door = (z == 0 && x == 2);
                boolean window = (x == 0 && z == 2) || (z == s - 1 && x == 2);
                if (door) { if (y == 1) b.add(block(x, y, z, "minecraft:oak_door")); continue; }
                if (window) { b.add(block(x, y, z, "minecraft:glass")); continue; }
                b.add(block(x, y, z, "minecraft:oak_planks"));
            }
        }
        // 屋顶
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 3, z, "minecraft:cobblestone"));
        return new BlueprintDTO("小木屋", b);
    }

    private static BlueprintDTO tower() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 3;
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 0, z, "minecraft:stone"));
        for (int y = 1; y <= 5; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                if (edge) {
                    if (y == 2 && z == 0 && x == 1) b.add(block(x, y, z, "minecraft:oak_door"));
                    else b.add(block(x, y, z, "minecraft:stone_bricks"));
                }
            }
        }
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 6, z, "minecraft:stone_bricks"));
        return new BlueprintDTO("瞭望塔", b);
    }

    private static BlueprintDTO well() {
        List<BlockSpecDTO> b = new ArrayList<>();
        // 井圈（y=0,1）石砖，中间水
        for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) {
            boolean edge = x == 0 || z == 0 || x == 2 || z == 2;
            if (edge) {
                b.add(block(x, 0, z, "minecraft:cobblestone"));
                b.add(block(x, 1, z, "minecraft:cobblestone"));
            } else {
                b.add(block(x, 0, z, "minecraft:water"));
            }
        }
        // 四角栅栏柱
        b.add(block(0, 2, 0, "minecraft:oak_fence"));
        b.add(block(2, 2, 0, "minecraft:oak_fence"));
        b.add(block(0, 2, 2, "minecraft:oak_fence"));
        b.add(block(2, 2, 2, "minecraft:oak_fence"));
        return new BlueprintDTO("水井", b);
    }

    private static BlueprintDTO wall() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int len = 9;
        for (int x = 0; x < len; x++) {
            b.add(block(x, 0, 0, "minecraft:cobblestone"));
            b.add(block(x, 1, 0, "minecraft:cobblestone"));
        }
        return new BlueprintDTO("矮墙", b);
    }

    private static BlueprintDTO farm() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int w = 5, d = 3;
        for (int x = 0; x < w; x++) for (int z = 0; z < d; z++) {
            b.add(block(x, 0, z, "minecraft:farmland"));
            b.add(block(x, 1, z, "minecraft:wheat"));
        }
        return new BlueprintDTO("农田", b);
    }

    private static BlueprintDTO forge() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 3;
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 0, z, "minecraft:cobblestone"));
        for (int y = 1; y <= 2; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                if (edge) b.add(block(x, y, z, "minecraft:cobblestone"));
            }
        }
        b.add(block(1, 1, 1, "minecraft:furnace"));
        return new BlueprintDTO("熔炉屋", b);
    }

    private static BlueprintDTO courtyard() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 7;
        // 围墙（y=0,1）
        for (int y = 0; y <= 1; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                if (!edge) continue;
                if (y == 0 && z == 0 && x == 3) { b.add(block(x, y, z, "minecraft:oak_door")); continue; }
                b.add(block(x, y, z, "minecraft:red_terracotta"));
            }
        }
        // 院内正房（3x3，位于后侧）
        for (int x = 2; x <= 4; x++) for (int z = 4; z <= 6; z++) {
            b.add(block(x, 0, z, "minecraft:spruce_planks"));
            if (x == 2 || z == 4 || x == 4 || z == 6) {
                b.add(block(x, 1, z, "minecraft:spruce_planks"));
                b.add(block(x, 2, z, "minecraft:spruce_planks"));
            }
        }
        b.add(block(3, 1, 4, "minecraft:oak_door"));
        return new BlueprintDTO("中式小院", b);
    }

    private static BlueprintDTO twoStory() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 5;
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 0, z, "minecraft:dark_oak_planks"));
        for (int y = 1; y <= 4; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                if (!edge) continue;
                if (y == 1 && z == 0 && x == 2) { b.add(block(x, y, z, "minecraft:oak_door")); continue; }
                if ((y == 1 || y == 3) && (x == 0 || z == s - 1) && (z == 2 || x == 2)) { b.add(block(x, y, z, "minecraft:glass")); continue; }
                b.add(block(x, y, z, y <= 2 ? "minecraft:dark_oak_planks" : "minecraft:bricks"));
            }
        }
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 5, z, "minecraft:bricks"));
        return new BlueprintDTO("两层小楼", b);
    }

    private static BlockSpecDTO block(int x, int y, int z, String id) {
        return new BlockSpecDTO(x, y, z, id);
    }

    // ===== 聚落升级原型 =====

    /**
     * 小镇联排房屋：6×6、两层，砖墙 + 木板楼板 + 多窗，比村庄小木屋更「城镇化」。
     * 完全使用原版方块，规模控制在数百方块内。
     */
    /**
     * 小镇「山东联排院落」：青灰砖墙、木圈梁、<b>平整天花板</b>、木挑檐、青瓦四坡顶、
     * 石砌院墙围合的小院，院内摆真实模型的折叠桌椅与茶具。俯视是成片青瓦屋顶加院落巷道。
     */
    private static BlueprintDTO townHouse() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 9;
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 0, z, "minecraft:stone_bricks"));
        for (int y = 1; y <= 3; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                boolean corner = (x == 0 || x == s - 1) && (z == 0 || z == s - 1);
                if (!edge) {
                    if (y == 3) b.add(block(x, y, z, "minecraft:spruce_planks")); // 天花板
                    continue;
                }
                if (z == s - 1 && x == s / 2 && y <= 2) {
                    if (y == 1) b.add(block(x, y, z, "minecraft:spruce_door"));
                    continue;
                }
                if (y == 2 && !corner && (x + z) % 2 == 0) { b.add(block(x, y, z, "minecraft:glass_pane")); continue; }
                if (y == 3) { b.add(block(x, y, z, "minecraft:dark_oak_planks")); continue; } // 圈梁
                b.add(block(x, y, z, y == 1 ? "minecraft:mud_bricks" : "minecraft:stone_bricks"));
            }
        }
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
            boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
            if (edge) b.add(block(x, 4, z, "minecraft:dark_oak_planks"));
        }
        for (int layer = 0; layer <= 2; layer++) {
            int lo = layer + 1;
            int hi = s - 2 - layer;
            for (int x = lo; x <= hi; x++) for (int z = lo; z <= hi; z++) {
                boolean ring = x == lo || x == hi || z == lo || z == hi || layer == 2;
                if (ring) b.add(block(x, 5 + layer, z, "minecraft:deepslate_tiles"));
            }
        }
        for (int x = 3; x <= 5; x++) b.add(block(x, 8, 4, "minecraft:cyan_terracotta"));
        b.add(block(6, 6, 5, "minecraft:bricks"));              // 烟囱
        b.add(block(s / 2, 3, s, "minecraft:lantern"));
        int wlo = -1, whi = s;
        for (int x = wlo; x <= whi; x++) for (int z = wlo; z <= whi; z++) {
            boolean edge = x == wlo || x == whi || z == wlo || z == whi;
            if (!edge) continue;
            if (z == whi && Math.abs(x - s / 2) <= 1) continue;
            b.add(block(x, 1, z, "minecraft:stone_brick_wall"));
            b.add(block(x, 2, z, "minecraft:stone_brick_wall"));
            b.add(block(x, 3, z, "minecraft:stone_brick_slab"));
        }
        // 院内真实家具（X 形折叠凳 / 折叠桌 / 茶具）
        b.add(block(1, 1, 1, "settlements:folding_chair"));
        b.add(block(3, 1, 1, "settlements:folding_chair"));
        b.add(block(2, 1, 1, "settlements:folding_table"));
        b.add(block(1, 1, 4, "settlements:tea_set"));
        return new BlueprintDTO("山东联排院落", b);
    }

    /**
     * 城市「山东砖瓦楼街区」：两层青灰砖楼、层间木楼板兼<b>天花板</b>、玻璃窗、
     * 白檐 + 青瓦四坡顶，外围石砌院墙与浅色街巷；院内摆真实模型家具。
     * 取代原先白色混凝土空心盒的抽象城建方块。
     */
    private static BlueprintDTO urbanBlock() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 11;
        for (int x = -1; x <= s; x++) for (int z = -1; z <= s; z++) b.add(block(x, 0, z, "minecraft:smooth_stone"));
        for (int floor = 0; floor < 2; floor++) {
            int base = 1 + floor * 4;
            for (int y = base; y <= base + 2; y++) {
                for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                    boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                    boolean corner = (x == 0 || x == s - 1) && (z == 0 || z == s - 1);
                    if (!edge) {
                        // 本层天花板（室内平整顶棚 / 上层地板）
                        if (y == base + 2) b.add(block(x, y, z, "minecraft:spruce_planks"));
                        continue;
                    }
                    if (floor == 0 && z == s - 1 && x == s / 2 && y <= base + 1) {
                        if (y == base) b.add(block(x, y, z, "minecraft:spruce_door"));
                        continue;
                    }
                    if (y == base + 1 && !corner && (x + z) % 2 == 0) {
                        b.add(block(x, y, z, "minecraft:glass_pane")); continue;
                    }
                    if (y == base + 2) { b.add(block(x, y, z, "minecraft:dark_oak_planks")); continue; }
                    b.add(block(x, y, z, floor == 0 ? "minecraft:stone_bricks" : "minecraft:mud_bricks"));
                }
            }
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                b.add(block(x, base + 3, z, edge ? "minecraft:dark_oak_planks" : "minecraft:oak_planks"));
            }
        }
        for (int layer = 0; layer <= 3; layer++) {
            int lo = layer + 1;
            int hi = s - 2 - layer;
            if (lo > hi) break;
            for (int x = lo; x <= hi; x++) for (int z = lo; z <= hi; z++) {
                boolean ring = x == lo || x == hi || z == lo || z == hi || layer == 3;
                if (ring) b.add(block(x, 10 + layer, z,
                        layer == 0 ? "minecraft:white_concrete" : "minecraft:deepslate_tiles"));
            }
        }
        for (int x = 4; x <= 6; x++) b.add(block(x, 14, 5, "minecraft:cyan_terracotta"));
        b.add(block(8, 12, 7, "minecraft:bricks"));
        b.add(block(s / 2, 3, s, "minecraft:lantern"));
        int wlo = -1, whi = s;
        for (int x = wlo; x <= whi; x++) for (int z = wlo; z <= whi; z++) {
            boolean edge = x == wlo || x == whi || z == wlo || z == whi;
            if (!edge) continue;
            if (z == whi && Math.abs(x - s / 2) <= 1) continue;
            b.add(block(x, 1, z, "minecraft:stone_brick_wall"));
            b.add(block(x, 2, z, "minecraft:stone_brick_slab"));
        }
        b.add(block(2, 1, 2, "settlements:folding_chair"));
        b.add(block(3, 1, 2, "settlements:folding_table"));
        b.add(block(2, 1, 8, "settlements:tea_set"));
        return new BlueprintDTO("山东城市砖瓦楼", b);
    }

    /**
     * 公交站：3×3 站台 + 四角木栅栏柱 + 顶棚，原版方块即可。
     */
    private static BlueprintDTO busStop() {
        List<BlockSpecDTO> b = new ArrayList<>();
        for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) b.add(block(x, 0, z, "minecraft:smooth_stone"));
        b.add(block(0, 1, 0, "minecraft:oak_fence"));
        b.add(block(2, 1, 0, "minecraft:oak_fence"));
        b.add(block(0, 1, 2, "minecraft:oak_fence"));
        b.add(block(2, 1, 2, "minecraft:oak_fence"));
        for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) b.add(block(x, 2, z, "minecraft:oak_planks"));
        b.add(block(1, 1, 1, "minecraft:lantern"));
        return new BlueprintDTO("公交站", b);
    }

    /**
     * 地铁站入口：在地表开一个向下 4 格的竖井（楼梯用石砖），井底铺一段铁轨。
     * 负值 y 偏移由村民相对建造原点落到地下。
     */
    private static BlueprintDTO subwayStation() {
        List<BlockSpecDTO> b = new ArrayList<>();
        // 地表口部
        b.add(block(0, 0, 0, "minecraft:iron_bars"));
        b.add(block(1, 0, 0, "minecraft:stone_bricks"));
        b.add(block(0, 0, 1, "minecraft:stone_bricks"));
        // 竖井井壁（向下）
        for (int y = -1; y >= -4; y--) {
            b.add(block(0, y, 0, "minecraft:stone_bricks"));
            b.add(block(1, y, 0, "minecraft:stone_bricks"));
            b.add(block(0, y, 1, "minecraft:stone_bricks"));
            b.add(block(1, y, 1, "minecraft:stone_bricks"));
            b.add(block(0, y - 1, 0, "minecraft:stone_bricks")); // 井底地板
        }
        // 井底铁轨
        b.add(block(0, -4, 0, "minecraft:powered_rail"));
        b.add(block(1, -4, 0, "minecraft:rail"));
        return new BlueprintDTO("地铁站", b);
    }

    /**
     * 机械动力造车厂：航空学 + 柴油动力 + 机械动力 casing 的组合厂房（小模型生成）。
     * <p>
     * 结构以原版石砖地基、机械动力 {@code create:andesite_casing} 墙体为主；航空学与柴油动力的方块按
     * {@code create_aeronautics:*} / {@code create_diesel:*} 命名引用——若对应可选 mod 未安装，这些方块在蓝图解析时
     * 退化为空气（静默降级），厂房依旧可盖成形。
     */
    private static BlueprintDTO carFactory() {
        List<BlockSpecDTO> b = new ArrayList<>();
        int s = 9;
        // 地基
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 0, z, "minecraft:stone_bricks"));
        // 墙体（机械动力 casing，缺失 Create 时退化为石砖）
        for (int y = 1; y <= 4; y++) {
            for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) {
                boolean edge = x == 0 || z == 0 || x == s - 1 || z == s - 1;
                if (!edge) continue;
                if (y == 1 && z == 0 && x == 4) { b.add(block(x, y, z, "minecraft:iron_door")); continue; }
                if ((y == 2) && (x == 2 || z == 2)) { b.add(block(x, y, z, "minecraft:glass")); continue; }
                b.add(block(x, y, z, "create:andesite_casing"));
            }
        }
        // 屋顶
        for (int x = 0; x < s; x++) for (int z = 0; z < s; z++) b.add(block(x, 5, z, "minecraft:dark_prismarine"));
        // 中央动力：柴油发电机组（可选；缺失则空气）+ 熔炉引擎 + 航空学机体（可选）
        b.add(block(4, 1, 4, "create:diesel_generator_block"));
        b.add(block(3, 1, 4, "create_aeronautics:chassis"));
        b.add(block(5, 1, 4, "create_aeronautics:engine"));
        b.add(block(4, 1, 3, "minecraft:furnace"));
        b.add(block(4, 2, 4, "create:creative_motor"));
        return new BlueprintDTO("机械动力造车厂", b);
    }
}
