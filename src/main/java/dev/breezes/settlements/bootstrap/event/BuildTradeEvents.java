package dev.breezes.settlements.bootstrap.event;

import dev.breezes.settlements.bootstrap.registry.professions.ProfessionRegistry;
import dev.breezes.settlements.compat.aeronautics.AeronauticsCompat;
import dev.breezes.settlements.compat.create.build.HandHousePlan;
import dev.breezes.settlements.infrastructure.minecraft.items.BuildOrderItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

/**
 * 交易菜单接入：为两个新职业添加「用绿宝石换取建造订单」的交易项。
 * <p>
 * 玩家购买订单后，村民背包里出现 {@link BuildOrderItem}，随后由对应的建造行为消费并启动工程。
 * <p>
 * 计价：1 方块 = 1 绿宝石（用户需求）。HOUSE 取房屋蓝图方块数，其余取固定价。
 * <p>
 * 飞艇/船交易仅在安装【航空学】时提供；未安装则建筑师只提供房屋/扩建/铁路。
 */
public final class BuildTradeEvents {

    private BuildTradeEvents() {
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == ProfessionRegistry.ARCHITECT.get()) {
            // 建筑师：房屋、扩建村庄、铁路（均仅需机械动力）。
            addTrade(event, BuildOrderItem.PlanType.HOUSE, HandHousePlan.create().costInBlocks());
            addTrade(event, BuildOrderItem.PlanType.VILLAGE_EXPAND, 64);
            addTrade(event, BuildOrderItem.PlanType.RAILWAY, 96);
            // 飞艇/船：仅当航空学可用。
            if (AeronauticsCompat.isLoaded()) {
                addTrade(event, BuildOrderItem.PlanType.AIRCRAFT, 128);
            }
        } else if (event.getType() == ProfessionRegistry.FACTORY_WORKER.get()) {
            // 工厂工人：机械、拆除、工厂。
            addTrade(event, BuildOrderItem.PlanType.MACHINE, 24);
            addTrade(event, BuildOrderItem.PlanType.DEMOLISH, 8);
            addTrade(event, BuildOrderItem.PlanType.FACTORY, 160);
        } else if (event.getType() == ProfessionRegistry.MECHANIC.get()) {
            // 机械师：火车、火车站、地形修补。
            addTrade(event, BuildOrderItem.PlanType.TRAIN, 200);
            addTrade(event, BuildOrderItem.PlanType.TRAIN_STATION, 80);
            addTrade(event, BuildOrderItem.PlanType.TERRAIN, 40);
        }
    }

    private static void addTrade(VillagerTradesEvent event, BuildOrderItem.PlanType type, int emeraldCost) {
        VillagerTrades.ItemListing listing = (Entity trader, RandomSource random) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                BuildOrderItem.create(type, emeraldCost),
                1, 1, 0.05f);
        // 1.21.1: getTrades() 是 Int2ObjectMap，key 为职业注册表数字 id（不是 VillagerProfession 对象）
        int professionId = BuiltInRegistries.VILLAGER_PROFESSION.getId(event.getType());
        event.getTrades().computeIfAbsent(professionId, k -> new ArrayList<>()).add(listing);
    }
}
