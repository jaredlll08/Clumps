package com.blamejared.clumps.platform;

import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public interface IPlatformHelper {
    
    default BiFunction<ItemStack, Integer, Float> getRepairRatio(BiFunction<ItemStack, Integer, Float> defaultRepairRatio) {
        
        return defaultRepairRatio;
    }
    
}
