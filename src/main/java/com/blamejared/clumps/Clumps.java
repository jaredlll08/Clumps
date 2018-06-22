package com.blamejared.clumps;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.blamejared.clumps.proxy.CommonProxy;
import com.blamejared.clumps.reference.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDECIES)
public class Clumps {
    
    
    @Mod.Instance(Reference.MODID)
    public static Clumps INSTANCE;
    
    @SidedProxy(clientSide = "com.blamejared.clumps.proxy.ClientProxy", serverSide = "com.blamejared.clumps.proxy.CommonProxy")
    public static CommonProxy PROXY;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        //        RenderingRegistry.registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig.Factory());//registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig(Minecraft.getMinecraft().getRenderManager()));
    }
    
    @SubscribeEvent
    public void onRegistryRegister(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry value = new EntityEntry(EntityXPOrbBig.class, "xp_orb_big");
        value.setRegistryName(Reference.MODID, "xp_orb_big");
        event.getRegistry().register(value);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.registerEvents();
        PROXY.registerRenders();
    }
    
    
}
