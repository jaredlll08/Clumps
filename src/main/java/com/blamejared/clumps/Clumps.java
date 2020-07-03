package com.blamejared.clumps;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.World;

@Mod(Clumps.MODID)
public class Clumps {
    
	public static final String MODID = "clumps";
    public static final EntityType<EntityXPOrbBig> BIG_ORB_ENTITY_TYPE = EntityType.Builder.<EntityXPOrbBig> create(EntityClassification.MISC).size(0.5f, 0.5f).setCustomClientFactory((pkt, world) -> new EntityXPOrbBig(world)).build(Clumps.MODID + ":xp_orb_big");
    
    public Clumps() {
    	
    	DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClumpsClient::setupClient));
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntity);
        MinecraftForge.EVENT_BUS.addListener(this::joinWorld);
    }
    
    private void registerEntity(Register<EntityType<?>> register) {
    	
        register.getRegistry().register(BIG_ORB_ENTITY_TYPE.setRegistryName(Clumps.MODID, "xp_orb_big"));
    }
    
    private void joinWorld(EntityJoinWorldEvent e) {
        if(e.getEntity() instanceof ExperienceOrbEntity && !(e.getEntity() instanceof EntityXPOrbBig)) {
            World world = e.getEntity().world;
            if(!world.isRemote) {
                ExperienceOrbEntity orb = (ExperienceOrbEntity) e.getEntity();
                EntityXPOrbBig bigOrb = new EntityXPOrbBig(world, orb.getPosX(), orb.getPosY(), orb.getPosZ(), orb.xpValue);
                world.addEntity(bigOrb);
                e.setCanceled(true);
            }
        }
    }
}