package dev.breezes.settlements.infrastructure.minecraft.blocks.furniture;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nonnull;

/**
 * 通用家具方块：折叠椅、折叠桌、茶具、扇子、扑克牌共用一个实现，靠 {@link Kind} 区分外观与碰撞箱。
 * <p>
 * 这些是「可放置的装饰/交互道具」：村民的喝茶、扇扇子、打牌等行为会寻找院子里已放置的对应方块
 * 并走到其附近执行动作；方块本身不绑定复杂逻辑，便于玩家自由布置。
 * <p>
 * 注意：codec 重建时使用默认全方块外形（datagen/结构序列化路径），运行期外形由 {@link Kind} 决定。
 */
public class FurnitureBlock extends Block {

    public static final MapCodec<FurnitureBlock> CODEC = simpleCodec(FurnitureBlock::new);

    public enum Kind {
        FOLDING_CHAIR(box(4.0, 0.0, 4.0, 12.0, 10.0, 12.0)),
        FOLDING_TABLE(box(1.0, 0.0, 1.0, 15.0, 9.0, 15.0)),
        TEA_SET(box(5.0, 0.0, 5.0, 11.0, 4.0, 11.0)),
        FAN(box(6.0, 0.0, 6.0, 10.0, 14.0, 10.0)),
        PLAYING_CARDS(box(6.0, 0.0, 6.0, 10.0, 1.0, 10.0));

        private final VoxelShape shape;

        Kind(@Nonnull VoxelShape shape) {
            this.shape = shape;
        }
    }

    private final Kind kind;

    public FurnitureBlock(@Nonnull BlockBehaviour.Properties properties) {
        this(properties, Kind.FOLDING_CHAIR);
    }

    public FurnitureBlock(@Nonnull BlockBehaviour.Properties properties, @Nonnull Kind kind) {
        super(properties);
        this.kind = kind;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected @Nonnull VoxelShape getShape(@Nonnull BlockState state,
                                           @Nonnull BlockGetter level,
                                           @Nonnull BlockPos pos,
                                           @Nonnull CollisionContext context) {
        return this.kind.shape;
    }

    @Override
    protected @Nonnull VoxelShape getCollisionShape(@Nonnull BlockState state,
                                                   @Nonnull BlockGetter level,
                                                   @Nonnull BlockPos pos,
                                                   @Nonnull CollisionContext context) {
        // 茶具/扑克牌/扇子可踩踏（不挡路）；桌椅仍保留碰撞。
        if (this.kind == Kind.TEA_SET || this.kind == Kind.PLAYING_CARDS || this.kind == Kind.FAN) {
            return Shapes.empty();
        }
        return this.kind.shape;
    }
}
