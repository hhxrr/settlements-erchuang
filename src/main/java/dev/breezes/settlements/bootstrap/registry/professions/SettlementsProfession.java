package dev.breezes.settlements.bootstrap.registry.professions;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * 机械动力整合包新增职业的构造工具。
 * <p>
 * Minecraft 1.21.1 中 {@link VillagerProfession} 是 {@code final record}，既不能继承，
 * 构造器签名也从旧版的 5 参数变为 6 参数（两个 {@code Predicate<Holder<PoiType>>}）。
 * 因此这里不再使用子类，改为静态工厂直接构造 {@link VillagerProfession}，
 * 并把 POI 的 {@link ResourceKey} 转成 {@code Holder::is} 谓词。
 */
public final class SettlementsProfession {

    private SettlementsProfession() {
    }

    public static VillagerProfession create(@Nullable String name,
                                            ResourceKey<PoiType> poi,
                                            ImmutableSet<Item> specificItems,
                                            ImmutableSet<Block> secondaryPoi,
                                            @Nullable SoundEvent soundEvent) {
        Predicate<Holder<PoiType>> site = holder -> poi != null && holder.is(poi);
        return new VillagerProfession(name, site, site, specificItems, secondaryPoi, soundEvent);
    }
}
