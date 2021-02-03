package com.blamejared.clumps;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.blamejared.clumps.events.EXPCloneEvent;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.registries.ObjectHolder;
import java.util.*;

@Mod(Clumps.MODID)
public class Clumps {
    
    public static final String MODID = "clumps";
    @ObjectHolder(Clumps.MODID + ":xp_orb_big")
    public static final EntityType<EntityXPOrbBig> BIG_ORB_ENTITY_TYPE = null;
    
    public Clumps() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus(),
                forgeBus = MinecraftForge.EVENT_BUS;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.addListener(ClumpsClient::setupClient));
        modBus.addGenericListener(EntityType.class, this::registerEntity);
        forgeBus.addListener(this::joinWorld);
        forgeBus.addListener(this::update);
    
    }
    
    private void registerEntity(Register<EntityType<?>> register) {
        register.getRegistry().register(EntityType.Builder.create((type, world) -> new EntityXPOrbBig(world), EntityClassification.MISC).size(0.5f, 0.5f).build(Clumps.MODID + ":xp_orb_big").setRegistryName(Clumps.MODID, "xp_orb_big"));
    }
    
    private static final List<ExperienceOrbEntity> orbs = new ArrayList<>();
    
    private void update(TickEvent.WorldTickEvent e) {
        if(e.world.isRemote || e.phase == TickEvent.Phase.START) {
            return;
        }
        if(e.world instanceof ServerWorld && !orbs.isEmpty()) {
            Iterator<ExperienceOrbEntity> it = orbs.iterator();
            while(it.hasNext()) {
                ExperienceOrbEntity entity = it.next();
                EntityXPOrbBig bigOrb = new EntityXPOrbBig(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.xpValue, 1);
                MinecraftForge.EVENT_BUS.post(new EXPCloneEvent(entity, bigOrb));

                bigOrb.setMotion(entity.getMotion());
                entity.getEntityWorld().addEntity(bigOrb);
                entity.remove();
                it.remove();
            }
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
