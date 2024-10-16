package com.github.tartaricacid.touhoulittlemaid.client.renderer.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityPowerPoint;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class EntityPowerPointRenderer extends EntityRenderer<EntityPowerPoint> {
    private static final ResourceLocation POWER_POINT_TEXTURES = ResourceLocation.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/entity/power_point.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(POWER_POINT_TEXTURES);

    public EntityPowerPointRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    private static void vertex(VertexConsumer bufferIn, Matrix4f pose, Matrix3f normal, double x, double y, double texU, double texV, int packedLight) {
        Vector3f vector3f = normal.transform(new Vector3f(0.0F, 1.0F, 0.0F));
        bufferIn.addVertex(pose, (float) x, (float) y, 0.0F).setColor(255, 255, 255, 128).setUv((float) texU, (float) texV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(vector3f.x(), vector3f.y(), vector3f.z());
    }

    @Override
    public void render(EntityPowerPoint entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        int texIndex = entity.getIcon();
        int remainder = texIndex % 4;
        int quotient = texIndex / 4;
        double texPos1 = remainder * 16 / 64.0;
        double texPos2 = (remainder * 16 + 16) / 64.0;
        double texPos3 = quotient * 16 / 64.0;
        double texPos4 = (quotient * 16 + 16) / 64.0;

        poseStack.pushPose();
        poseStack.translate(0, 0.1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.3F, 0.3F, 0.3F);

        VertexConsumer buffer = bufferIn.getBuffer(RENDER_TYPE);
        PoseStack.Pose poseStackLast = poseStack.last();
        Matrix4f pose = poseStackLast.pose();
        Matrix3f normal = poseStackLast.normal();

        vertex(buffer, pose, normal, -1, -0.25, texPos1, texPos4, packedLightIn);
        vertex(buffer, pose, normal, 1, -0.25, texPos2, texPos4, packedLightIn);
        vertex(buffer, pose, normal, 1, 1.75, texPos2, texPos3, packedLightIn);
        vertex(buffer, pose, normal, -1, 1.75, texPos1, texPos3, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityPowerPoint entity) {
        return POWER_POINT_TEXTURES;
    }
}
