package com.blamejared.clumps.client.render;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class RenderXPOrbBig extends Render<EntityXPOrbBig> {
	
	private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
	
	public RenderXPOrbBig(RenderManager renderManagerIn) {
		super(renderManagerIn);
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}
	
	/**
	 * Renders the desired {@code T} type Entity.
	 */
	public void doRender(EntityXPOrbBig entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(!this.renderOutlines) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y, (float) z);
			this.bindEntityTexture(entity);
			RenderHelper.enableStandardItemLighting();
			int i = entity.getTextureByXP();
			float f = (float) (i % 4 * 16) / 64.0F;
			float f1 = (float) (i % 4 * 16 + 16) / 64.0F;
			float f2 = (float) (i / 4 * 16) / 64.0F;
			float f3 = (float) (i / 4 * 16 + 16) / 64.0F;
			int j = entity.getBrightnessForRender(partialTicks);
			int k = j % 65536;
			int l = j / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			float f9 = ((float) entity.xpColor) / 2.0F;
			l = (int) ((MathHelper.sin(f9 + 1) + 1.0F)  * 255);
			int j1 = 0xFFFFFF;//(int) ((MathHelper.sin(f9 + 4.1887903F) + 1.0F) * 255.0F);
			GlStateManager.translate(0.0F, 0.1F, 0.0F);
			GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(0.3F, 0.3F, 0.3F);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
			vertexbuffer.pos(-1, -1, 0.0D).tex((double) f, (double) f3).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
			vertexbuffer.pos(1, -1, 0.0D).tex((double) f1, (double) f3).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
			vertexbuffer.pos(1, 1, 0.0D).tex((double) f1, (double) f2).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
			vertexbuffer.pos(-1, 1, 0.0D).tex((double) f, (double) f2).color(l, 255, j1, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			GlStateManager.disableBlend();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}
	
	
	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityXPOrbBig entity) {
		return EXPERIENCE_ORB_TEXTURES;
	}
}
