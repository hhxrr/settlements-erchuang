package dev.breezes.settlements.infrastructure.minecraft.items;

import dev.breezes.settlements.bootstrap.registry.items.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 建房订单物品：玩家在交易菜单用绿宝石购买后，村民背包里出现此物，
 * 随后由对应的建造类行为消费并启动对应工程。
 * <p>
 * 物品 NBT 编码：
 * <ul>
 *   <li>{@code planType}：0=手搓房屋(HOUSE)，1=机械(MACHINE)，2=拆除(DEMOLISH)，
 *       3=工厂(FACTORY)，4=铁路(RAILWAY)，5=飞艇/船(AIRCRAFT)，6=扩建村庄(VILLAGE_EXPAND)，
 *       7=火车(TRAIN)，8=火车站(TRAIN_STATION)，9=地形修补(TERRAIN)</li>
 *   <li>{@code costInBlocks}：计划方块数（绿宝石计价依据，1 方块 = 1 绿宝石）</li>
 * </ul>
 */
public class BuildOrderItem extends Item {

    public static final String TAG_PLAN_TYPE = "settlements.plan_type";
    public static final String TAG_COST = "settlements.cost_blocks";

    /** 计划类型枚举。 */
    public enum PlanType {
        HOUSE(0),
        MACHINE(1),
        DEMOLISH(2),
        FACTORY(3),
        RAILWAY(4),
        AIRCRAFT(5),
        VILLAGE_EXPAND(6),
        TRAIN(7),
        TRAIN_STATION(8),
        TERRAIN(9);

        public final int id;

        PlanType(int id) {
            this.id = id;
        }

        @Nonnull
        public static PlanType fromId(int id) {
            for (PlanType t : values()) {
                if (t.id == id) {
                    return t;
                }
            }
            return HOUSE;
        }
    }

    public BuildOrderItem(@Nonnull Item.Properties properties) {
        super(properties);
    }

    @Nonnull
    public static ItemStack create(@Nonnull PlanType type, int costInBlocks) {
        ItemStack stack = new ItemStack(ItemRegistry.BUILD_ORDER.get());
        CompoundTag tag = new CompoundTag();
        tag.putInt(TAG_PLAN_TYPE, type.id);
        tag.putInt(TAG_COST, costInBlocks);
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        return stack;
    }

    @Nonnull
    public static PlanType getPlanType(@Nullable ItemStack stack) {
        CompoundTag tag = orderTag(stack);
        return tag == null ? PlanType.HOUSE : PlanType.fromId(tag.getInt(TAG_PLAN_TYPE));
    }

    public static int getCost(@Nullable ItemStack stack) {
        CompoundTag tag = orderTag(stack);
        return tag == null ? 0 : tag.getInt(TAG_COST);
    }

    @Nullable
    private static CompoundTag orderTag(@Nullable ItemStack stack) {
        if (stack == null) { return null; }
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    public static boolean isOrder(@Nullable ItemStack stack) {
        return stack != null && stack.getItem() instanceof BuildOrderItem;
    }
}
