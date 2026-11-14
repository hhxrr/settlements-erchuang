package dev.breezes.settlements.bootstrap.registry.professions;

import com.google.common.collect.ImmutableSet;
import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import dev.breezes.settlements.bootstrap.registry.poi.PoiTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 机械动力整合包新增职业注册（建筑师 + 工厂工人 + 机械师，共 3 个）。
 * <p>
 * Create 已声明为必须依赖（见 neoforge.mods.toml），因此这三个职业总是注册；
 * 航空学为可选依赖，相关能力（飞艇/船）在航空学缺失时自动降级。
 * <p>
 * 每个职业都有<b>专属工作站方块</b>（职业方块），并各自注册成 POI：
 * <ul>
 *   <li>建筑师(ARCHITECT) → architect_station（建筑绘图台）</li>
 *   <li>工厂工人(FACTORY_WORKER) → factory_station（操作台）</li>
 *   <li>机械师(MECHANIC) → mechanic_station（机械台）</li>
 * </ul>
 * 村民只要在对应工作站方块附近即可认领职业。工作站方块可通过创造标签页获取，
 * 也可由建造行为在村庄内放置（见 BuildTrainStationBehavior / 村庄生成扩展）。
 */
public final class ProfessionRegistry {

    public static final DeferredRegister<VillagerProfession> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, SettlementsMod.MOD_ID);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> ARCHITECT =
            REGISTRY.register("architect", () -> SettlementsProfession.create(
                    "settlements.architect",
                    PoiTypeRegistry.ARCHITECT_STATION.getKey(),
                    ImmutableSet.of(),
                    ImmutableSet.of(BlockRegistry.ARCHITECT_STATION.get()),
                    null));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> FACTORY_WORKER =
            REGISTRY.register("factory_worker", () -> SettlementsProfession.create(
                    "settlements.factory_worker",
                    PoiTypeRegistry.FACTORY_STATION.getKey(),
                    ImmutableSet.of(),
                    ImmutableSet.of(BlockRegistry.FACTORY_STATION.get()),
                    null));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> MECHANIC =
            REGISTRY.register("mechanic", () -> SettlementsProfession.create(
                    "settlements.mechanic",
                    PoiTypeRegistry.MECHANIC_STATION.getKey(),
                    ImmutableSet.of(),
                    ImmutableSet.of(BlockRegistry.MECHANIC_STATION.get()),
                    null));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
