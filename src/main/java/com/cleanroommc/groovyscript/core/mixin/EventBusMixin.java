package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.event.EventBusExtended;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = EventBus.class, remap = false)
public class EventBusMixin implements EventBusExtended {

    @Shadow
    @Final
    private int busID;

    @Shadow
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;

    @Override
    public void register(Class<?> eventClass, EventPriority priority, IEventListener listener) {
        try {
            Constructor<?> ctor = eventClass.getConstructor();
            ctor.setAccessible(true);
            Event event = (Event) ctor.newInstance();
            event.getListenerList().register(busID, priority, listener);
            listeners.computeIfAbsent(listener, k -> new ArrayList<>()).add(listener);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
