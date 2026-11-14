package dev.breezes.settlements.bootstrap.registry.items;

import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.bootstrap.registry.entities.EntityRegistry;
import dev.breezes.settlements.infrastructure.minecraft.blocks.cultivation.CultivationLilyItem;
import dev.breezes.settlements.infrastructure.minecraft.blocks.domestic.ChimneyBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.domestic.HomeSettingBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.furniture.FurnitureBlock;
import dev.breezes.settlements.infrastructure.minecraft.items.BuildOrderItem;
import dev.breezes.settlements.infrastructure.minecraft.items.DialogueManualItem;
import dev.breezes.settlements.infrastructure.minecraft.items.VillagerTotemItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemRegistry {

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, SettlementsMod.MOD_ID);

    public static final DeferredHolder<Item, SpawnEggItem> BASE_VILLAGER_SPAWN_EGG = REGISTRY.register("base_villager_spawn_egg",
            () -> new DeferredSpawnEggItem(EntityRegistry.BASE_VILLAGER, 0x6B4030, 0xD49575, new Item.Properties()));

    public static final DeferredHolder<Item, Item> VILLAGER_FISHING_ROD = REGISTRY.register("villager_fishing_rod",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> HAMMER = REGISTRY.register("hammer",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> VILLAGER_TOTEM = REGISTRY.register("villager_totem",
            () -> new VillagerTotemItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, CultivationLilyItem> CULTIVATION_LILY = REGISTRY.register("cultivation_lily",
            () -> new CultivationLilyItem(BlockRegistry.CULTIVATION_LILY.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredHolder<Item, BlockItem> BALLISTA = REGISTRY.register("ballista",
            () -> new BlockItem(BlockRegistry.BALLISTA.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> BALLISTA_BOLT = REGISTRY.register("ballista_bolt",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, BuildOrderItem> BUILD_ORDER = REGISTRY.register("build_order",
            () -> new BuildOrderItem(new Item.Properties().stacksTo(64)
                    .component(net.minecraft.core.component.DataComponents.ITEM_NAME,
                            net.minecraft.network.chat.Component.literal("建房订单"))));

    // ===== 机械动力整合包：三个新增职业的工作站方块物品 =====
    public static final DeferredHolder<Item, BlockItem> ARCHITECT_STATION = REGISTRY.register("architect_station",
            () -> new BlockItem(BlockRegistry.ARCHITECT_STATION.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FACTORY_STATION = REGISTRY.register("factory_station",
            () -> new BlockItem(BlockRegistry.FACTORY_STATION.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> MECHANIC_STATION = REGISTRY.register("mechanic_station",
            () -> new BlockItem(BlockRegistry.MECHANIC_STATION.get(), new Item.Properties()));

    // 机械动力整合包：乡村生活方块物品
    public static final DeferredHolder<Item, BlockItem> FOLDING_CHAIR = REGISTRY.register("folding_chair",
            () -> new BlockItem(BlockRegistry.FOLDING_CHAIR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FOLDING_TABLE = REGISTRY.register("folding_table",
            () -> new BlockItem(BlockRegistry.FOLDING_TABLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TEA_SET = REGISTRY.register("tea_set",
            () -> new BlockItem(BlockRegistry.TEA_SET.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FAN = REGISTRY.register("fan",
            () -> new BlockItem(BlockRegistry.FAN.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> PLAYING_CARDS = REGISTRY.register("playing_cards",
            () -> new BlockItem(BlockRegistry.PLAYING_CARDS.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> HOME_SETTING = REGISTRY.register("home_setting",
            () -> new BlockItem(BlockRegistry.HOME_SETTING.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CHIMNEY = REGISTRY.register("chimney",
            () -> new BlockItem(BlockRegistry.CHIMNEY.get(), new Item.Properties()));

    // 模型对话建造：对话手册（手持对着村民按使用键，打开村民对话界面）
    public static final DeferredHolder<Item, DialogueManualItem> DIALOGUE_MANUAL = REGISTRY.register("dialogue_manual",
            () -> new DialogueManualItem(new Item.Properties().stacksTo(1)
                    .component(net.minecraft.core.component.DataComponents.ITEM_NAME,
                            net.minecraft.network.chat.Component.literal("对话手册"))));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }

}
