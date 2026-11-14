package dev.breezes.settlements.compat.create;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import dev.breezes.settlements.compat.create.CreateBlockRefs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;

/**
 * 机械动力「蓝图大炮」（Schematicannon）建造驱动器。
 * <p>
 * 流程：放置蓝图大炮 → 装入 .nbt 蓝图（Create 的 schematic 物品）→ 供给火药 → 触发打印。
 * 打印过程中大炮会按蓝图自动放置/破坏方块，实现「导入机械动力蓝图让他去造」。
 * <p>
 * 注意：SchematicannonBlockEntity 的具体方法（装填蓝图、起印）随 Create 版本略有差异，
 * 下方 {@link #startPrinting} 中的 BE 调用请在你的 Create 源码里核对方法名后微调，
 * 其余（放置方块、判定大炮）已用稳定 API 实现。
 */
public final class SchematicannonBlueprintBuilder {

    private SchematicannonBlueprintBuilder() {
    }

    /** 在目标位置放置一台蓝图大炮，并回填方块状态。 */
    public static boolean placeSchematicannon(Level level, BlockPos pos) {
        if (!CreateCompat.isLoaded()) {
            return false;
        }
        Block cannon = CreateBlockRefs.schematicannon();
        if (cannon == null) {
            return false;
        }
        level.setBlock(pos, cannon.defaultBlockState(), 3);
        return level.getBlockState(pos).is(cannon);
    }

    /**
     * 给已放置的蓝图大炮装填蓝图并起印。
     *
     * @param schematic 蓝图物品（通常为 Create 的 schematic 物品，可来自资源包或玩家给予）
     * @return 是否成功触发
     */
    public static boolean startPrinting(Level level, BlockPos cannonPos, ItemStack schematic) {
        if (!CreateCompat.isLoaded()) {
            return false;
        }
        BlockEntity be = level.getBlockEntity(cannonPos);
        if (!(be instanceof SchematicannonBlockEntity cannon)) {
            return false;
        }
        // ---- 以下 BE 方法需对照你本地 Create 源码确认 ----
        // 典型实现（Create 6.x）：
        //   cannon.getInventory().setStackInSlot(0, schematic);   // 装入蓝图
        //   cannon.getInventory().setStackInSlot(1, new ItemStack(Items.GUNPOWDER, 64)); // 火药
        //   cannon.activate(); // 或 cannon.startPrinting();
        throw new NotImplementedException(
                "SchematicannonBlockEntity 的装填/起印方法需对照本地 Create 源码实现："
                        + "装入 schematic 物品 + 火药，并调用起印方法。");
    }

    /** 创建一份空白蓝图物品栈（占位，用于调试/后续从资源包读取）。 */
    public static ItemStack blankSchematic() {
        try {
            return AllItems.SCHEMATIC.asStack();
        } catch (Throwable t) {
            return ItemStack.EMPTY;
        }
    }

    /** 判定某位置是否为蓝图大炮。 */
    public static boolean isSchematicannon(@Nullable BlockState state) {
        Block cannon = CreateBlockRefs.schematicannon();
        return cannon != null && state != null && state.is(cannon);
    }
}
