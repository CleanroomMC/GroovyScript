package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroovyEvent {

    private final String fullName;
    private final String name;
    private final boolean cancelable;

    public GroovyEvent(String fullName, String name, boolean cancelable) {
        this.fullName = fullName;
        this.name = name;
        this.cancelable = cancelable;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    @GroovyBlacklist
    public static class Group {

        private static final String pathSeparator = "\\.";
        private final Map<String, GroovyEvent> events = new HashMap<>();
        private final Map<String, GroovyEvent.Group> eventGroups = new HashMap<>();
        private final Map<String, List<Closure<Object>>> listeners = new HashMap<>();
        private final String namespace;

        protected Group(String namespace) {
            this.namespace = namespace;
        }

        protected void registerEvent(String fullName, String name, boolean cancelable) {
            String[] parts = name.split(pathSeparator, 2);
            if (parts.length == 1) {
                String event = parts[0];
                if (events.containsKey(event)) {
                    throw new IllegalStateException("Event '" + fullName + " is already registered!");
                }
                events.put(event, new GroovyEvent(fullName, name, cancelable));
            } else if (parts.length == 2) {
                eventGroups.computeIfAbsent(parts[0], key -> new Group(parts[0])).registerEvent(fullName, parts[1], cancelable);
            } else {
                throw new IllegalArgumentException("Could not parse event name " + fullName);
            }
        }

        public boolean registerListener(String event, Closure<Object> closure) {
            String[] parts = event.split(pathSeparator, 2);
            if (parts.length == 1) {
                event = parts[0];
                if (events.containsKey(event)) {
                    listeners.computeIfAbsent(event, key -> new ArrayList<>()).add(closure);
                    return true;
                }
                return false;
            } else if (parts.length == 2) {
                Group group = eventGroups.get(parts[0]);
                return group != null && group.registerListener(parts[1], closure);
            } else {
                throw new IllegalArgumentException("Could not parse event name " + event);
            }
        }

        protected void clearListeners(String event) {
            String[] parts = event.split(pathSeparator, 2);
            if (parts.length == 1) {
                event = parts[0];
                listeners.remove(event);
            } else if (parts.length == 2) {
                Group group = eventGroups.get(parts[0]);
                if (group != null) {
                    group.clearListeners(parts[1]);
                }
            } else {
                throw new IllegalArgumentException("Could not parse event name " + event);
            }
        }

        protected void clearListeners() {
            listeners.clear();
            eventGroups.values().forEach(Group::clearListeners);
        }

        protected boolean invokeEvent(String event, Object... args) {
            String[] parts = event.split(pathSeparator, 2);
            if (parts.length == 1) {
                event = parts[0];
                GroovyEvent groovyEvent = events.get(event);
                if (groovyEvent == null) throw new IllegalArgumentException("Could not find event for name " + event);
                List<Closure<Object>> eventListeners = listeners.get(event);
                if (eventListeners != null && !eventListeners.isEmpty()) {
                    for (Closure<Object> closure : eventListeners) {
                        boolean result = ClosureHelper.call(true, closure, args);
                        if (groovyEvent.isCancelable() && result) {
                            return true;
                        }
                    }
                }
            } else if (parts.length == 2) {
                Group group = eventGroups.get(parts[0]);
                if (group == null) throw new IllegalArgumentException("Could not find event for name " + event);
                return group.invokeEvent(parts[1], args);
            } else {
                throw new IllegalArgumentException("Could not parse event name " + event);
            }
            return false;
        }

        @Nullable
        public GroovyEvent getEvent(String name) {
            String[] parts = name.split(pathSeparator, 2);
            if (parts.length == 1) {
                return events.get(parts[0]);
            } else if (parts.length == 2) {
                Group group = eventGroups.get(parts[0]);
                if (group != null) {
                    return group.getEvent(parts[1]);
                }
            }
            return null;
        }

        @Nullable
        public Group getEventGroup(String group) {
            return eventGroups.get(group);
        }

        public boolean isEventHere(String event) {
            return events.containsKey(event);
        }
    }
}
