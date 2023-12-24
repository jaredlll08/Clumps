package com.blamejared.clumps.platform;

import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class NeoForgePlatformHelper implements IPlatformHelper {
    
    @Override
    public BiFunction<ItemStack, Integer, Float> getRepairRatio(BiFunction<ItemStack, Integer, Float> defaultRepairRatio) {
        
        return (itemStack, integer) -> itemStack.getXpRepairRatio();
    }
    
}
