package com.blamejared.clumps.events;

import com.blamejared.clumps.entities.EntityXPOrbBig;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraftforge.eventbus.api.Event;

/**
 * EXPCloneEvent is called when a vanilla XP orb is transformed into a Clumps
 * {@link EntityXPOrbBig}. This provides the ability to transfer NBT data to the
 * newly created entity if desired.
 */
public class EXPCloneEvent extends Event {
    private final ExperienceOrbEntity vanillaOrb;
    private EntityXPOrbBig newOrb;

    public EXPCloneEvent(ExperienceOrbEntity vanillaOrb, EntityXPOrbBig newOrb) {
        this.vanillaOrb = vanillaOrb;
        this.newOrb = newOrb;
    }

    public ExperienceOrbEntity getVanillaOrb() {
        return vanillaOrb;
    }

    public EntityXPOrbBig getNewOrb() {
        return newOrb;
    }

    public void setNewOrb(EntityXPOrbBig newOrb) {
        this.newOrb = newOrb;
    }
}
