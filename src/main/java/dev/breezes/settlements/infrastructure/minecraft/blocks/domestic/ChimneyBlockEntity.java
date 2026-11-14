package dev.breezes.settlements.infrastructure.minecraft.blocks.domestic;

import dev.breezes.settlements.bootstrap.registry.particles.ParticleRegistry;
import dev.breezes.settlements.domain.world.location.Location;
import dev.breezes.settlements.domain.time.ClockTicks;
import dev.breezes.settlements.domain.time.Tickable;
import dev.breezes.settlements.bootstrap.registry.blockentities.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 烟囱方块实体：每 ~1.5 秒从顶部冒出一次炊烟。
 * <ul>
 *   <li>默认（闲置）：极淡的炊烟，让村屋看起来「有人气」；</li>
 *   <li>做饭时（{@link #setCooking} 置 true）：炊烟浓度提升，表现「厨房升起炊烟」。</li>
 * </ul>
 * 做饭标记由 {@code CookMealBehavior} 在村民到达厨房后设置，并在离开后清除。
 */
public class ChimneyBlockEntity extends BlockEntity {

    private static final String NBT_COOKING = "cooking";
    private static final ClockTicks SMOKE_INTERVAL = ClockTicks.seconds(1.5);

    private boolean cooking = false;
    private final Tickable smokeTimer = SMOKE_INTERVAL.asTickable();
    private final Random random = new Random();

    public ChimneyBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(BlockEntityTypeRegistry.CHIMNEY.get(), pos, state);
    }

    public void setCooking(boolean cooking) {
        this.cooking = cooking;
    }

    public boolean isCooking() {
        return this.cooking;
    }

    public static void serverTick(@Nonnull net.minecraft.world.level.Level level,
                                  @Nonnull BlockPos pos,
                                  @Nonnull BlockState state,
                                  @Nonnull ChimneyBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }
        // 仅在已加载且附近有玩家时冒烟，避免无谓开销
        if (!blockEntity.smokeTimer.tickAndCheck(1)) {
            return;
        }
        double strength = blockEntity.cooking ? 1.0 : 0.35;
        Location top = Location.of(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, level);
        ParticleRegistry.cookingSmoke(top, strength);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean(NBT_COOKING, this.cooking);
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.cooking = tag.getBoolean(NBT_COOKING);
    }
}
