package com.blamejared.clumps;

import com.blamejared.clumps.client.render.RenderXPOrbBig;
import com.blamejared.clumps.entities.EntityXPOrbBig;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClumpsClient {

    protected static void setupClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Clumps.BIG_ORB_ENTITY_TYPE, RenderXPOrbBig::new);
    }
}