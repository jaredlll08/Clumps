package com.blamejared.clumps.platform;

import com.blamejared.clumps.api.events.IValueEvent;
import com.blamejared.clumps.api.events.ValueEvent;
import com.mojang.datafixers.util.Either;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

public class ForgeEventHandler implements IEventHelper {
    
    @Override
    public Either<IValueEvent, Integer> fireValueEvent(int value) {
        
        ValueEvent event = new ValueEvent(value);
        MinecraftForge.EVENT_BUS.post(event);
        return Either.left(event);
    }
    
}
