package com.blamejared.clumps;

import com.blamejared.clumps.client.render.RenderXPOrbBig;
import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.blamejared.clumps.reference.Reference;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MODID)
public class Clumps {
    
    public static final EntityType<EntityXPOrbBig> BIG_ORB_ENTITY_TYPE = EntityType.Builder.<EntityXPOrbBig> create(EntityClassification.MISC).size(0.5f, 0.5f).build(Reference.MODID + ":xp_orb_big");
    
    
    public Clumps() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityXPOrbBig.class, new RenderXPOrbBig.Factory());
    }
    
    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent e) {
        if(e.getEntity() instanceof ExperienceOrbEntity && !(e.getEntity() instanceof EntityXPOrbBig)) {
            World world = e.getEntity().world;
            if(!world.isRemote) {
                ExperienceOrbEntity orb = (ExperienceOrbEntity) e.getEntity();
                EntityXPOrbBig bigOrb = new EntityXPOrbBig(world, orb.posX, orb.posY, orb.posZ, orb.xpValue);
                world.addEntity(bigOrb);
                e.setCanceled(true);
            }
        }
    }
    
    
}
