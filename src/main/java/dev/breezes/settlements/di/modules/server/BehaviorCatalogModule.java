package dev.breezes.settlements.di.modules.server;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import dev.breezes.settlements.application.ai.behavior.runtime.BehaviorSupport;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.BreedAnimalsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.BreedAnimalsConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.BreedSpecies;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.DyeSheepBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.DyeSheepConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.ShearSheepBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.ShearSheepConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.TameCatBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.TameCatConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.TameWolfBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.TameWolfConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.butchering.ButcherLivestockBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.butchering.ButcherLivestockConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.feeding.FeedWolfBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.feeding.FeedWolfConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.milking.MilkCowBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.milking.MilkCowConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.petting.PetAnimalBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.petting.PetAnimalConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.washing.WashWolfBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.animals.washing.WashWolfConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cartographer.ChartRaidersBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cartographer.ChartRaidersConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cartographer.MarkRaidWaveBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cartographer.MarkRaidWaveConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cartographer.SurveyLandscapeBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cartographer.SurveyLandscapeConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cooking.smokemeat.SmokeMeatBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.cooking.smokemeat.SmokeMeatConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.courtship.CourtshipAcceptBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.courtship.CourtshipInitiateBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.courtship.CourtshipInitiateConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.courtship.CourtshipPresenter;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.crafting.CraftGoodsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.crafting.CraftGoodsConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.crafting.CutStoneBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.crafting.CutStoneConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.donation.DonateEmeraldsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.donation.DonationConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.donation.DonationPresenter;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.donation.DonationScanner;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.enchanting.EnchantItemBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.enchanting.EnchantItemConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.CollectHoneyBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.CollectHoneyConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.CultivatePlotBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.CultivatePlotConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestHoneycombBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestHoneycombConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestMelonBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestMelonConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestNetherWartBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestNetherWartConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestOreBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestOreConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestPumpkinBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestPumpkinConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestRipeCropsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestRipeCropsConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestSugarCaneBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestSugarCaneConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestSweetBerriesBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.farming.HarvestSweetBerriesConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.fishing.FishingBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.fishing.FishingConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.forge.ForgeToolBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.forge.ForgeToolConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.hunger.EatFoodBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.hunger.MealPresenter;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.idle.WalkDogBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.idle.WalkDogConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.leatherworking.dyeleather.DyeLeatherBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.leatherworking.dyeleather.DyeLeatherConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.leatherworking.washleather.WashLeatherBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.leatherworking.washleather.WashLeatherConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.librarian.BindVexesBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.librarian.BindVexesConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.logistics.CollectDemandedItemBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.logistics.CollectDemandedItemConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.logistics.ManageChestsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.logistics.ManageChestsConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.logistics.RetrieveProjectilesBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.logistics.RetrieveProjectilesConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.mason.ExcavateSubstrateBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.mason.ExcavateSubstrateConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.nitwit.ChaseChickensBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.nitwit.ChaseChickensConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.nitwit.RingBellBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.nitwit.RingBellConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.nitwit.ThrowEggsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.nitwit.ThrowEggsConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.scavenge.ScavengeBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.scavenge.ScavengeConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.smelting.blastore.BlastOreBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.smelting.blastore.BlastOreConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.support.RepairIronGolemBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.support.RepairIronGolemConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.support.ThrowPotionsBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.support.ThrowPotionsConfig;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.trading.PartnerScanner;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.trading.TradeAcceptBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.trading.TradeInitiateBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.trading.TradeSessionPresenter;
import dev.breezes.settlements.application.ai.courtship.BedReservationService;
import dev.breezes.settlements.application.ai.courtship.CourtshipChoreographyLibrary;
import dev.breezes.settlements.application.ai.courtship.CourtshipSelfMemoryRecorder;
import dev.breezes.settlements.application.ai.courtship.CourtshipSessionRegistry;
import dev.breezes.settlements.application.ai.raid.RaidWaveMarkRegistry;
import dev.breezes.settlements.application.ai.trading.NegotiationEngine;
import dev.breezes.settlements.application.ai.trading.TradeExecutor;
import dev.breezes.settlements.application.ai.trading.TradeSessionRegistry;
import dev.breezes.settlements.application.ai.trading.TradingConfig;
import dev.breezes.settlements.application.economy.VillagerWallet;
import dev.breezes.settlements.application.economy.catalog.TradePriceResolver;
import dev.breezes.settlements.application.enchanting.engine.EnchantmentEngine;
import dev.breezes.settlements.application.settlement.membership.SettlementMembershipService;
import dev.breezes.settlements.di.catalog.BehaviorCatalogEntry;
import dev.breezes.settlements.domain.ai.catalog.BehaviorCategory;
import dev.breezes.settlements.domain.ai.catalog.BehaviorChannel;
import dev.breezes.settlements.domain.ai.catalog.BehaviorDisplayMetadata;
import dev.breezes.settlements.domain.ai.catalog.BehaviorKey;
import dev.breezes.settlements.domain.ai.catalog.BehaviorPlanningMetadata;
import dev.breezes.settlements.domain.ai.catalog.CooldownRange;
import dev.breezes.settlements.domain.ai.catalog.WorkIntensity;
import dev.breezes.settlements.domain.ai.memory.MemoryTypeRegistry;
import dev.breezes.settlements.domain.ai.planning.OpportunityRequirement;
import dev.breezes.settlements.domain.animation.BindVexesAnimations;
import dev.breezes.settlements.domain.animation.CartographerAnimations;
import dev.breezes.settlements.domain.crafting.catalog.CraftCatalogRegistry;
import dev.breezes.settlements.domain.economy.catalog.TradeCatalogRegistry;
import dev.breezes.settlements.domain.farming.CultivationCropRegistry;
import dev.breezes.settlements.domain.forge.catalog.ForgeCatalogRegistry;
import dev.breezes.settlements.domain.smelting.catalog.BlastOreRecipeRegistry;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildHouseBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BreakBlocksBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.ConstructMachineBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.PlaceBlocksBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildFactoryBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.OperateFactoryBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.ExpandVillageBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildRailwayBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildAircraftBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.OperateAircraftBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildTrainBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildTrainStationBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.RepairTerrainBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildCityWallBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildFromBlueprintBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.RemodelHouseBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.ScaleSettlementBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.build.BuildSubwayBehavior;
import dev.breezes.settlements.application.settlement.scaling.SettlementScalingService;
import dev.breezes.settlements.application.settlement.persistence.SettlementStore;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.MarryMoveInBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.WakeUpBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.BathroomBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.CookMealBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.EatMealBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.SleepHomeBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.DrinkTeaBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.FanBehavior;
import dev.breezes.settlements.application.ai.behavior.usecases.villager.domestic.PlayCardsBehavior;
import dev.breezes.settlements.application.ai.modeldialogue.service.BuildOrderQueue;
import dev.breezes.settlements.infrastructure.minecraft.data.animal.ButcherableAnimalDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.farming.hive.CollectHoneyYieldDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.farming.hive.HarvestHoneycombYieldDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.mason.ExcavateSubstrateYieldDataManager;
import dev.breezes.settlements.infrastructure.minecraft.data.scavenge.ScavengeYieldDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

/**
 * Registers all behavior variants as {@link BehaviorCatalogEntry} Dagger multibindings.
 * <p>
 * One method per behavior variant. Parameterized variants that share an implementation class
 * (e.g. breed_pigs / breed_chickens / breed_cows all use {@link BreedAnimalsBehavior}) get
 * separate methods — the factory lambda closes over the species-specific config.
 * <p>
 * Which professions use which behaviors is NOT declared here. That mapping lives in {@link PoolModule}.
 * <p>
 * To add a new behavior: add one {@code @Provides @IntoSet} method here and a key constant
 * to {@link BehaviorKey}. Then add the key to the relevant profession pool(s) in {@link PoolModule}.
 */
@Module
public final class BehaviorCatalogModule {

    // Universal

    @Provides
    @IntoSet
    static BehaviorCatalogEntry manageChests(ManageChestsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.MANAGE_CHESTS)
                        .displayName("Manage Chests")
                        .description("Fetch needed items and deposit surplus across nearby village chests")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .maxRunDuration(ClockTicks.seconds(150))
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.MANAGE_CHESTS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("chest"))
                        .build())
                .factory(() -> new ManageChestsBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry collectDemandedItem(CollectDemandedItemConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.COLLECT_DEMANDED_ITEM)
                        .displayName("Collect Items")
                        .description("Pick up a wanted item dropped nearby")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(15).asGameTicks())
                        .maxRunDuration(CollectDemandedItemBehavior.MAX_RUN_DURATION)
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.COLLECT_DEMANDED_ITEM.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("hopper"))
                        .build())
                .factory(() -> new CollectDemandedItemBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry retrieveProjectiles(RetrieveProjectilesConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.RETRIEVE_PROJECTILES)
                        .displayName("Retrieve Projectiles")
                        .description("Collect stuck arrows and bolts nearby")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .maxRunDuration(ClockTicks.minutes(2))
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.RETRIEVE_PROJECTILES.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("arrow"))
                        .build())
                .factory(() -> new RetrieveProjectilesBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry eatFood(BehaviorSupport support, MealPresenter presenter) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.EAT_FOOD)
                        .displayName("Eat Food")
                        .description("Have a meal -- eats and heals when it helps, otherwise just takes the break")
                        .category(BehaviorCategory.SELF_CARE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(5).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(1, 1))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.EAT_FOOD.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("bread"))
                        .build())
                .factory(() -> new EatFoodBehavior(support, presenter))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry tradeInitiate(TradingConfig config,
                                              BehaviorSupport support,
                                              TradeSessionRegistry sessionRegistry,
                                              TradeCatalogRegistry tradeCatalogRegistry,
                                              TradePriceResolver tradePriceResolver,
                                              VillagerWallet villagerWallet,
                                              TradeExecutor tradeExecutor,
                                              TradeSessionPresenter tradeSessionPresenter,
                                              PartnerScanner partnerScanner,
                                              NegotiationEngine negotiationEngine) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.TRADE_INITIATE)
                        .displayName("Initiate Trade")
                        .description("Seek a nearby villager and propose a trade session")
                        .category(BehaviorCategory.SOCIAL)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .requiredChannel(BehaviorChannel.SOCIAL)
                        .estimatedDuration(ClockTicks.seconds(35).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.TRADE_INITIATE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("emerald"))
                        .build())
                .factory(() -> new TradeInitiateBehavior(config, support, sessionRegistry, tradeCatalogRegistry,
                        tradePriceResolver, villagerWallet, tradeExecutor, tradeSessionPresenter, partnerScanner,
                        negotiationEngine))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry tradeAccept(TradingConfig config,
                                            BehaviorSupport support,
                                            TradeSessionRegistry sessionRegistry,
                                            TradeSessionPresenter tradeSessionPresenter) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.TRADE_ACCEPT)
                        .displayName("Accept Trade")
                        .description("Accept an incoming trade invitation from another villager")
                        .category(BehaviorCategory.SOCIAL)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .requiredChannel(BehaviorChannel.SOCIAL)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.acceptBehaviorCooldownSecondsMin(), config.acceptBehaviorCooldownSecondsMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.TRADE_ACCEPT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("emerald"))
                        .build())
                .factory(() -> new TradeAcceptBehavior(config, support, sessionRegistry, tradeSessionPresenter))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry courtshipInitiate(CourtshipInitiateConfig config,
                                                  BehaviorSupport support,
                                                  CourtshipSessionRegistry courtshipSessionRegistry,
                                                  BedReservationService bedReservationService,
                                                  CourtshipPresenter courtshipPresenter,
                                                  CourtshipChoreographyLibrary choreographyLibrary,
                                                  CourtshipSelfMemoryRecorder selfMemoryRecorder,
                                                  SettlementMembershipService membershipService) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.COURTSHIP_INITIATE)
                        .displayName("Initiate Courtship")
                        .description("Seek a nearby willing villager and initiate courtship")
                        .category(BehaviorCategory.SOCIAL)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .requiredChannel(BehaviorChannel.SOCIAL)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.COURTSHIP_INITIATE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("poppy"))
                        .build())
                .factory(() -> new CourtshipInitiateBehavior(config, support, courtshipSessionRegistry,
                        bedReservationService, courtshipPresenter, choreographyLibrary, selfMemoryRecorder,
                        membershipService))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry courtshipAccept(BehaviorSupport support,
                                                CourtshipSessionRegistry courtshipSessionRegistry,
                                                CourtshipPresenter courtshipPresenter,
                                                CourtshipChoreographyLibrary choreographyLibrary) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.COURTSHIP_ACCEPT)
                        .displayName("Accept Courtship")
                        .description("Accept an incoming courtship invitation from another villager")
                        .category(BehaviorCategory.SOCIAL)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .requiredChannel(BehaviorChannel.SOCIAL)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(1, 1))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.COURTSHIP_ACCEPT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("poppy"))
                        .build())
                .factory(() -> new CourtshipAcceptBehavior(support, courtshipSessionRegistry,
                        courtshipPresenter, choreographyLibrary))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry donateEmeralds(DonationConfig config,
                                               BehaviorSupport support,
                                               DonationScanner scanner,
                                               DonationPresenter presenter,
                                               VillagerWallet wallet) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.DONATE_EMERALDS)
                        .displayName("Donate Emeralds")
                        .description("Seek a nearby destitute neighbor and gift them a few emeralds")
                        .category(BehaviorCategory.SOCIAL)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .requiredChannel(BehaviorChannel.SOCIAL)
                        .estimatedDuration(ClockTicks.seconds(10).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.DONATE_EMERALDS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("emerald"))
                        .build())
                .factory(() -> new DonateEmeraldsBehavior(config, support, scanner, presenter, wallet))
                .build();
    }

    // Village support

    @Provides
    @IntoSet
    static BehaviorCatalogEntry repairIronGolem(RepairIronGolemConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.REPAIR_IRON_GOLEM)
                        .displayName("Repair Iron Golem")
                        .description("Apply iron ingots to repair the village's iron golem")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.REPAIR_IRON_GOLEM.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("iron_ingot"))
                        .build())
                .factory(() -> new RepairIronGolemBehavior(config, support))
                .build();
    }

    // Smithing

    @Provides
    @IntoSet
    static BehaviorCatalogEntry blastOre(BlastOreConfig config, BehaviorSupport support, BlastOreRecipeRegistry recipeRegistry) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BLAST_ORE)
                        .displayName("Blast Ore")
                        .description("Smelt ore in a blast furnace")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.JobSiteBlockOpportunity(Blocks.BLAST_FURNACE))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BLAST_ORE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("blast_furnace"))
                        .build())
                .factory(() -> new BlastOreBehavior(config, support, recipeRegistry))
                .build();
    }

    // Butchering

    @Provides
    @IntoSet
    static BehaviorCatalogEntry smokeMeat(SmokeMeatConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.SMOKE_MEAT)
                        .displayName("Smoke Meat")
                        .description("Cook raw meat in the smoker")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.InventoryItemOpportunity(
                                Set.of(Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.MUTTON, Items.RABBIT, Items.COD, Items.SALMON)))
                        .opportunity(new OpportunityRequirement.JobSiteBlockOpportunity(Blocks.SMOKER))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.SMOKE_MEAT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("smoker"))
                        .build())
                .factory(() -> new SmokeMeatBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry butcherLivestock(ButcherLivestockConfig config, ButcherableAnimalDataManager animalDataManager, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUTCHER_LIVESTOCK)
                        .displayName("Butcher Livestock")
                        .description("Slaughter mature livestock for meat")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUTCHER_LIVESTOCK.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("iron_axe"))
                        .build())
                .factory(() -> new ButcherLivestockBehavior(config, animalDataManager, support))
                .build();
    }

    // Cleric

    @Provides
    @IntoSet
    static BehaviorCatalogEntry throwPotions(ThrowPotionsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.THROW_POTIONS)
                        .displayName("Throw Potions")
                        .description("Throw splash potions at injured nearby villagers")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.THROW_POTIONS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("splash_potion"))
                        .build())
                .factory(() -> new ThrowPotionsBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestNetherWart(HarvestNetherWartConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_NETHER_WART)
                        .displayName("Harvest Nether Wart")
                        .description("Harvest mature nether wart from soul sand")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.NETHER_WART_FARM_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_NETHER_WART.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("nether_wart"))
                        .build())
                .factory(() -> new HarvestNetherWartBehavior(config, support))
                .build();
    }

    // Farming

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestSugarCane(HarvestSugarCaneConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_SUGARCANE)
                        .displayName("Harvest Sugar Cane")
                        .description("Cut mature sugarcane from nearby fields")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.HARVESTABLE_SUGARCANE_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_SUGARCANE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("sugar_cane"))
                        .build())
                .factory(() -> new HarvestSugarCaneBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry collectHoney(CollectHoneyConfig config,
                                             BehaviorSupport support,
                                             CollectHoneyYieldDataManager yieldData) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.COLLECT_HONEY)
                        .displayName("Collect Honey")
                        .description("Collect honey bottles from full beehives")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.FULL_HIVE_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.COLLECT_HONEY.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("honey_bottle"))
                        .build())
                .factory(() -> new CollectHoneyBehavior(config, support, yieldData))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestHoneycomb(HarvestHoneycombConfig config,
                                                 BehaviorSupport support,
                                                 HarvestHoneycombYieldDataManager yieldData) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_HONEYCOMB)
                        .displayName("Harvest Honeycomb")
                        .description("Scrape honeycomb from full beehives using shears")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.FULL_HIVE_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_HONEYCOMB.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("honeycomb"))
                        .build())
                .factory(() -> new HarvestHoneycombBehavior(config, support, yieldData))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestPumpkin(HarvestPumpkinConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_PUMPKIN)
                        .displayName("Harvest Pumpkin")
                        .description("Harvest ripe pumpkins from nearby stems")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.RIPE_PUMPKIN_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_PUMPKIN.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("pumpkin"))
                        .build())
                .factory(() -> new HarvestPumpkinBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestMelon(HarvestMelonConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_MELON)
                        .displayName("Harvest Melon")
                        .description("Harvest ripe melons from nearby stems")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.RIPE_MELON_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_MELON.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("melon"))
                        .build())
                .factory(() -> new HarvestMelonBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestSweetBerries(HarvestSweetBerriesConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_SWEET_BERRIES)
                        .displayName("Harvest Sweet Berries")
                        .description("Pick ripe sweet berries from nearby bushes")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.RIPE_SWEET_BERRY_BUSH_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_SWEET_BERRIES.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("sweet_berries"))
                        .build())
                .factory(() -> new HarvestSweetBerriesBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestRipeCrops(HarvestRipeCropsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_RIPE_CROPS)
                        .displayName("Harvest Ripe Crops")
                        .description("Harvest ripe crops from nearby farmland")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.RIPE_CROP_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_RIPE_CROPS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("wheat"))
                        .build())
                .factory(() -> new HarvestRipeCropsBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry cultivatePlot(CultivatePlotConfig config,
                                              BehaviorSupport support,
                                              CultivationCropRegistry cropRegistry) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.CULTIVATE_PLOT)
                        .displayName("Cultivate Plot")
                        .description("Till soil and plant seeds in a Cultivation zone")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .maxRunDuration(ClockTicks.seconds(180))
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.CULTIVATION_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.CULTIVATE_PLOT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("iron_hoe"))
                        .build())
                .factory(() -> new CultivatePlotBehavior(config, support, cropRegistry))
                .build();
    }

    // Animal handling

    @Provides
    @IntoSet
    static BehaviorCatalogEntry milkCow(MilkCowConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.MILK_COW)
                        .displayName("Milk Cow")
                        .description("Milk nearby cows using a bucket")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.MILK_COW.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("milk_bucket"))
                        .build())
                .factory(() -> new MilkCowBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry breedChickens(BreedAnimalsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BREED_CHICKENS)
                        .displayName("Breed Chickens")
                        .description("Feed and breed nearby chickens to grow the flock")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BREED_CHICKENS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("chicken_spawn_egg"))
                        .build())
                .factory(() -> new BreedAnimalsBehavior(config, support, BreedSpecies.builder()
                        .type(EntityType.CHICKEN)
                        .foodTag(ItemTags.CHICKEN_FOOD)
                        .canonicalFood(Items.WHEAT_SEEDS)
                        .build()))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry breedCows(BreedAnimalsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BREED_COWS)
                        .displayName("Breed Cows")
                        .description("Feed and breed nearby cows to grow the herd")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BREED_COWS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("cow_spawn_egg"))
                        .build())
                .factory(() -> new BreedAnimalsBehavior(config, support, BreedSpecies.builder()
                        .type(EntityType.COW)
                        .foodTag(ItemTags.COW_FOOD)
                        .canonicalFood(Items.WHEAT)
                        .build()))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry breedPigs(BreedAnimalsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BREED_PIGS)
                        .displayName("Breed Pigs")
                        .description("Feed and breed nearby pigs to grow the herd")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BREED_PIGS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("pig_spawn_egg"))
                        .build())
                .factory(() -> new BreedAnimalsBehavior(config, support, BreedSpecies.builder()
                        .type(EntityType.PIG)
                        .foodTag(ItemTags.PIG_FOOD)
                        .canonicalFood(Items.CARROT)
                        .build()))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry breedSheep(BreedAnimalsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BREED_SHEEP)
                        .displayName("Breed Sheep")
                        .description("Feed and breed nearby sheep to grow the herd")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BREED_SHEEP.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("sheep_spawn_egg"))
                        .build())
                .factory(() -> new BreedAnimalsBehavior(config, support, BreedSpecies.builder()
                        .type(EntityType.SHEEP)
                        .foodTag(ItemTags.SHEEP_FOOD)
                        .canonicalFood(Items.WHEAT)
                        .build()))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry tameWolf(TameWolfConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.TAME_WOLF)
                        .displayName("Tame Wolf")
                        .description("Offer bones to a nearby wild wolf to tame it")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.TAME_WOLF.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("bone"))
                        .build())
                .factory(() -> new TameWolfBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry tameCat(TameCatConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.TAME_CAT)
                        .displayName("Tame Cat")
                        .description("Offer raw fish to a nearby stray cat to tame it")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.TAME_CAT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("cod"))
                        .build())
                .factory(() -> new TameCatBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry shearSheep(ShearSheepConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.SHEAR_SHEEP)
                        .displayName("Shear Sheep")
                        .description("Shear wool from nearby sheep using shears")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.SHEAR_SHEEP.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("shears"))
                        .build())
                .factory(() -> new ShearSheepBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry dyeSheep(DyeSheepConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.DYE_SHEEP)
                        .displayName("Dye Sheep")
                        .description("Colors nearby sheep using carried dyes")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.DYE_SHEEP.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("red_dye"))
                        .build())
                .factory(() -> new DyeSheepBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry feedWolf(FeedWolfConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.FEED_WOLF)
                        .displayName("Feed Wolf")
                        .description("Offer meat to a nearby owned wolf to restore its health")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(10).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.FEED_WOLF.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("cooked_beef"))
                        .build())
                .factory(() -> new FeedWolfBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry washWolf(WashWolfConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.WASH_WOLF)
                        .displayName("Wash Dog")
                        .description("Wash and dry an owned dog")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(10).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.WASH_WOLF.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("water_bucket"))
                        .build())
                .factory(() -> new WashWolfBehavior(config, support))
                .build();
    }

    // Cartographer

    @Provides
    @IntoSet
    static BehaviorCatalogEntry surveyLandscape(SurveyLandscapeConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.SURVEY_LANDSCAPE)
                        .displayName("Survey Landscape")
                        .description("Travel to a distant point and survey the landscape")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.SURVEY_LANDSCAPE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("map"))
                        .build())
                .factory(() -> new SurveyLandscapeBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry chartRaiders(ChartRaidersConfig config, BehaviorSupport support) {
        ClockTicks sketch = ClockTicks.of(CartographerAnimations.MARK_DURATION_TICKS);
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.CHART_RAIDERS)
                        .displayName("Chart Raiders")
                        .description("Sketch the raid's raiders onto a map so they glow")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(sketch.asGameTicks())
                        .maxRunDuration(ClockTicks.of(sketch.getTicks() * 2))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.CHART_RAIDERS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("compass"))
                        .build())
                .factory(() -> new ChartRaidersBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry markRaidWave(MarkRaidWaveConfig config,
                                             RaidWaveMarkRegistry marks,
                                             BehaviorSupport support) {
        ClockTicks sighting = CartographerAnimations.SIGHT_DURATION;
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.MARK_RAID_WAVE)
                        .displayName("Mark Raid Wave")
                        .description("Sight the next raid wave's selected spawn point through a spyglass")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(sighting.asGameTicks())
                        .maxRunDuration(ClockTicks.of(sighting.getTicks() * 2))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.MARK_RAID_WAVE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("spyglass"))
                        .build())
                .factory(() -> new MarkRaidWaveBehavior(config, marks, support))
                .build();
    }

    // Fishing

    @Provides
    @IntoSet
    static BehaviorCatalogEntry fishing(FishingConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.FISHING)
                        .displayName("Go Fishing")
                        .description("Cast a fishing rod and wait for a catch")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.FISHING.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("fishing_rod"))
                        .build())
                .factory(() -> new FishingBehavior(config, support))
                .build();
    }

    // Crafting / Mason

    @Provides
    @IntoSet
    static BehaviorCatalogEntry cutStone(CutStoneConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.CUT_STONE)
                        .displayName("Cut Stone")
                        .description("Shape stone blocks at the stonecutter")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.JobSiteBlockOpportunity(Blocks.STONECUTTER))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.CUT_STONE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("stonecutter"))
                        .build())
                .factory(() -> new CutStoneBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry craftGoods(CraftGoodsConfig config,
                                           BehaviorSupport support,
                                           CraftCatalogRegistry craftCatalog,
                                           TradeCatalogRegistry tradeCatalog,
                                           VillagerWallet wallet) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.CRAFT_GOODS)
                        .displayName("Craft Goods")
                        .description("Turn raw materials into finished goods at the profession workstation")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.CRAFT_GOODS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("crafting_table"))
                        .build())
                .factory(() -> new CraftGoodsBehavior(config, support, craftCatalog, tradeCatalog, wallet))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry forgeTool(ForgeToolConfig config,
                                          BehaviorSupport support,
                                          ForgeCatalogRegistry forgeCatalog,
                                          TradeCatalogRegistry tradeCatalog,
                                          VillagerWallet wallet) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.FORGE_TOOL)
                        .displayName("Forge Tools")
                        .description("Forge tools at the anvil or smithing table")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.FORGE_TOOL.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("anvil"))
                        .build())
                .factory(() -> new ForgeToolBehavior(config, support, forgeCatalog, tradeCatalog, wallet))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry harvestOre(HarvestOreConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.HARVEST_ORE)
                        .displayName("Harvest Ore")
                        .description("Mine ore blocks from a nearby vein")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.ORE_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.HARVEST_ORE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("iron_ore"))
                        .build())
                .factory(() -> new HarvestOreBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry excavateSubstrate(ExcavateSubstrateConfig config,
                                                  BehaviorSupport support,
                                                  ExcavateSubstrateYieldDataManager yieldData) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.EXCAVATE_SUBSTRATE)
                        .displayName("Excavate Substrate")
                        .description("Dig surface gravel and sand for materials")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.GRAVEL_SITES, MemoryTypeRegistry.SAND_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.EXCAVATE_SUBSTRATE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("iron_shovel"))
                        .build())
                .factory(() -> new ExcavateSubstrateBehavior(config, support, yieldData))
                .build();
    }

    // Enchanting

    @Provides
    @IntoSet
    static BehaviorCatalogEntry enchantItem(EnchantItemConfig config,
                                            BehaviorSupport support,
                                            EnchantmentEngine enchantmentEngine) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.ENCHANT_ITEM)
                        .displayName("Enchant Item")
                        .description("Apply enchantments to an item at the enchanting table")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.ENCHANT_ITEM.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("enchanting_table"))
                        .build())
                .factory(() -> new EnchantItemBehavior(config, support, enchantmentEngine))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry bindVexes(BindVexesConfig config, BehaviorSupport support) {
        ClockTicks channel = BindVexesAnimations.CHANNEL_DURATION;
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BIND_VEXES)
                        .displayName("Bind Vexes")
                        .description("Channel a spell that holds and weakens nearby vexes")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(channel.asGameTicks())
                        .maxRunDuration(ClockTicks.of(channel.getTicks() * 2))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BIND_VEXES.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("enchanted_book"))
                        .build())
                .factory(() -> new BindVexesBehavior(config, support))
                .build();
    }

    // Idle / Leisure

    @Provides
    @IntoSet
    static BehaviorCatalogEntry walkDog(WalkDogConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.WALK_DOG)
                        .displayName("Walk Dog")
                        .description("Take a tamed wolf for a leisurely walk around the village")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .maxRunDuration(ClockTicks.seconds(150))
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.WALK_DOG.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("lead"))
                        .build())
                .factory(() -> new WalkDogBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry petAnimal(PetAnimalConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.PET_ANIMAL)
                        .displayName("Pet Animal")
                        .description("Walk up to a nearby wolf or cat and give it a pet")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(config.petCount()).asGameTicks())
                        .maxRunDuration(PetAnimalBehavior.MAX_RUN_DURATION)
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.PET_ANIMAL.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("bone"))
                        .build())
                .factory(() -> new PetAnimalBehavior(config, support))
                .build();
    }

    // Leatherworking

    @Provides
    @IntoSet
    static BehaviorCatalogEntry dyeLeather(DyeLeatherConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.DYE_LEATHER)
                        .displayName("Dye Leather")
                        .description("Dye leather armor on an armor stand")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.DYE_LEATHER.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("leather_chestplate"))
                        .build())
                .factory(() -> new DyeLeatherBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry washLeather(WashLeatherConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.WASH_LEATHER)
                        .displayName("Wash Leather")
                        .description("Wash leather in the cauldron")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.WASH_LEATHER.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("leather"))
                        .build())
                .factory(() -> new WashLeatherBehavior(config, support))
                .build();
    }

    // Nitwit / Mischief

    @Provides
    @IntoSet
    static BehaviorCatalogEntry ringBell(RingBellConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.RING_BELL)
                        .displayName("Ring Bell")
                        .description("Ring a nearby village bell for no productive reason")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.RING_BELL.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("bell"))
                        .build())
                .factory(() -> new RingBellBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry throwEggs(ThrowEggsConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.THROW_EGGS)
                        .displayName("Throw Eggs")
                        .description("Pelt nearby villagers, players, and wandering traders with eggs")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.THROW_EGGS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("egg"))
                        .build())
                .factory(() -> new ThrowEggsBehavior(config, support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry chaseChickens(ChaseChickensConfig config, BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.CHASE_CHICKENS)
                        .displayName("Chase Chickens")
                        .description("Chase a chicken around the village")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.CHASE_CHICKENS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("feather"))
                        .build())
                .factory(() -> new ChaseChickensBehavior(config, support))
                .build();
    }

    // Foraging

    @Provides
    @IntoSet
    static BehaviorCatalogEntry scavenge(ScavengeConfig config,
                                         BehaviorSupport support,
                                         ScavengeYieldDataManager yieldData) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.SCAVENGE)
                        .displayName("Scavenge")
                        .description("Rummage ground foliage for a small trickle of miscellany")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(config.behaviorCooldownMin(), config.behaviorCooldownMax()))
                        .interruptible(true)
                        .opportunity(new OpportunityRequirement.KnownSiteOpportunity(
                                Set.of(MemoryTypeRegistry.SCAVENGEABLE_FLORA_SITES)))
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.SCAVENGE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("short_grass"))
                        .build())
                .factory(() -> new ScavengeBehavior(config, support, yieldData))
                .build();
    }

    // ===== 机械动力整合包：建造与机械 =====

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildHouse(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_HOUSE)
                        .displayName("Build House")
                        .description("Trade for a house order, then auto-find empty land and build it")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(30, 60))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_HOUSE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("oak_planks"))
                        .build())
                .factory(() -> new BuildHouseBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry constructMachine(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_MACHINE)
                        .displayName("Construct Machine")
                        .description("Build Create machines from a machine order")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(30, 60))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_MACHINE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("anvil"))
                        .build())
                .factory(() -> new ConstructMachineBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry breakBlocks(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BREAK_BLOCKS)
                        .displayName("Break Blocks")
                        .description("Clear a region of blocks")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(15, 30))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BREAK_BLOCKS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("stone_pickaxe"))
                        .build())
                .factory(() -> new BreakBlocksBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry placeBlocks(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.PLACE_BLOCKS)
                        .displayName("Place Blocks")
                        .description("Lay a foundation platform from inventory or a creative crate")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(15, 30))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.PLACE_BLOCKS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("stone"))
                        .build())
                .factory(() -> new PlaceBlocksBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildFromBlueprint(BehaviorSupport support, BuildOrderQueue buildOrderQueue) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_FROM_BLUEPRINT)
                        .displayName("Build From Blueprint")
                        .description("Build a structure parsed from the model dialogue (player-approved blueprint)")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(20, 60))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_FROM_BLUEPRINT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("oak_planks"))
                        .build())
                .factory(() -> new BuildFromBlueprintBehavior(support, buildOrderQueue))
                .build();
    }

    // ===== 机械动力整合包（简化版）：工厂 / 扩建 / 铁路 / 飞艇 =====

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildFactory(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_FACTORY)
                        .displayName("Build Factory")
                        .description("Build a Create factory in the village")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(120).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(60, 120))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_FACTORY.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("furnace"))
                        .build())
                .factory(() -> new BuildFactoryBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry operateFactory(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.OPERATE_FACTORY)
                        .displayName("Operate Factory")
                        .description("Supervise and run nearby Create machines")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(30, 60))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.OPERATE_FACTORY.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("redstone"))
                        .build())
                .factory(() -> new OperateFactoryBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry expandVillage(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.EXPAND_VILLAGE)
                        .displayName("Expand Village")
                        .description("Build an extra house on the village edge")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(45, 90))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.EXPAND_VILLAGE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("oak_planks"))
                        .build())
                .factory(() -> new ExpandVillageBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildRailway(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_RAILWAY)
                        .displayName("Build Railway")
                        .description("Locate the nearest non-abandoned village and lay Create track to it")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(180).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(120, 240))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_RAILWAY.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("rail"))
                        .build())
                .factory(() -> new BuildRailwayBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildAircraft(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_AIRCRAFT)
                        .displayName("Build Aircraft")
                        .description("Build a Create airship/boat (requires Aeronautics)")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(120).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(90, 180))
                        .interruptible(false)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_AIRCRAFT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("white_banner"))
                        .build())
                .factory(() -> new BuildAircraftBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry operateAircraft(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.OPERATE_AIRCRAFT)
                        .displayName("Operate Aircraft")
                        .description("Super-glue, physicalize and pilot a Create airship (requires Aeronautics)")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(60, 120))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.OPERATE_AIRCRAFT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("ender_eye"))
                        .build())
                .factory(() -> new OperateAircraftBehavior(support))
                .build();
    }

    // ===== 机械动力整合包：火车 / 火车站 / 地形修补（机械师）=====

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildTrain(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_TRAIN)
                        .displayName("Build Train")
                        .description("Lay track and assemble a Create train")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(60, 120))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_TRAIN.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("minecart"))
                        .build())
                .factory(() -> new BuildTrainBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildTrainStation(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_TRAIN_STATION)
                        .displayName("Build Train Station")
                        .description("Level the ground and build a village train station with a mechanic job site")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(60).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(60, 120))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_TRAIN_STATION.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("rail"))
                        .build())
                .factory(() -> new BuildTrainStationBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry repairTerrain(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.REPAIR_TERRAIN)
                        .displayName("Repair Terrain")
                        .description("Fill pits, flatten ground and clear floating debris around the villager")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.MOVEMENT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(40, 80))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.REPAIR_TERRAIN.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("dirt"))
                        .build())
                .factory(() -> new RepairTerrainBehavior(support))
                .build();
    }

    // ===== 机械动力整合包：乡村生活（山东农村风）行为目录 =====

    @Provides
    @IntoSet
    static BehaviorCatalogEntry marryMoveIn(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.MARRY_MOVE_IN)
                        .displayName("Marry & Move In")
                        .description("Register as cohabitants with another villager sharing the same Home Setting Block")
                        .category(BehaviorCategory.SOCIAL)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.COGNITION)
                        .estimatedDuration(ClockTicks.seconds(5).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(600, 1200))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.MARRY_MOVE_IN.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("paper"))
                        .build())
                .factory(() -> new MarryMoveInBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry wakeUp(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.WAKE_UP)
                        .displayName("Wake Up")
                        .description("Get out of bed in the morning")
                        .category(BehaviorCategory.SELF_CARE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(10).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(60, 120))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.WAKE_UP.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("clock"))
                        .build())
                .factory(() -> new WakeUpBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry useBathroom(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.USE_BATHROOM)
                        .displayName("Use Bathroom")
                        .description("Brush teeth and wash face at the home bathroom")
                        .category(BehaviorCategory.SELF_CARE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(120, 240))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.USE_BATHROOM.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("water_bucket"))
                        .build())
                .factory(() -> new BathroomBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry cookMeal(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.COOK_MEAL)
                        .displayName("Cook Meal")
                        .description("Cook at the kitchen; chimney emits cooking smoke")
                        .category(BehaviorCategory.SELF_CARE)
                        .intensity(WorkIntensity.LIGHT)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(25).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(120, 300))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.COOK_MEAL.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("campfire"))
                        .build())
                .factory(() -> new CookMealBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry eatAtTable(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.EAT_AT_TABLE)
                        .displayName("Eat at Table")
                        .description("Carry food to the table and eat")
                        .category(BehaviorCategory.SELF_CARE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(120, 300))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.EAT_AT_TABLE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("bread"))
                        .build())
                .factory(() -> new EatMealBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry sleepHome(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.SLEEP_HOME)
                        .displayName("Sleep at Home")
                        .description("Return home to bed and turn off the redstone lamp")
                        .category(BehaviorCategory.SELF_CARE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(60, 120))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.SLEEP_HOME.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("red_bed"))
                        .build())
                .factory(() -> new SleepHomeBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry drinkTea(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.DRINK_TEA)
                        .displayName("Drink Tea")
                        .description("Sit in the courtyard and drink tea when idle")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(30).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(300, 600))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.DRINK_TEA.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("potion"))
                        .build())
                .factory(() -> new DrinkTeaBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry useFan(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.USE_FAN)
                        .displayName("Fan")
                        .description("Fan oneself to cool off when idle")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.seconds(25).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(300, 600))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.USE_FAN.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("feather"))
                        .build())
                .factory(() -> new FanBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry playCards(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.PLAY_CARDS)
                        .displayName("Play Cards")
                        .description("Gather with other villagers to play cards; the winner keeps jumping")
                        .category(BehaviorCategory.LEISURE)
                        .intensity(WorkIntensity.NONE)
                        .requiredChannel(BehaviorChannel.SOCIAL)
                        .estimatedDuration(ClockTicks.seconds(40).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(300, 600))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.PLAY_CARDS.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("paper"))
                        .build())
                .factory(() -> new PlayCardsBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildCityWall(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_CITY_WALL)
                        .displayName("Build City Wall")
                        .description("Build a Create-styled city wall ring around the village")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.minutes(10).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(600, 1200))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_CITY_WALL.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("bricks"))
                        .build())
                .factory(() -> new BuildCityWallBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry remodelHouse(BehaviorSupport support) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.REMODEL_HOUSE)
                        .displayName("Remodel House")
                        .description("Rebuild a house in Shandong-rural style (courtyard / two-story / workshop)")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.minutes(8).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(600, 1200))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.REMODEL_HOUSE.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("brick"))
                        .build())
                .factory(() -> new RemodelHouseBehavior(support))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry scaleSettlement(BehaviorSupport support,
                                               SettlementScalingService scalingService,
                                               SettlementStore settlementStore,
                                               BuildOrderQueue buildOrderQueue) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.SCALE_SETTLEMENT)
                        .displayName("Scale Settlement")
                        .description("Auto-rebuild a village into a town (model houses, car factory, buses) "
                                + "or a town into a city (urban blocks, subway) when population crosses a tier")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.minutes(20).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(1800, 3600))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.SCALE_SETTLEMENT.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("oak_door"))
                        .build())
                .factory(() -> new ScaleSettlementBehavior(support, scalingService, settlementStore, buildOrderQueue))
                .build();
    }

    @Provides
    @IntoSet
    static BehaviorCatalogEntry buildSubway(BehaviorSupport support,
                                           SettlementScalingService scalingService,
                                           SettlementStore settlementStore) {
        return BehaviorCatalogEntry.builder()
                .descriptor(BehaviorPlanningMetadata.builder()
                        .key(BehaviorKey.BUILD_SUBWAY)
                        .displayName("Build Subway")
                        .description("Carve an underground tunnel and lay powered rails for a city subway")
                        .category(BehaviorCategory.WORK)
                        .intensity(WorkIntensity.HEAVY)
                        .requiredChannel(BehaviorChannel.INTERACTION)
                        .estimatedDuration(ClockTicks.minutes(15).asGameTicks())
                        .cooldown(CooldownRange.ofSeconds(3600, 7200))
                        .interruptible(true)
                        .build())
                .displayInfo(BehaviorDisplayMetadata.builder()
                        .displayNameKey(BehaviorKey.BUILD_SUBWAY.displayNameKey())
                        .iconItemId(ResourceLocation.withDefaultNamespace("powered_rail"))
                        .build())
                .factory(() -> new BuildSubwayBehavior(support, scalingService, settlementStore))
                .build();
    }

}
