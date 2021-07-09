package com.blamejared.clumps;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import com.blamejared.clumps.events.EXPCloneEvent;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.*;

@Mod(Clumps.MODID)
public class Clumps {
    
    public static final String MODID = "clumps";

    private static final DeferredRegister<EntityType<?>> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<EntityXPOrbBig>> BIG_ORB_ENTITY_TYPE = DEFERRED_REGISTER.register("xp_orb_big", () -> EntityType.Builder.<EntityXPOrbBig>create(EntityXPOrbBig::new, EntityClassification.MISC).size(0.5f, 0.5f).build(MODID + ":xp_orb_big"));
    
    public Clumps() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus(),
                forgeBus = MinecraftForge.EVENT_BUS;

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClumpsClient::register);
        DEFERRED_REGISTER.register(modBus);
        modBus.addListener(this::setup);
        forgeBus.addListener(this::joinWorld);
        forgeBus.addListener(this::update);
    
    }

    private static final List<ExperienceOrbEntity> orbs = new ArrayList<>();

    private void setup(FMLCommonSetupEvent e) {
        ObfuscationReflectionHelper.setPrivateValue(EntityType.class, BIG_ORB_ENTITY_TYPE.get(), EntityType.EXPERIENCE_ORB.getTranslationKey(), "field_210762_aX");
    }
    
    private void update(TickEvent.WorldTickEvent e) {
        if(e.world.isRemote || e.phase == TickEvent.Phase.START) {
            return;
        }
        if(e.world instanceof ServerWorld && !orbs.isEmpty()) {
            Iterator<ExperienceOrbEntity> it = orbs.iterator();
            while(it.hasNext()) {
                ExperienceOrbEntity entity = it.next();
                HashMap<Integer, Long> clumpedMap = new HashMap<>();
                clumpedMap.put(entity.xpValue, 1L);
                EntityXPOrbBig bigOrb = new EntityXPOrbBig(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), entity.xpValue, clumpedMap);
                MinecraftForge.EVENT_BUS.post(new EXPCloneEvent(entity, bigOrb));

                bigOrb.setMotion(entity.getMotion());
                entity.getEntityWorld().addEntity(bigOrb);
                entity.remove();
            }
            orbs.clear();
        }
    }
    
    private void joinWorld(EntityJoinWorldEvent e) {
        if(e.getEntity() instanceof ExperienceOrbEntity && e.getEntity().getType() != BIG_ORB_ENTITY_TYPE.get()) {
            World world = e.getEntity().world;
            if(!world.isRemote) {
                orbs.add((ExperienceOrbEntity) e.getEntity());
            }
        }
    }
    
}
