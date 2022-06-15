package com.blamejared.clumps.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccess {
    
    @Accessor("age")
    int clumps$getAge();
    
    @Accessor("age")
    void clumps$setAge(int age);
    
    @Accessor("count")
    void clumps$setCount(int count);
    
}
