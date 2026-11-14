package dev.breezes.settlements.infrastructure.minecraft.blocks.domestic;

import dev.breezes.settlements.bootstrap.registry.blockentities.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 家设置块的方块实体：持久化「本家成员」列表（村民 UUID），并提供登记查询。
 * 实际入住逻辑（写入村民的 {@code VILLAGER_HOME} 附件）由 {@link HomeSettingBlock#use} 完成，
 * 这里只负责存储与同步，方便行为系统（如结婚同居、回家睡觉）按家聚合村民。
 */
public class HomeSettingBlockEntity extends BlockEntity {

    private static final String NBT_MEMBERS = "members";

    private final List<UUID> members = new ArrayList<>();

    public HomeSettingBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(BlockEntityTypeRegistry.HOME_SETTING.get(), pos, state);
    }

    public boolean isMember(@Nonnull UUID id) {
        return this.members.contains(id);
    }

    public void addMember(@Nonnull UUID id) {
        if (!this.members.contains(id)) {
            this.members.add(id);
            this.setChangedAndSync();
        }
    }

    public void removeMember(@Nonnull UUID id) {
        if (this.members.remove(id)) {
            this.setChangedAndSync();
        }
    }

    @Nonnull
    public List<UUID> getMembers() {
        return List.copyOf(this.members);
    }

    public int memberCount() {
        return this.members.size();
    }

    /**
     * 由结婚系统调用：把配偶双方写入同一家（若其中一方已登记到某家，则把另一方并入）。
     */
    public void addMembers(@Nonnull List<UUID> ids) {
        boolean changed = false;
        for (UUID id : ids) {
            if (!this.members.contains(id)) {
                this.members.add(id);
                changed = true;
            }
        }
        if (changed) {
            this.setChangedAndSync();
        }
    }

    private void setChangedAndSync() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (UUID id : this.members) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("id", id);
            list.add(entry);
        }
        tag.put(NBT_MEMBERS, list);
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.members.clear();
        if (tag.contains(NBT_MEMBERS, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_MEMBERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                if (entry.hasUUID("id")) {
                    this.members.add(entry.getUUID("id"));
                }
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @Nonnull CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    public static void serverTick(@Nonnull Level level,
                                  @Nonnull BlockPos pos,
                                  @Nonnull BlockState state,
                                  @Nonnull HomeSettingBlockEntity blockEntity) {
        // 当前无需每 tick 逻辑；保留以便后续按家聚合（如家庭事件）。
    }
}
