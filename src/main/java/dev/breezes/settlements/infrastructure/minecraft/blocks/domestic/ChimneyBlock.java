package dev.breezes.settlements.infrastructure.minecraft.blocks.domestic;

import com.mojang.serialization.MapCodec;
import dev.breezes.settlements.bootstrap.registry.blockentities.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 烟囱方块：厨房灶台之上的排烟口。其方块实体 {@link ChimneyBlockEntity} 会持续从顶部冒出炊烟，
 * 并在「正在做饭」时加大浓度。配合 {@code CookMealBehavior}（村民做饭时点亮并触发升烟）。
 */
public class ChimneyBlock extends BaseEntityBlock {

    public static final MapCodec<ChimneyBlock> CODEC = simpleCodec(ChimneyBlock::new);

    /** 烟囱口（顶部）的碰撞/外形：细柱。 */
    private static final VoxelShape SHAPE = box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);

    public ChimneyBlock(@Nonnull BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ChimneyBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return BlockEntityTypeRegistry.CHIMNEY.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level,
                                                                  @Nonnull BlockState state,
                                                                  @Nonnull BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityTypeRegistry.CHIMNEY.get(), ChimneyBlockEntity::serverTick);
    }

    @Override
    @Nonnull
    protected VoxelShape getShape(@Nonnull BlockState state,
                                  @Nonnull BlockGetter level,
                                  @Nonnull BlockPos pos,
                                  @Nonnull CollisionContext context) {
        return SHAPE;
    }

    @Override
    @Nonnull
    protected VoxelShape getCollisionShape(@Nonnull BlockState state,
                                           @Nonnull BlockGetter level,
                                           @Nonnull BlockPos pos,
                                           @Nonnull CollisionContext context) {
        return SHAPE;
    }
}
