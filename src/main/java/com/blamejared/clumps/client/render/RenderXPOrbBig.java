package com.blamejared.clumps.client.render;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderXPOrbBig extends EntityRenderer<EntityXPOrbBig> {
    
    private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
    
    public RenderXPOrbBig(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }
    
    @Override
    public boolean shouldRender(EntityXPOrbBig livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }
    
    @Override
    public void doRender(EntityXPOrbBig entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if(!this.renderOutlines) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float) x, (float) y, (float) z);
            this.bindEntityTexture(entity);
            RenderHelper.enableStandardItemLighting();
            int i = entity.getTextureByXP();
            float f = (float) (i % 4 * 16) / 64.0F;
            float f1 = (float) (i % 4 * 16 + 16) / 64.0F;
            float f2 = (float) (i / 4 * 16) / 64.0F;
            float f3 = (float) (i / 4 * 16 + 16) / 64.0F;
            int j = entity.getBrightnessForRender();
            int k = j % 65536;
            int l = j / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) k, (float) l);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translatef(0.0F, 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef((float) (this.renderManager.options != null && this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scalef(0.3F, 0.3F, 0.3F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            
            int startRed = 0;
            int startGreen = 102;
            int startBlue = 255;
            
            int endRed = 20;
            int endGreen = 204;
            int endBlue = 255;
            
            // 255, 102, 0
            float ratio = (MathHelper.sin(entity.ticksExisted / 2)+1) / 2;
            int red = (int)Math.abs((ratio * startRed) + ((1 - ratio) * endRed));
            int green = (int)Math.abs((ratio * startGreen) + ((1 - ratio) * endGreen));
            int blue = (int)Math.abs((ratio * startBlue) + ((1 - ratio) * endBlue));
            
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            vertexbuffer.pos(-1, -1, 0.0D).tex((double) f, (double) f3).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            vertexbuffer.pos(1, -1, 0.0D).tex((double) f1, (double) f3).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            vertexbuffer.pos(1, 1, 0.0D).tex((double) f1, (double) f2).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            vertexbuffer.pos(-1, 1, 0.0D).tex((double) f, (double) f2).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityXPOrbBig entity) {
        return EXPERIENCE_ORB_TEXTURES;
    }
}
