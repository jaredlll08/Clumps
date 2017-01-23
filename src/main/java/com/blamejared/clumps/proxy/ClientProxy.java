package com.blamejared.clumps.proxy;

import com.blamejared.clumps.client.render.RenderXPOrbBig;
import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.blamejared.clumps.events.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	public void registerEvents() {
		super.registerEvents();
	}
	
	public void registerRenders() {
		RenderingRegistry.registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig(Minecraft.getMinecraft().getRenderManager()));
	}
}
