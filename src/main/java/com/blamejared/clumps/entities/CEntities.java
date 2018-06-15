package com.blamejared.clumps.entities;

import com.blamejared.clumps.reference.Reference;
import com.teamacronymcoders.base.modulesystem.*;
import com.teamacronymcoders.base.registrysystem.EntityRegistry;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import com.teamacronymcoders.base.registrysystem.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.registries.GameData;


import static com.blamejared.clumps.reference.Reference.MODID;

@Module(MODID)
public class CEntities extends ModuleBase {

	@Override
	public void registerEntities(ConfigRegistry configRegistry, EntityRegistry entityRegistry) {
	
	
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
