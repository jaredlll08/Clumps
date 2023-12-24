package com.blamejared.clumps.platform;

import com.blamejared.clumps.api.events.IRepairEvent;
import com.blamejared.clumps.api.events.IValueEvent;
import com.blamejared.clumps.api.events.RepairEvent;
import com.blamejared.clumps.api.events.ValueEvent;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IValueEvent, Integer> fireValueEvent(Player player, int value) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new ValueEvent(player, value)));
    }
    
    @Override
    public Either<IRepairEvent, Integer> fireRepairEvent(Player player, int value) {
        
        return Either.left(NeoForge.EVENT_BUS.post(new RepairEvent(player, value)));
    }
    
}
