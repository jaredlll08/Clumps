package com.blamejared.clumps;

import com.blamejared.clumps.client.render.RenderXPOrbBig;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClumpsClient {
    protected static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClumpsClient::setupClient);
    }

    protected static void setupClient(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Clumps.BIG_ORB_ENTITY_TYPE.get(), RenderXPOrbBig::new);
    }
}