package com.blamejared.clumps;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiPredicate;

public class ClumpsCommon {
    
    public static final Logger LOG = LogManager.getLogger("clumps");
    
    public static BiPredicate<Player, ExperienceOrb> pickupXPEvent = (player, experienceOrb) -> false;
    
}
