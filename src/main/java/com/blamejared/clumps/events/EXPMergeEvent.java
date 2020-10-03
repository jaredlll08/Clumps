package com.blamejared.clumps.events;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import net.minecraftforge.eventbus.api.Event;

/**
 * EXPMergeEvent is called when two Clumps {@link EntityXPOrbBig}s are combined
 * into a new, bigger orb. Again, this allows for the transfer of any important NBT
 * data from the old orbs to the new orb if necessary.
 */
public class EXPMergeEvent extends Event {
    private final EntityXPOrbBig primaryOldOrb;
    private final EntityXPOrbBig secondaryOldOrb;
    private EntityXPOrbBig newOrb;

    public EXPMergeEvent(EntityXPOrbBig primaryOldOrb, EntityXPOrbBig secondaryOldOrb, EntityXPOrbBig newOrb) {
        this.primaryOldOrb = primaryOldOrb;
        this.secondaryOldOrb = secondaryOldOrb;
        this.newOrb = newOrb;
    }

    public EntityXPOrbBig getPrimaryOldOrb() {
        return primaryOldOrb;
    }

    public EntityXPOrbBig getSecondaryOldOrb() {
        return secondaryOldOrb;
    }

    public EntityXPOrbBig getNewOrb() {
        return newOrb;
    }

    public void setNewOrb(EntityXPOrbBig newOrb) {
        this.newOrb = newOrb;
    }
}
