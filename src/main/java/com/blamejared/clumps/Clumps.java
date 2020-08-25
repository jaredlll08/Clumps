package com.blamejared.clumps;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;

@Mod(Clumps.MODID)
public class Clumps {
    
    public static final String MODID = "clumps";
    public static final EntityType<EntityXPOrbBig> BIG_ORB_ENTITY_TYPE = EntityType.Builder.<EntityXPOrbBig> create(EntityClassification.MISC).size(0.5f, 0.5f).setCustomClientFactory((pkt, world) -> new EntityXPOrbBig(world)).build(Clumps.MODID + ":xp_orb_big");
    
    public Clumps() {
        
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(ClumpsClient::setupClient));
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntity);
        MinecraftForge.EVENT_BUS.addListener(this::joinWorld);
        MinecraftForge.EVENT_BUS.addListener(this::update);
    
    }
    
    private void registerEntity(Register<EntityType<?>> register) {
        
        register.getRegistry().register(BIG_ORB_ENTITY_TYPE.setRegistryName(Clumps.MODID, "xp_orb_big"));
    }
    
    private static final List<ExperienceOrbEntity> orbs = new ArrayList<>();
    
    private void update(TickEvent.WorldTickEvent e) {
        if(e.world.isRemote || e.phase == TickEvent.Phase.START) {
            return;
        }
        if(e.world instanceof ServerWorld) {
            ArrayList<ExperienceOrbEntity> list = new ArrayList<>(orbs);
            for(ExperienceOrbEntity entity : list) {
                EntityXPOrbBig bigOrb = new EntityXPOrbBig(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.xpValue);
                bigOrb.setMotion(entity.getMotion());
                entity.getEntityWorld().addEntity(bigOrb);
                entity.remove();
            }
            orbs.removeAll(list);
        }
    }
    
    private void joinWorld(EntityJoinWorldEvent e) {
        if(e.getEntity() instanceof ExperienceOrbEntity && !(e.getEntity() instanceof EntityXPOrbBig)) {
            World world = e.getEntity().world;
            if(!world.isRemote) {
                orbs.add((ExperienceOrbEntity) e.getEntity());
            }
        }
    }
    
}