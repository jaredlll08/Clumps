package com.blamejared.clumps.platform;

import com.blamejared.clumps.api.events.IRepairEvent;
import com.blamejared.clumps.api.events.IValueEvent;
import com.blamejared.clumps.api.events.RepairEvent;
import com.blamejared.clumps.api.events.ValueEvent;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;

public class ForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IValueEvent, Integer> fireValueEvent(Player player, int value) {
        
        ValueEvent event = new ValueEvent(player, value);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
    
    @Override
    public Either<IRepairEvent, Integer> fireRepairEvent(Player player, int value) {
        
        RepairEvent event = new RepairEvent(player, value);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
    
}
