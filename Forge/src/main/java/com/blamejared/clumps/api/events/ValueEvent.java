package com.blamejared.clumps.api.events;

import net.minecraftforge.eventbus.api.Event;

public class ValueEvent extends Event implements IValueEvent {
    
    private int value;
    
    public ValueEvent(int value) {
        
        this.value = value;
    }
    
    @Override
    public void setValue(int value) {
        
        this.value = value;
    }
    
    @Override
    public int getValue() {
        
        return value;
    }
    
}
