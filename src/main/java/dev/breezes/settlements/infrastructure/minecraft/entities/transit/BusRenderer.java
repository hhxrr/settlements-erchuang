package dev.breezes.settlements.infrastructure.minecraft.entities.transit;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

/**
 * 公交车实体渲染器：用一块拉长的蓝色混凝土方块表示车厢，俯视时清晰可见。
 * <p>
 * 之前 {@link BusEntity} 只注册了实体类型而没有客户端渲染器，一旦小镇生成公交车，
 * 客户端在 {@code EntityRenderDispatcher.shouldRender} 取到 null renderer 直接崩溃。
 * 这里补齐注册，使「小镇开始会有公交车」在实机里真正可见。
 */
public class BusRenderer extends EntityRenderer<BusEntity> {

    public BusRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BusEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/block/blue_concrete.png");
    }

    @Override
    public void render(BusEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        // 车厢：横向 1.4 格、纵向 2.6 格，以实体位置为中心。
        poseStack.scale(1.4F, 1.0F, 2.6F);
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                Blocks.BLUE_CONCRETE.defaultBlockState(), poseStack, buffer,
                packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
