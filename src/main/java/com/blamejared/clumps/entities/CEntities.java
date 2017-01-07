package com.blamejared.clumps.entities;

import com.blamejared.clumps.Clumps;
import com.teamacronymcoders.base.modulesystem.*;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import static com.blamejared.clumps.reference.Reference.MODID;

@Module(MODID)
public class CEntities extends ModuleBase {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		EntityRegistry.registerModEntity(EntityXPOrbBig.class, "xp_orb_big", 0, Clumps.INSTANCE, 64, 20, true);
	}
	
	@Override
	public String getName() {
		return "Entities";
	}
}
