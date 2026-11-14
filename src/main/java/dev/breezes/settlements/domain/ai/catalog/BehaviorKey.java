package dev.breezes.settlements.domain.ai.catalog;

import org.apache.commons.lang3.StringUtils;

/**
 * Type-safe, stable identifier for a registered behavior.
 * <p>
 * Constants are grouped by the theme of the behavior, not by which profession uses them.
 * Profession → behavior mapping lives in {@link ProfessionBehaviorPool}.
 */
public record BehaviorKey(String id) {

    private static final String DISPLAY_NAME_KEY_PREFIX = "ui.settlements.behavior.behavior.";

    // Universal
    public static final BehaviorKey EAT_FOOD = new BehaviorKey("eat_food");

    // Logistics (universal)
    public static final BehaviorKey MANAGE_CHESTS = new BehaviorKey("manage_chests");
    public static final BehaviorKey COLLECT_DEMANDED_ITEM = new BehaviorKey("collect_demanded_item");

    // Social (universal)
    public static final BehaviorKey TRADE_INITIATE = new BehaviorKey("trade_initiate");
    public static final BehaviorKey TRADE_ACCEPT = new BehaviorKey("trade_accept");
    public static final BehaviorKey COURTSHIP_INITIATE = new BehaviorKey("courtship_initiate");
    public static final BehaviorKey COURTSHIP_ACCEPT = new BehaviorKey("courtship_accept");
    public static final BehaviorKey DONATE_EMERALDS = new BehaviorKey("donate_emeralds");

    // Support / Village
    public static final BehaviorKey REPAIR_IRON_GOLEM = new BehaviorKey("repair_iron_golem");

    // Smithing
    public static final BehaviorKey BLAST_ORE = new BehaviorKey("blast_ore");

    // Butchering
    public static final BehaviorKey SMOKE_MEAT = new BehaviorKey("smoke_meat");
    public static final BehaviorKey BUTCHER_LIVESTOCK = new BehaviorKey("butcher_livestock");

    // Cleric
    public static final BehaviorKey THROW_POTIONS = new BehaviorKey("throw_potions");
    public static final BehaviorKey HARVEST_NETHER_WART = new BehaviorKey("harvest_nether_wart");

    // Farming
    public static final BehaviorKey HARVEST_SUGARCANE = new BehaviorKey("harvest_sugarcane");
    public static final BehaviorKey COLLECT_HONEY = new BehaviorKey("collect_honey");
    public static final BehaviorKey HARVEST_HONEYCOMB = new BehaviorKey("harvest_honeycomb");
    public static final BehaviorKey HARVEST_PUMPKIN = new BehaviorKey("harvest_pumpkin");
    public static final BehaviorKey HARVEST_MELON = new BehaviorKey("harvest_melon");
    public static final BehaviorKey HARVEST_SWEET_BERRIES = new BehaviorKey("harvest_sweet_berries");
    public static final BehaviorKey HARVEST_RIPE_CROPS = new BehaviorKey("harvest_ripe_crops");
    public static final BehaviorKey CULTIVATE_PLOT = new BehaviorKey("cultivate_plot");

    // Animal handling
    public static final BehaviorKey MILK_COW = new BehaviorKey("milk_cow");
    public static final BehaviorKey BREED_CHICKENS = new BehaviorKey("breed_chickens");
    public static final BehaviorKey BREED_COWS = new BehaviorKey("breed_cows");
    public static final BehaviorKey BREED_PIGS = new BehaviorKey("breed_pigs");
    public static final BehaviorKey BREED_SHEEP = new BehaviorKey("breed_sheep");
    public static final BehaviorKey SHEAR_SHEEP = new BehaviorKey("shear_sheep");
    public static final BehaviorKey DYE_SHEEP = new BehaviorKey("dye_sheep");
    public static final BehaviorKey TAME_CAT = new BehaviorKey("tame_cat");
    public static final BehaviorKey TAME_WOLF = new BehaviorKey("tame_wolf");
    public static final BehaviorKey WASH_WOLF = new BehaviorKey("wash_wolf");
    public static final BehaviorKey FEED_WOLF = new BehaviorKey("feed_wolf");

    // Idle / Leisure
    public static final BehaviorKey WALK_DOG = new BehaviorKey("walk_dog");
    public static final BehaviorKey PET_ANIMAL = new BehaviorKey("pet_animal");

    // Nitwit / Mischief
    public static final BehaviorKey RING_BELL = new BehaviorKey("ring_bell");
    public static final BehaviorKey THROW_EGGS = new BehaviorKey("throw_eggs");
    public static final BehaviorKey CHASE_CHICKENS = new BehaviorKey("chase_chickens");

    // Foraging
    public static final BehaviorKey SCAVENGE = new BehaviorKey("scavenge");

    // Surveying
    public static final BehaviorKey SURVEY_LANDSCAPE = new BehaviorKey("survey_landscape");
    public static final BehaviorKey CHART_RAIDERS = new BehaviorKey("chart_raiders");
    public static final BehaviorKey MARK_RAID_WAVE = new BehaviorKey("mark_raid_wave");

    // Fishing
    public static final BehaviorKey FISHING = new BehaviorKey("fishing");

    // Fletching
    public static final BehaviorKey RETRIEVE_PROJECTILES = new BehaviorKey("retrieve_projectiles");

    // Crafting
    public static final BehaviorKey CUT_STONE = new BehaviorKey("cut_stone");
    public static final BehaviorKey CRAFT_GOODS = new BehaviorKey("craft_goods");
    public static final BehaviorKey FORGE_TOOL = new BehaviorKey("forge_tool");
    public static final BehaviorKey HARVEST_ORE = new BehaviorKey("harvest_ore");
    public static final BehaviorKey EXCAVATE_SUBSTRATE = new BehaviorKey("excavate_substrate");

    // Enchanting
    public static final BehaviorKey ENCHANT_ITEM = new BehaviorKey("enchant_item");
    public static final BehaviorKey BIND_VEXES = new BehaviorKey("bind_vexes");

    // Leatherworking
    public static final BehaviorKey WASH_LEATHER = new BehaviorKey("wash_leather");
    public static final BehaviorKey DYE_LEATHER = new BehaviorKey("dye_leather");

    // ===== 机械动力整合包：建造与机械 =====
    // 建筑师：放置/破坏方块、按蓝图或手搓建造房屋
    public static final BehaviorKey PLACE_BLOCKS = new BehaviorKey("place_blocks");
    public static final BehaviorKey BREAK_BLOCKS = new BehaviorKey("break_blocks");
    public static final BehaviorKey BUILD_HOUSE = new BehaviorKey("build_house");
    // 工厂工人：建造机械动力各类机械
    public static final BehaviorKey BUILD_MACHINE = new BehaviorKey("build_machine");
    // 工厂工人：在村庄中建造并运转一座机械动力工厂
    public static final BehaviorKey BUILD_FACTORY = new BehaviorKey("build_factory");
    public static final BehaviorKey OPERATE_FACTORY = new BehaviorKey("operate_factory");
    // 建筑师：扩建村庄（在村庄边缘增建房屋）、定位下个村庄并修建机械动力铁路过去
    public static final BehaviorKey EXPAND_VILLAGE = new BehaviorKey("expand_village");
    public static final BehaviorKey BUILD_RAILWAY = new BehaviorKey("build_railway");
    // 建筑师：搭建机械动力飞艇/船（需航空学），并对飞艇进行操控（强力胶浇固、物理化、交互）
    public static final BehaviorKey BUILD_AIRCRAFT = new BehaviorKey("build_aircraft");
    public static final BehaviorKey OPERATE_AIRCRAFT = new BehaviorKey("operate_aircraft");

    // 机械师：建造并驾驶机械动力火车、建造火车站、地形修补
    // 火车需要铺好的机械动力铁轨（由建筑师的 BUILD_RAILWAY 或机械师自己延伸的铁轨支撑）
    public static final BehaviorKey BUILD_TRAIN = new BehaviorKey("build_train");
    public static final BehaviorKey BUILD_TRAIN_STATION = new BehaviorKey("build_train_station");
    public static final BehaviorKey REPAIR_TERRAIN = new BehaviorKey("repair_terrain");

    // ===== 机械动力整合包：乡村生活（山东农村风）=====
    // 通用（所有村民，按作息时间表触发）
    public static final BehaviorKey WAKE_UP = new BehaviorKey("wake_up");
    public static final BehaviorKey USE_BATHROOM = new BehaviorKey("use_bathroom");
    public static final BehaviorKey COOK_MEAL = new BehaviorKey("cook_meal");
    public static final BehaviorKey EAT_AT_TABLE = new BehaviorKey("eat_at_table");
    public static final BehaviorKey SLEEP_HOME = new BehaviorKey("sleep_home");
    // 空闲消遣
    public static final BehaviorKey DRINK_TEA = new BehaviorKey("drink_tea");
    public static final BehaviorKey USE_FAN = new BehaviorKey("use_fan");
    public static final BehaviorKey PLAY_CARDS = new BehaviorKey("play_cards");
    // 婚姻 / 同居
    public static final BehaviorKey MARRY_MOVE_IN = new BehaviorKey("marry_move_in");
    // 村庄建设
    public static final BehaviorKey BUILD_CITY_WALL = new BehaviorKey("build_city_wall");
    public static final BehaviorKey REMODEL_HOUSE = new BehaviorKey("remodel_house");

    // ===== 模型对话建造：村民按模型解析出的蓝图逐格施工 =====
    public static final BehaviorKey BUILD_FROM_BLUEPRINT = new BehaviorKey("build_from_blueprint");

    // ===== 聚落自动升级：村庄→小镇→城市 =====
    // 建筑师/机械师：当所属聚落人口越级时，挖掉旧村、按小模型蓝图重建为小镇/城市，并生成公交/地铁。
    public static final BehaviorKey SCALE_SETTLEMENT = new BehaviorKey("scale_settlement");
    // 机械师：仅在城市档位、一次性铺设地下地铁隧道与充能铁轨。
    public static final BehaviorKey BUILD_SUBWAY = new BehaviorKey("build_subway");

    public BehaviorKey {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("BehaviorKey id must not be blank");
        }
    }

    public static BehaviorKey of(String id) {
        return new BehaviorKey(id);
    }

    public String displayNameKey() {
        return DISPLAY_NAME_KEY_PREFIX + id;
    }

    @Override
    public String toString() {
        return id;
    }

}
