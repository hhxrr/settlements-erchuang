package dev.breezes.settlements.domain.entities;

import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

/**
 * Domain-layer identity for a villager profession: its full registry id, namespace included, so professions from
 * different mods that share a path stay distinct.
 * <p>
 * Never import VillagerProfession in domain code.
 */
public record VillagerProfessionKey(ResourceLocation id) {

    // Vanilla professions
    public static final VillagerProfessionKey NONE = vanilla("none");
    public static final VillagerProfessionKey ARMORER = vanilla("armorer");
    public static final VillagerProfessionKey BUTCHER = vanilla("butcher");
    public static final VillagerProfessionKey CARTOGRAPHER = vanilla("cartographer");
    public static final VillagerProfessionKey CLERIC = vanilla("cleric");
    public static final VillagerProfessionKey FARMER = vanilla("farmer");
    public static final VillagerProfessionKey FISHERMAN = vanilla("fisherman");
    public static final VillagerProfessionKey FLETCHER = vanilla("fletcher");
    public static final VillagerProfessionKey LEATHERWORKER = vanilla("leatherworker");
    public static final VillagerProfessionKey LIBRARIAN = vanilla("librarian");
    public static final VillagerProfessionKey MASON = vanilla("mason");
    public static final VillagerProfessionKey NITWIT = vanilla("nitwit");
    public static final VillagerProfessionKey SHEPHERD = vanilla("shepherd");
    public static final VillagerProfessionKey TOOLSMITH = vanilla("toolsmith");
    public static final VillagerProfessionKey WEAPONSMITH = vanilla("weaponsmith");

    // Custom Settlements professions
    // Add constants here as new professions are introduced, under the settlements namespace and the name used during
    // NeoForge registry registration.

    // 机械动力整合包新增职业（共 3 个，Create 为必须依赖，全部始终注册）
    // - 建筑师(ARCHITECT)：建造房屋、扩建村庄、修建铁路、建火车站、搭建并操控机械动力飞艇/船（需航空学）
    // - 工厂工人(FACTORY_WORKER)：建造机械动力的机械、建造并运转工厂
    // - 机械师(MECHANIC)：建造并驾驶机械动力火车、建造火车站、地形修补
    public static final VillagerProfessionKey ARCHITECT = settlements("architect");
    public static final VillagerProfessionKey FACTORY_WORKER = settlements("factory_worker");
    public static final VillagerProfessionKey MECHANIC = settlements("mechanic");

    public VillagerProfessionKey {
        Validate.notNull(id, "id must not be null");
    }

    private static VillagerProfessionKey vanilla(@Nonnull String path) {
        return new VillagerProfessionKey(ResourceLocation.withDefaultNamespace(path));
    }

    private static VillagerProfessionKey settlements(@Nonnull String path) {
        return new VillagerProfessionKey(ResourceLocation.fromNamespaceAndPath("settlements", path));
    }

}
