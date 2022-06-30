package com.cleanroommc.groovyscript.event;

import groovy.lang.Closure;

import java.util.*;

public class GroovyEvent {

    private final String namespace;
    private final String name;
    private final boolean cancelable;

    public GroovyEvent(String namespace, String name, boolean cancelable) {
        this.namespace = namespace;
        this.name = name;
        this.cancelable = cancelable;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public static class Group {

        private static final Map<String, GroovyEvent> eventRegistry = new HashMap<>();
        private static final Map<String, List<Closure<Object>>> listeners = new HashMap<>();
        private final String namespace;

        protected Group(String namespace) {
            this.namespace = namespace;
        }

        protected void registerEvent(String namespace, String name, boolean cancelable) {
            if (eventRegistry.containsKey(name)) {
                throw new IllegalStateException("Event '" + (namespace.isEmpty() ? name : namespace + "." + name) + " is already registered!");
            }
            eventRegistry.put(name, new GroovyEvent(namespace, name, cancelable));
        }

        public void registerListener(String event, Closure<Object> closure) {
            if (!eventRegistry.containsKey(event)) {
                throw new NoSuchElementException("There is no such GroovyEvent '" + event + "' in group '" + namespace + "'!");
            }
            listeners.computeIfAbsent(event, key -> new ArrayList<>()).add(closure);
        }

        protected void clearListeners(String event) {
            if (!eventRegistry.containsKey(event)) {
                throw new NoSuchElementException("There is no such GroovyEvent '" + event + "' in group '" + namespace + "'!");
            }
            listeners.remove(event);
        }

        protected void clearListeners() {
            listeners.clear();
        }

        protected List<Closure<Object>> getListeners(String event) {
            if (!eventRegistry.containsKey(event)) {
                throw new NoSuchElementException("There is no such GroovyEvent '" + event + "' in group '" + namespace + "'!");
            }
            return listeners.getOrDefault(event, Collections.emptyList());
        }

        public GroovyEvent getEvent(String name) {
            if (!eventRegistry.containsKey(name)) {
                throw new NoSuchElementException("There is no such groovy event '" + name + "' in group '" + namespace + "'!");
            }
            return eventRegistry.get(name);
        }
    }
}
