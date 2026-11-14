package dev.breezes.settlements.bootstrap.registry.poi;

import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.bootstrap.registry.blocks.BlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

/**
 * 三个新增职业的专属 POI（工作站点）注册。
 * <p>
 * 每个 POI 类型对应一个 {@code WorkstationBlock} 的全部方块状态。职业在
 * {@code ProfessionRegistry} 中通过 {@link #ARCHITECT_STATION#getKey()} 等引用这里注册出来的
 * {@code ResourceKey<PoiType>}，村民即可在附近认领该工作站并获得对应职业。
 * <p>
 * 注意：POI 必须在村民认领之前完成注册，{@link DeferredRegister} 会在 mod 加载早期完成，因此直接引用
 * 各 {@code DeferredHolder} 的 {@code getKey()} 即可，无需担心时序。
 */
public final class PoiTypeRegistry {

    public static final DeferredRegister<PoiType> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, SettlementsMod.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> ARCHITECT_STATION = REGISTRY.register("architect_station",
            () -> new PoiType(statesOf(BlockRegistry.ARCHITECT_STATION), 1, 1));

    public static final DeferredHolder<PoiType, PoiType> FACTORY_STATION = REGISTRY.register("factory_station",
            () -> new PoiType(statesOf(BlockRegistry.FACTORY_STATION), 1, 1));

    public static final DeferredHolder<PoiType, PoiType> MECHANIC_STATION = REGISTRY.register("mechanic_station",
            () -> new PoiType(statesOf(BlockRegistry.MECHANIC_STATION), 1, 1));

    private PoiTypeRegistry() {
    }

    private static Set<BlockState> statesOf(
            net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.level.block.Block, ?> holder) {
        return Set.copyOf(holder.get().getStateDefinition().getPossibleStates());
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
