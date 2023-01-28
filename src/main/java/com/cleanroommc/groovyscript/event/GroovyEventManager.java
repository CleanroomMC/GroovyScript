package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.*;

import java.util.ArrayList;
import java.util.List;

public enum GroovyEventManager {

    INSTANCE;

    private final List<EventListener> listeners = new ArrayList<>();

    @GroovyBlacklist
    public void reset() {
        for (EventListener listener : this.listeners) {
            listener.unregister();
        }
        this.listeners.clear();
    }

    public void listen(EventPriority eventPriority, EventBusType eventBusType, Closure<?> eventListener) {
        if (eventListener.getMaximumNumberOfParameters() > 1) {
            GroovyLog.get().error("Event listeners should only have one parameter.");
            return;
        }
        Class<?> eventClass = eventListener.getParameterTypes()[0];
        if (!Event.class.isAssignableFrom(eventClass)) {
            GroovyLog.get().error("Event listeners' only parameter should be the Event class you are trying to listen to.");
            return;
        }
        this.listeners.add(new EventListener(eventBusType, eventPriority, eventListener));
    }

    public void listen(EventBusType eventBusType, EventPriority eventPriority, Closure<?> eventListener) {
        listen(eventPriority, eventBusType, eventListener);
    }

    public void listen(EventBusType eventBusType, Closure<?> eventListener) {
        listen(EventPriority.NORMAL, eventBusType, eventListener);
    }

    public void listen(EventPriority eventPriority, Closure<?> eventListener) {
        listen(eventPriority, EventBusType.MAIN, eventListener);
    }

    public void listen(Closure<?> eventListener) {
        listen(EventPriority.NORMAL, EventBusType.MAIN, eventListener);
    }

    private static class EventListener implements IEventListener {

        private final EventBus eventBus;
        private final Closure<?> listener;

        private IEventListener wrappedListener = this;

        private EventListener(EventBusType busType, EventPriority priority, Closure<?> listener) {
            switch (busType) {
                case ORE_GENERATION:
                    this.eventBus = MinecraftForge.ORE_GEN_BUS;
                    break;
                case TERRAIN_GENERATION:
                    this.eventBus = MinecraftForge.TERRAIN_GEN_BUS;
                    break;
                default:
                    this.eventBus = MinecraftForge.EVENT_BUS;
                    break;
            }
            this.listener = listener;
            this.register(priority);
        }

        private void register(EventPriority priority) {
            Class<?> eventClass = listener.getParameterTypes()[0];
            if (IContextSetter.class.isAssignableFrom(eventClass)) {
                final ModContainer owner = Loader.instance().activeModContainer();
                this.wrappedListener = event -> {
                    final Loader loader = Loader.instance();
                    ModContainer old = loader.activeModContainer();
                    loader.setActiveModContainer(owner);
                    ((IContextSetter) event).setModContainer(owner);
                    EventListener.this.invoke(event);
                    loader.setActiveModContainer(old);
                };
            }
            ((EventBusExtended) this.eventBus).register(eventClass, priority, this.wrappedListener);
        }

        private void unregister() {
            this.eventBus.unregister(this.wrappedListener);
        }

        @Override
        public void invoke(Event event) {
            if (!event.isCancelable() || !event.isCanceled()/* || subInfo.receiveCanceled()*/) {
                /*
                if (filter == null || filter == ((IGenericEvent) event).getGenericType()) {
                    handler.invoke(event);
                }
                 */
                ClosureHelper.call(this.listener, event);
            }
        }

    }

}
