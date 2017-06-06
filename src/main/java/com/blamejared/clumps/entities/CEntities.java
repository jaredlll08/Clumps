package com.blamejared.clumps.entities;

import com.teamacronymcoders.base.modulesystem.*;
import com.teamacronymcoders.base.registrysystem.EntityRegistry;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import com.teamacronymcoders.base.registrysystem.entity.*;
import net.minecraft.util.ResourceLocation;

import static com.blamejared.clumps.reference.Reference.MODID;

@Module(MODID)
public class CEntities extends ModuleBase {

	@Override
	public void registerEntities(ConfigRegistry configRegistry, EntityRegistry entityRegistry) {
		UpdateInfo updateInfo = new UpdateInfo();
		updateInfo.setTrackingRange(20);
		EntityEntry entityEntry = new EntityEntry(EntityXPOrbBig.class);
		entityEntry.setUpdateInfo(updateInfo);
		entityRegistry.register(new ResourceLocation(this.getMod().getID(),"xp_orb_big"), entityEntry);
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}
	
	@Override
	public String getName() {
		return "Entities";
	}
}
