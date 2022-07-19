package com.blamejared.clumps.platform;

import com.blamejared.clumps.api.events.ClumpsEvents;
import com.blamejared.clumps.api.events.IValueEvent;
import com.blamejared.clumps.api.events.ValueEvent;
import com.mojang.datafixers.util.Either;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;

public class FabricEventHandler implements IEventHelper {
    
    @Override
    public Either<IValueEvent, Integer> fireValueEvent(int value) {
        
        ValueEvent event = new ValueEvent();
        if(FabricLoader.getInstance().isModLoaded("fabric")) {
            ClumpsEvents.VALUE_EVENT.invoker().handle(event);
            return Either.right(event.getValue());
        }
        
        return Either.right(event.getValue());
    }
    
}
