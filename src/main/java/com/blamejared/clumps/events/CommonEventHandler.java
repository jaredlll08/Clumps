package com.blamejared.clumps.events;


import static com.blamejared.clumps.Clumps.BIG_ORB_ENTITY_TYPE;

import com.blamejared.clumps.reference.Reference;

import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEventHandler {
    
    public CommonEventHandler() {
    }
    
    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> register) {
        register.getRegistry().register(BIG_ORB_ENTITY_TYPE.setRegistryName(Reference.MODID, "xp_orb_big"));
    }
}
