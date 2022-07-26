package com.blamejared.clumps.platform;


import com.blamejared.clumps.api.events.IValueEvent;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.player.Player;


public interface IEventHelper {
    
    Either<IValueEvent, Integer> fireValueEvent(Player player, int value);
    
}
