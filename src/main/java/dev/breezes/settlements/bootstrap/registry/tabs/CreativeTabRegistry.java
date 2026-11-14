package dev.breezes.settlements.bootstrap.registry.tabs;

import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.bootstrap.registry.items.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class CreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SettlementsMod.MOD_ID);

    public static final Supplier<CreativeModeTab> SETTLEMENTS_TAB = REGISTRY.register("settlements", () -> CreativeModeTab.builder()
            .title(Component.translatable("creative_tab.settlements"))
            .icon(Items.EMERALD::getDefaultInstance)
            .displayItems(((parameters, output) -> {
                output.accept(ItemRegistry.BASE_VILLAGER_SPAWN_EGG.get());
                output.accept(ItemRegistry.VILLAGER_TOTEM.get());
                output.accept(ItemRegistry.CULTIVATION_LILY.get());
                output.accept(ItemRegistry.BALLISTA.get());
                output.accept(ItemRegistry.BALLISTA_BOLT.get());
                // 机械动力整合包：三个职业工作站方块
                output.accept(ItemRegistry.ARCHITECT_STATION.get());
                output.accept(ItemRegistry.FACTORY_STATION.get());
                output.accept(ItemRegistry.MECHANIC_STATION.get());
                // 乡村生活方块：家具、家设置块、烟囱
                output.accept(ItemRegistry.FOLDING_CHAIR.get());
                output.accept(ItemRegistry.FOLDING_TABLE.get());
                output.accept(ItemRegistry.TEA_SET.get());
                output.accept(ItemRegistry.FAN.get());
                output.accept(ItemRegistry.PLAYING_CARDS.get());
                output.accept(ItemRegistry.HOME_SETTING.get());
                output.accept(ItemRegistry.CHIMNEY.get());
                // 模型对话建造：对话手册
                output.accept(ItemRegistry.DIALOGUE_MANUAL.get());
            }))
            .build());

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }

}
