package dev.breezes.settlements.bootstrap.registry.blocks;

import dev.breezes.settlements.SettlementsMod;
import dev.breezes.settlements.infrastructure.minecraft.blocks.DormantOreBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.WorkstationBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.ballista.BallistaBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.cultivation.CultivationLilyBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.domestic.ChimneyBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.domestic.HomeSettingBlock;
import dev.breezes.settlements.infrastructure.minecraft.blocks.furniture.FurnitureBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockRegistry {

    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, SettlementsMod.MOD_ID);

    public static final DeferredHolder<Block, DormantOreBlock> DORMANT_ORE = REGISTRY.register("dormant_ore",
            () -> new DormantOreBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.STONE)
                            .noOcclusion()
                            .randomTicks()
                            .pushReaction(PushReaction.BLOCK),
                    DormantOreBlock.Host.STONE
            ));

    public static final DeferredHolder<Block, DormantOreBlock> DORMANT_DEEPSLATE_ORE = REGISTRY.register("dormant_deepslate_ore",
            () -> new DormantOreBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .requiresCorrectToolForDrops()
                            .strength(3.5F, 6.0F)
                            .sound(SoundType.DEEPSLATE)
                            .noOcclusion()
                            .randomTicks()
                            .pushReaction(PushReaction.BLOCK),
                    DormantOreBlock.Host.DEEPSLATE
            ));

    public static final DeferredHolder<Block, CultivationLilyBlock> CULTIVATION_LILY = REGISTRY.register("cultivation_lily",
            () -> new CultivationLilyBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.PLANT)
                            .noOcclusion()
                            .lightLevel(CultivationLilyBlock::lightEmission)
                            .instabreak()
                            .sound(SoundType.WET_GRASS)
                            .pushReaction(PushReaction.DESTROY)
            ));

    public static final DeferredHolder<Block, BallistaBlock> BALLISTA = REGISTRY.register("ballista",
            () -> new BallistaBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .strength(2.5F)
                            .sound(SoundType.WOOD)
                            // A model drawn inside an occluding block renders dark and culls its neighbors' faces
                            .noOcclusion()
                            .pushReaction(PushReaction.BLOCK)
            ));

    // ===== 机械动力整合包：三个新增职业的专属工作站（职业方块）=====
    // 这些方块同时被 PoiTypeRegistry 注册为 POI，供对应职业村民认领。

    /** 建筑师工作站：建筑绘图台风格（木色）。 */
    public static final DeferredHolder<Block, WorkstationBlock> ARCHITECT_STATION = REGISTRY.register("architect_station",
            () -> new WorkstationBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.BLOCK)));

    /** 工厂工人工作站：操作台风格（金属灰）。 */
    public static final DeferredHolder<Block, WorkstationBlock> FACTORY_STATION = REGISTRY.register("factory_station",
            () -> new WorkstationBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0F)
                    .sound(SoundType.METAL)
                    .pushReaction(PushReaction.BLOCK)));

    /** 机械师工作站：机械台风格（石/铁灰）。 */
    public static final DeferredHolder<Block, WorkstationBlock> MECHANIC_STATION = REGISTRY.register("mechanic_station",
            () -> new WorkstationBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.BLOCK)));

    // ===== 机械动力整合包：乡村生活方块（山东农村风）=====

    // 家具（院子里的折叠椅 / 折叠桌；室内茶具 / 扇子 / 扑克牌）
    public static final DeferredHolder<Block, FurnitureBlock> FOLDING_CHAIR = REGISTRY.register("folding_chair",
            () -> new FurnitureBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(0.6F).sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY), FurnitureBlock.Kind.FOLDING_CHAIR));
    public static final DeferredHolder<Block, FurnitureBlock> FOLDING_TABLE = REGISTRY.register("folding_table",
            () -> new FurnitureBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(0.6F).sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY), FurnitureBlock.Kind.FOLDING_TABLE));
    public static final DeferredHolder<Block, FurnitureBlock> TEA_SET = REGISTRY.register("tea_set",
            () -> new FurnitureBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.4F).sound(SoundType.STONE)
                    .pushReaction(PushReaction.DESTROY), FurnitureBlock.Kind.TEA_SET));
    public static final DeferredHolder<Block, FurnitureBlock> FAN = REGISTRY.register("fan",
            () -> new FurnitureBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ).strength(0.3F).sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY), FurnitureBlock.Kind.FAN));
    public static final DeferredHolder<Block, FurnitureBlock> PLAYING_CARDS = REGISTRY.register("playing_cards",
            () -> new FurnitureBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW).strength(0.2F).sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY), FurnitureBlock.Kind.PLAYING_CARDS));

    /** 家设置块（玩家蹲下右键登记家庭成员；结婚同居的玩家入口）。 */
    public static final DeferredHolder<Block, HomeSettingBlock> HOME_SETTING = REGISTRY.register("home_setting",
            () -> new HomeSettingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN).strength(2.0F).sound(SoundType.WOOD)
                    .pushReaction(PushReaction.BLOCK)));

    /** 烟囱：厨房灶台上的排烟口，方块实体持续冒炊烟。 */
    public static final DeferredHolder<Block, ChimneyBlock> CHIMNEY = REGISTRY.register("chimney",
            () -> new ChimneyBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE).strength(2.0F).sound(SoundType.STONE)
                    .pushReaction(PushReaction.BLOCK)));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }

}
