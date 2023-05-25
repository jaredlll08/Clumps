package com.blamejared.clumps.api.events;

import net.minecraft.world.entity.player.Player;

/**
 * Used to mutate the value of experience before repairing player's items and giving the rest to the player.
 * **NOTE** This mutates the actual value of the experience orb, it is fired once per *value*, not per orb inside the clumped orb.
 * For example, given the orb: {5: 10}, which is an orb that has clumped 10 other orbs who had the value of 5, this even will fire once
 * if this event is used to change that 5 to a 7, the orb will *effectively* turn into {7: 10}
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
