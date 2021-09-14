package com.blamejared.clumps.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ExperienceOrb.class)
public interface ExperienceOrbAccess {
    
    @Accessor
    int getAge();
    
    @Accessor
    void setAge(int age);
    
    @Accessor
    void setCount(int count);
    
    
    
}
