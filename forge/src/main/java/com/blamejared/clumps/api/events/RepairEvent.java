package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired before an item is repaired, if a repair is performed, then any leftover experience should be set back to this event.
 */
public class RepairEvent extends Event implements IRepairEvent {
    
    private final Player player;
    private int value;
    
    public RepairEvent(Player player, int value) {
        
        this.player = player;
        this.value = value;
    }
    
    /**
     * Sets the amount of experience left after repairing.
     *
     * @param value The new value to set.
     */
    @Override
    public void setValue(int value) {
        
        this.value = value;
    }
    
    /**
     * Gets the amount of experience to be used by the repair.
     *
     * @return The amount of experience to be used by the repair.
     */
    @Override
    public int getValue() {
        
        return this.value;
    }
    
    /**
     * Gets the player whose items are being repaired.
     *
     * @return The player whose items are being repaired.
     */
    @Override
    public Player getPlayer() {
        
        return this.player;
    }
    
}
