package com.blamejared.clumps;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("clumps")
public class Clumps {
    
    public Clumps() {
        
        ClumpsCommon.pickupXPEvent = (player, experienceOrb) -> MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(player, experienceOrb));
    }
    
}
