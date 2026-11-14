package dev.breezes.settlements.infrastructure.minecraft.entities.transit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 小镇公交车实体：在两个站点之间往返巡逻的简易载具实体。
 * <p>
 * 设计为轻量、可编译的雏形：位置在两个端点之间正弦往返，作为「小镇开始会有公交车」的可视化载体。
 * 后续可接 Create 的列车/动力系统做真实轨道公交，此处仅保证实体可生成、可移动、可存档。
 */
public class BusEntity extends Entity {

    private static final double SPAN = 16.0;
    private static final double SPEED = 0.02;

    private double anchorX;
    private double anchorZ;
    private int age;

    public BusEntity(@Nonnull EntityType<BusEntity> type, @Nonnull Level level) {
        super(type, level);
        this.anchorX = this.getX();
        this.anchorZ = this.getZ();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // 无自定义同步数据
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
        this.anchorX = tag.getDouble("anchorX");
        this.anchorZ = tag.getDouble("anchorZ");
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
        tag.putDouble("anchorX", this.anchorX);
        tag.putDouble("anchorZ", this.anchorZ);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        // 首次 tick 时以当前出生点为锚点。
        if (this.age == 0) {
            this.anchorX = this.getX();
            this.anchorZ = this.getZ();
        }
        this.age++;
        // 在锚点附近沿 X 轴正弦往返（简易公交巡逻）。
        double x = this.anchorX + Math.sin(this.age * SPEED) * SPAN;
        double z = this.anchorZ + Math.cos(this.age * SPEED) * (SPAN * 0.4);
        this.setPos(x, this.getY(), z);
        this.setYRot((float) (this.age * SPEED * 180.0 / Math.PI));
    }
}
