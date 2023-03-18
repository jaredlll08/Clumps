package com.blamejared.clumps.api.events;

public interface IEventHandler<T, U> {
    
    U handle(T event);
    
}
