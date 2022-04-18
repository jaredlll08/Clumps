package com.blamejared.clumps.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccess {
    
    @Accessor
    int getAge();
    
    @Accessor
    void setAge(int age);
    
    @Accessor
    void setCount(int count);
    
    
}
