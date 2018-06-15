package com.blamejared.clumps;

import com.blamejared.clumps.client.render.RenderXPOrbBig;
import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.blamejared.clumps.proxy.CommonProxy;
import com.blamejared.clumps.reference.Reference;
import com.teamacronymcoders.base.BaseModFoundation;
import com.teamacronymcoders.base.registrysystem.entity.*;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.*;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDECIES)
public class Clumps extends BaseModFoundation<Clumps> {
    
    
    @Mod.Instance(Reference.MODID)
    public static Clumps INSTANCE;
    
    @SidedProxy(clientSide = "com.blamejared.clumps.proxy.ClientProxy", serverSide = "com.blamejared.clumps.proxy.CommonProxy")
    public static CommonProxy PROXY;
    
    public Clumps() {
        super(Reference.MODID, Reference.NAME, Reference.VERSION, null);
    }
    
    @Override
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
        RenderingRegistry.registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig.Factory());//registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig(Minecraft.getMinecraft().getRenderManager()));
    }
    
//    @SideOnly(Side.CLIENT)
//    @Mod.EventHandler
//    public void onFMLPreInitialization(FMLPreInitializationEvent event) {
//
////        RenderingRegistry.registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig.Factory());//registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig(Minecraft.getMinecraft().getRenderManager()));
//
//    }
    
    @SubscribeEvent
    public void onRegistryRegister(RegistryEvent.Register<EntityEntry> event) {
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setTrackingRange(20);
        ExtendedEntityEntry entityEntry = new ExtendedEntityEntry(EntityXPOrbBig.class, "xp_orb_big");
        entityEntry.setUpdateInfo(updateInfo);
        entityEntry.setRegistryName(Reference.MODID, "xp_orb_big");
        event.getRegistry().register(entityEntry);
        
    }
    
    @Override
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
        PROXY.registerEvents();
        PROXY.registerRenders();
    }
    
    
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    
    @Override
    public boolean hasConfig() {
        return false;
    }
    
    @Override
    public Clumps getInstance() {
        return INSTANCE;
    }
}
