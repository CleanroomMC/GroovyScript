package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import groovy.lang.Closure;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@GroovyBlacklist
public final class GroovyEventManager extends GroovyEvent.Group {

    public static final GroovyEventManager MAIN;

    private static final Pattern namePattern = Pattern.compile("[a-z_]+[a-zA-Z0-9_$]*");
    private static final List<GroovyEventManager> eventManagers;

    static {
        eventManagers = new ArrayList<>();
        MAIN = new GroovyEventManager();
    }

    private boolean disposed = false;

    public GroovyEventManager() {
        super("");
        eventManagers.add(this);
    }

    /**
     * Registers a event which can be listened to from groovy
     *
     * @param fullName   The name of the event f.e. onBlockBreak
     *                   In groovy you can then call events.[namespace].onBlockBreak to add an event listener
     * @param cancelable if the event can be canceled by returning true
     */
    public void registerEvent(String fullName, boolean cancelable) {
        checkDisposed();
        validateEventName(fullName);
        registerEvent(fullName, fullName, cancelable);
    }

    /**
     * Returns if such an event was registered
     *
     * @param event event to check for
     * @return if the event exists
     */
    public boolean eventExists(String event) {
        return !disposed && getEvent(event) != null;
    }

    /**
     * Registers a listener. Automatically called by the groovy sandbox
     *
     * @param event   event name
     * @param closure event consumer
     */
    public boolean registerListener(String event, Closure<Object> closure) {
        checkDisposed();
        return super.registerListener(event, closure);
    }

    /**
     * Removes all current listeners for the specified event
     *
     * @param event event name to remove listeners for
     */
    public void clearListeners(String event) {
        super.clearListeners(event);
    }

    /**
     * Removes all event listeners for all events
     */
    public void clearListeners() {
        super.clearListeners();
    }

    /**
     * Invalidates the manager. Need to check how useful this is.
     */
    @ApiStatus.Experimental
    public void dispose() {
        checkDisposed();
        this.disposed = true;
        eventManagers.remove(this);
    }

    private static void validateEventName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Event name must no be empty!");
        }
        String[] parts = name.split("\\.");
        for (String part : parts) {
            if (!namePattern.matcher(part).matches()) {
                throw new IllegalArgumentException("Event name parts (" + name + ") must start with underscore or a lowercase letter and must be followed by numbers, letters or underscores");
            }
        }
    }

    private void checkDisposed() {
        if (disposed) {
            throw new IllegalStateException("Event manager has been disposed! This action can not be performed!");
        }
    }

    public static void clearAllListeners() {
        eventManagers.forEach(GroovyEventManager::clearListeners);
    }
}
