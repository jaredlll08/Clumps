package com.blamejared.clumps.events;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEventHandler {
	
	public CommonEventHandler() {
		System.out.println("subscribing");
	}
	
	@SubscribeEvent
	public void updateEntities(EntityJoinWorldEvent e) {
		if(e.getEntity() instanceof EntityXPOrb && !(e.getEntity() instanceof EntityXPOrbBig)) {
			EntityXPOrb orb = (EntityXPOrb) e.getEntity();
			World world = e.getEntity().worldObj;
			if(!world.isRemote)
				world.spawnEntityInWorld(new EntityXPOrbBig(world, orb.posX, orb.posY, orb.posZ, orb.xpValue));
			e.setCanceled(true);
			
		}
	}
	
}
