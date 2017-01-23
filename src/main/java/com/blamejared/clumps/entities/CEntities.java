package com.blamejared.clumps.entities;

import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.base.registry.EntityRegistry;
import com.teamacronymcoders.base.registry.config.ConfigRegistry;
import com.teamacronymcoders.base.registry.entity.EntityEntry;
import com.teamacronymcoders.base.registry.entity.UpdateInfo;
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
