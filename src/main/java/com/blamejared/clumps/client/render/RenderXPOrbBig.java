package com.blamejared.clumps.client.render;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.mojang.blaze3d.platform.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.*;
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
            int texIndex = entity.getTextureByXP();
            float minX = (float) (texIndex % 4 * 16) / 64.0F;
            float maxX = (float) (texIndex % 4 * 16 + 16) / 64.0F;
            float minY = (float) (texIndex / 4 * 16) / 64.0F;
            float maxY = (float) (texIndex / 4 * 16 + 16) / 64.0F;
            int brightness = entity.getBrightnessForRender();
            int brightnessX = brightness % 65536;
            int brightnessY = brightness / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) brightnessX, (float) brightnessY);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translatef(0.0F, 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef((float) (this.renderManager.options != null && this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scalef(0.3F, 0.3F, 0.3F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            int red;
            int green;
            int blue;
            float f9 = ((float) entity.xpColor + partialTicks) / 2.0F;
            red = (int) ((MathHelper.sin(f9 + 1) + 1.0F) * 255);
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
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            vertexbuffer.pos(-1, -1, 0.0D).tex(minX, maxY).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            vertexbuffer.pos(1, -1, 0.0D).tex(maxX, maxY).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            vertexbuffer.pos(1, 1, 0.0D).tex(maxX, minY).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
            vertexbuffer.pos(-1, 1, 0.0D).tex(minX, minY).color(red, green, blue, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
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
