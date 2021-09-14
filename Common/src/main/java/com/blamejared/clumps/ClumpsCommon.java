package com.blamejared.clumps;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiPredicate;

public class ClumpsCommon {
    
    public static BiPredicate<Player, ExperienceOrb> pickupXPEvent = (player, experienceOrb) -> false;
    
}
