package com.blamejared.clumps.client.render;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderXPOrbBig extends EntityRenderer<EntityXPOrbBig> {
    
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.getEntityTranslucent(EXPERIENCE_ORB_TEXTURES);
    
    public RenderXPOrbBig(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }
    
    @Override
    public boolean shouldRender(EntityXPOrbBig livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return true;
    }
    
    protected int getBlockLight(EntityXPOrbBig entityIn, float partialTicks) {
        return MathHelper.clamp(super.getBlockLight(entityIn, partialTicks) + 7, 0, 15);
    }
    
    @Override
    public void render(EntityXPOrbBig entity, float entityYaw, float partialTicks, @Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer buffer, int packedLightIn) {
        matrix.push();
        int texIndex = entity.getTextureByXP();
        float minX = (float) (texIndex % 4 * 16) / 64.0F;
        float maxX = (float) (texIndex % 4 * 16 + 16) / 64.0F;
        float minY = (float) (texIndex / 4 * 16) / 64.0F;
        float maxY = (float) (texIndex / 4 * 16 + 16) / 64.0F;
        
        int red;
        int green;
        int blue;
        float f9 = ((float) entity.xpColor + partialTicks) / 2.0F;
        red = (int) ((MathHelper.sin(f9 + 0.0F) + 1.0F) * 0.5F * 255.0F);
        green = 255;
        blue = 0xFFFFFF;
        // More custom colours, requires red, green and blue to be floats to work
        //                int startRed = 0;
        //                int startGreen = 102;
        //                int startBlue = 255;
        //
        //                int endRed = 20;
        //                int endGreen = 204;
        //                int endBlue = 255;
        //
        //                // 255, 102, 0
        //                float ratio = (MathHelper.sin((entity.xpColor)) + 1) / 2;
        //                red = (int) Math.abs((ratio * startRed) + ((1 - ratio) * endRed));
        //                green = (int) Math.abs((ratio * startGreen) + ((1 - ratio) * endGreen));
        //                blue = (int) Math.abs((ratio * startBlue) + ((1 - ratio) * endBlue));
        
        
        matrix.translate(0.0D, (double) 0.1F, 0.0D);
        matrix.rotate(this.renderManager.getCameraOrientation());
        matrix.rotate(Vector3f.YP.rotationDegrees(180.0F));
        matrix.scale(0.3F, 0.3F, 0.3F);
        
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RENDER_TYPE);
        MatrixStack.Entry matrixstack$entry = matrix.getLast();
        Matrix4f matrix4f = matrixstack$entry.getMatrix();
        Matrix3f matrix3f = matrixstack$entry.getNormal();
        
        vertex(ivertexbuilder, matrix4f, matrix3f, -1, -1F, red, green, blue, minX, maxY, packedLightIn);
        vertex(ivertexbuilder, matrix4f, matrix3f, +1, -1F, red, green, blue, maxX, maxY, packedLightIn);
        vertex(ivertexbuilder, matrix4f, matrix3f, +1, +1F, red, green, blue, maxX, minY, packedLightIn);
        vertex(ivertexbuilder, matrix4f, matrix3f, -1, +1F, red, green, blue, minX, minY, packedLightIn);
        
        matrix.pop();
    }
    
    private static void vertex(IVertexBuilder bufferIn, Matrix4f matrixIn, Matrix3f matrixNormalIn, float x, float y, int red, int green, int blue, float texU, float texV, int packedLight) {
        bufferIn.pos(matrixIn, x, y, 0.0F).color(red, green, blue, 128).tex(texU, texV).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(matrixNormalIn, 0.0F, 1.0F, 0.0F).endVertex();
    }
    
    @Override
    public ResourceLocation getEntityTexture(EntityXPOrbBig entity) {
        return EXPERIENCE_ORB_TEXTURES;
    }
}
