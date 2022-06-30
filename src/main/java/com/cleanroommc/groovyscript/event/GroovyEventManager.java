package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import groovy.lang.Closure;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.regex.Pattern;

public class GroovyEventManager {

    private static final Map<String, GroovyEvent.Group> eventRegistry = new HashMap<>();
    private static final Pattern namePattern = Pattern.compile("[a-z_]+[a-zA-Z0-9_$]*");

    /**
     * Registers a event which can be listened to from groovy
     *
     * @param namespace  The name space of the event. Usually the mod id.
     * @param name       The name of the event f.e. onBlockBreak
     *                   In groovy you can then call events.[namespace].onBlockBreak to add an event listener
     * @param cancelable if the event can be canceled by returning true
     */
    public static void registerEvent(String namespace, String name, boolean cancelable) {
        validateEventName(namespace, name);
        eventRegistry.computeIfAbsent(namespace, GroovyEvent.Group::new).registerEvent(namespace, name, cancelable);
    }

    /**
     * Registers a event with an empty namespace. Can be accessed from groovy via events.[name]
     *
     * @param name       event name
     * @param cancelable if the event can be canceled by returning true
     */
    public static void registerEvent(String name, boolean cancelable) {
        registerEvent("", name, cancelable);
    }

    /**
     * Returns if such an event was registered
     *
     * @param namespace namespace of the event
     * @param name      event to check for
     * @return if the event exists
     */
    public static boolean eventExists(String namespace, String name) {
        try {
            return getEvent(namespace, name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registers a listener. Automatically called by the groovy sandbox
     *
     * @param namespace namespace of the event
     * @param name      event name
     * @param closure   event consumer
     */
    public static void registerListener(String namespace, String name, Closure<Object> closure) {
        applyEventAction(namespace, (group) -> {
            group.registerListener(name, closure);
            return null;
        });
    }

    /**
     * Removes all current listeners for the specified event
     *
     * @param namespace namespace of the event
     * @param name      event name to remove listeners for
     */
    public static void clearListeners(String namespace, String name) {
        applyEventAction(namespace, (group) -> {
            group.clearListeners(name);
            return null;
        });
    }

    /**
     * Removes all event listeners for all events
     */
    public static void clearListeners() {
        eventRegistry.values().forEach(GroovyEvent.Group::clearListeners);
    }

    @Nullable
    public static GroovyEvent.Group getEventGroup(String group) {
        return eventRegistry.get(group);
    }

    /**
     * Invokes all listeners for an event. The arguments should always be passed with the same amount and in the same order.
     *
     * @param namespace namespace of the event
     * @param name      event name to invoke
     * @param args      event arguments
     */
    public static void invokeEvent(String namespace, String name, Object... args) {
        GroovyEvent groovyEvent = getEvent(namespace, name);
        if (groovyEvent == null) {
            throw new NoSuchElementException("There is no such GroovyEvent '" + name + "' in group '" + namespace + "'!");
        }
        List<Closure<Object>> closures = applyEventAction(namespace, group -> group.getListeners(name));
        if (!closures.isEmpty()) {
            for (Closure<Object> closure : closures) {
                Object result = SandboxRunner.runClosure(closure, args);
                if (groovyEvent.isCancelable() && result == Boolean.TRUE) {
                    break;
                }
            }
        }
    }

    public static void invokeEvent(String name, Object... args) {
        invokeEvent("", name, args);
    }

    private static <T> T applyEventAction(String namespace, Function<GroovyEvent.Group, T> consumer) {
        GroovyEvent.Group group = getEventGroup(namespace);
        if (group == null) {
            throw new NoSuchElementException("There is no such event group '" + namespace + "'!");
        }
        return consumer.apply(group);
    }

    @Nullable
    private static GroovyEvent getEvent(String namespace, String name) {
        return applyEventAction(namespace, group -> group.getEvent(name));
    }

    private static void validateEventName(String namespace, String name) {
        if (!namespace.isEmpty() && !namePattern.matcher(namespace).matches()) {
            throw new IllegalArgumentException("Event namespace must start with underscore or a lowercase letter and must be followed by numbers, letters or underscores");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Event name must no be empty!");
        }
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Event name must start with underscore or a lowercase letter and must be followed by numbers, letters or underscores");
        }
    }
}
