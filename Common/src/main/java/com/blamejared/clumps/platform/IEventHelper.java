package com.blamejared.clumps.platform;


import com.blamejared.clumps.api.events.IValueEvent;
import com.mojang.datafixers.util.Either;

import java.util.Optional;

public interface IEventHelper {
    
    Either<IValueEvent, Integer> fireValueEvent(int value);
    
}
