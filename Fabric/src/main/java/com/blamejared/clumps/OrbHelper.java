package com.blamejared.clumps;

import com.blamejared.clumps.helper.IOrbHelper;
import com.blamejared.clumps.mixin.ExperienceOrbAccess;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

public class OrbHelper implements IOrbHelper {
    
    @Override
    public void setCount(ExperienceOrb orb, int count) {
        
        ((ExperienceOrbAccess) orb).setCount(count);
    }
    
    @Override
    public void setAge(ExperienceOrb orb, int age) {
        
        ((ExperienceOrbAccess) orb).setAge(age);
    }
    
    @Override
    public int getCount(ExperienceOrb orb) {
        
        return ((ExperienceOrbAccess) orb).getCount();
    }
    
    @Override
    public int getValue(ExperienceOrb orb) {
        
        return ((ExperienceOrbAccess) orb).getValue();
    }
    
    @Override
    public int getAge(ExperienceOrb orb) {
        
        return ((ExperienceOrbAccess) orb).getAge();
    }
    
    @Override
    public int repairPlayerItems(ExperienceOrb orb, Player player, int param1) {
        
        return ((ExperienceOrbAccess) orb).callRepairPlayerItems(player, param1);
    }
    
}
