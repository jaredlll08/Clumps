package com.blamejared.clumps.helper;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

public interface IOrbHelper {
    
    void setCount(ExperienceOrb orb, int count);
    
    void setAge(ExperienceOrb orb, int age);
    
    int getCount(ExperienceOrb orb);
    
    int getValue(ExperienceOrb orb);
    
    int getAge(ExperienceOrb orb);
    
    int repairPlayerItems(ExperienceOrb orb, Player param0, int param1);
    
    default boolean fireXPPickup(Player player, ExperienceOrb orb) {
        
        return false;
    }
    
}
