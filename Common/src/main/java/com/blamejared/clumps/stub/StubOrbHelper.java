package com.blamejared.clumps.stub;

import com.blamejared.clumps.helper.IOrbHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

public class StubOrbHelper implements IOrbHelper {
    
    @Override
    public void setCount(ExperienceOrb orb, int count) {
        
        throw new IllegalArgumentException("ClumpsCommon.orbHelper was not set! This should never happen!");
    }
    
    @Override
    public void setAge(ExperienceOrb orb, int age) {
        
        throw new IllegalArgumentException("ClumpsCommon.orbHelper was not set! This should never happen!");
    }
    
    @Override
    public int getCount(ExperienceOrb orb) {
        
        throw new IllegalArgumentException("ClumpsCommon.orbHelper was not set! This should never happen!");
    }
    
    @Override
    public int getValue(ExperienceOrb orb) {
        
        throw new IllegalArgumentException("ClumpsCommon.orbHelper was not set! This should never happen!");
    }
    
    @Override
    public int getAge(ExperienceOrb orb) {
        
        throw new IllegalArgumentException("ClumpsCommon.orbHelper was not set! This should never happen!");
    }
    
    @Override
    public int repairPlayerItems(ExperienceOrb orb, Player player, int param1) {
        
        throw new IllegalArgumentException("ClumpsCommon.orbHelper was not set! This should never happen!");
    }
    
}
