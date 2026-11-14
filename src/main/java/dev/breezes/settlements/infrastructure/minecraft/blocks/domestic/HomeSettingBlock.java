package dev.breezes.settlements.infrastructure.minecraft.blocks.domestic;

import com.mojang.serialization.MapCodec;
import dev.breezes.settlements.bootstrap.registry.blockentities.BlockEntityTypeRegistry;
import dev.breezes.settlements.bootstrap.registry.attachments.AttachmentRegistry;
import dev.breezes.settlements.infrastructure.minecraft.entities.villager.BaseVillager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * 「家设置块」：玩家<b>蹲下 + 右键</b>把附近（注视方向最近）的村民登记为该家的成员，
 * 再次对同一个人操作则移除。被登记者会写入 {@code VILLAGER_HOME} 附件（指向本块坐标），
 * 从而入住、并在结婚后与其配偶共享同一家。
 * <p>
 * 这是「村民结婚后可搬到一起住」的玩家驱动入口：把一对村民都登记到同一块家设置块下即可。
 */
public class HomeSettingBlock extends BaseEntityBlock {

    public static final MapCodec<HomeSettingBlock> CODEC = simpleCodec(HomeSettingBlock::new);

    public HomeSettingBlock(@Nonnull BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<HomeSettingBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return BlockEntityTypeRegistry.HOME_SETTING.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level,
                                                                  @Nonnull BlockState state,
                                                                  @Nonnull BlockEntityType<T> type) {
        // 家设置块自身无需每 tick 逻辑；保留挂钩以便后续扩展（如家谱显示）。
        return createTickerHelper(type, BlockEntityTypeRegistry.HOME_SETTING.get(), HomeSettingBlockEntity::serverTick);
    }

    @Nonnull
    public InteractionResult use(@Nonnull BlockState state,
                                 @Nonnull Level level,
                                 @Nonnull BlockPos pos,
                                 @Nonnull Player player,
                                 @Nonnull InteractionHand hand,
                                 @Nonnull BlockHitResult hit) {
        // 仅服务器处理登记逻辑
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 必须蹲下右键才登记家庭成员
        if (!player.isShiftKeyDown()) {
            player.sendSystemMessage(Component.literal("§e家设置块：蹲下右键可把注视的村民登记为本家成员；手持命名牌蹲下右键可给他改名。"));
            return InteractionResult.SUCCESS;
        }

        // 手持【已命名的命名牌】蹲下右键 → 给注视的村民改名（交易菜单改名的玩家驱动入口）
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.NAME_TAG) && held.has(DataComponents.CUSTOM_NAME)) {
            BaseVillager renameTarget = pickTargetVillager(level, player, pos);
            if (renameTarget == null) {
                player.sendSystemMessage(Component.literal("§c附近没有可改名的村民。"));
                return InteractionResult.SUCCESS;
            }
            renameTarget.setCustomName(Component.literal(held.getHoverName().getString()));
            player.sendSystemMessage(Component.literal("§a已将 " + renameTarget.getName().getString() + " 改名为 " + held.getHoverName().getString()));
            return InteractionResult.SUCCESS;
        }

        BaseVillager target = pickTargetVillager(level, player, pos);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§c附近没有可登记的村民。"));
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof HomeSettingBlockEntity home)) {
            return InteractionResult.SUCCESS;
        }

        UUID id = target.getUUID();
        if (home.isMember(id)) {
            home.removeMember(id);
            target.removeData(AttachmentRegistry.VILLAGER_HOME);
            player.sendSystemMessage(Component.literal("§c已将 " + target.getName().getString() + " 移出本家。"));
        } else {
            home.addMember(id);
            target.setData(AttachmentRegistry.VILLAGER_HOME,
                    GlobalPos.of(level.dimension(), pos));
            player.sendSystemMessage(Component.literal("§a已将 " + target.getName().getString() + " 登记为本家成员。"));
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 选取玩家注视方向最近、且在本块周边范围内的村民。
     */
    @Nullable
    private static BaseVillager pickTargetVillager(@Nonnull Level level, @Nonnull Player player, @Nonnull BlockPos pos) {
        Vec3 look = player.getLookAngle();
        Vec3 eye = player.getEyePosition();
        List<BaseVillager> nearby = level.getEntitiesOfClass(BaseVillager.class,
                new AABB(pos).inflate(8.0));
        BaseVillager best = null;
        double bestScore = Double.MAX_VALUE;
        for (BaseVillager v : nearby) {
            Vec3 toV = v.getEyePosition().subtract(eye);
            double dist = toV.length();
            if (dist > 8.0) {
                continue;
            }
            // 与注视方向夹角越小、距离越近，得分越低
            double dot = toV.normalize().dot(look.normalize());
            double score = dist * (1.0 - Math.max(0.0, dot) * 0.9);
            if (score < bestScore) {
                bestScore = score;
                best = v;
            }
        }
        return best;
    }

    @Override
    @Nonnull
    public InteractionResult useWithoutItem(@Nonnull BlockState state,
                                            @Nonnull Level level,
                                            @Nonnull BlockPos pos,
                                            @Nonnull Player player,
                                            @Nonnull BlockHitResult hit) {
        return this.use(state, level, pos, player, InteractionHand.MAIN_HAND, hit);
    }
}
