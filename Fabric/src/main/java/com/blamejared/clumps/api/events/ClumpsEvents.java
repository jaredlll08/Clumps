package com.blamejared.clumps.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ClumpsEvents {
    
    public static final Event<IEventHandler<ValueEvent, Void>> VALUE_EVENT = EventFactory.createArrayBacked(IEventHandler.class, listeners -> event -> {
        for(IEventHandler<ValueEvent, Void> listener : listeners) {
            listener.handle(event);
        }
        return null;
    });
    
}
