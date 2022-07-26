package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class ValueEvent extends Event implements IValueEvent {
    
    private final Player player;
    private int value;
    
    public ValueEvent(Player player, int value) {
        
        this.player = player;
        this.value = value;
    }
    
    /**
     * Sets the value of the experience orb.
     *
     * @param value The new value to set.
     */
    @Override
    public void setValue(int value) {
        
        this.value = value;
    }
    
    /**
     * Gets the value of the experience orb.
     *
     * @return The value of the experience orb.
     */
    @Override
    public int getValue() {
        
        return this.value;
    }
    
    /**
     * Gets the player that the experience is being given to.
     *
     * @return The player the experience is being given to.
     */
    @Override
    public Player getPlayer() {
        
        return this.player;
    }
    
}
