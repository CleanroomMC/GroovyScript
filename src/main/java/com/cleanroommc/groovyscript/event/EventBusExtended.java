package com.cleanroommc.groovyscript.event;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

public interface EventBusExtended {

    void register(Class<?> eventClass, EventPriority priority, IEventListener listener);
}
