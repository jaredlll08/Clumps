package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;

/**
 * Fired before an item is repaired, allowing other mods to repair items before the vanilla inventory is repaired.
 * If a repair is performed, then any leftover experience should be set back to this event using {@link #setValue(int)}
 */
public interface IRepairEvent {
    
    /**
     * Sets the amount of experience left after repairing.
     *
     * @param value The new value to set.
     */
    void setValue(int value);
    
    /**
     * Gets the amount of experience to be used by the repair.
     *
     * @return The amount of experience to be used by the repair.
     */
    int getValue();
    
    /**
     * Gets the player whose items are being repaired.
     *
     * @return The player whose items are being repaired.
     */
    Player getPlayer();
    
}
