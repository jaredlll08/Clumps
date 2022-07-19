
package com.blamejared.clumps.api.events;

/**
 * Used to mutate the value of experience before repairing player's items and giving the rest to the player.
 */
public class ValueEvent implements IValueEvent {
    
    private int value;
    
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
     * @return the value of the experience orb.
     */
    @Override
    public int getValue() {
        
        return value;
    }
    
}