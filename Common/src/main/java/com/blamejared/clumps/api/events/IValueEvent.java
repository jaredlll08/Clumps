package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;

/**
 * Used to mutate the value of experience before repairing player's items and giving the rest to the player.
 */
public interface IValueEvent {
    
    /**
     * Sets the value of the experience orb.
     *
     * @param value The new value to set.
     */
    void setValue(int value);
    
    /**
     * Gets the value of the experience orb.
     *
     * @return The value of the experience orb.
     */
    int getValue();
    
    /**
     * Gets the player that the experience is being given to.
     *
     * @return The player the experience is being given to.
     */
    Player getPlayer();
    
}
