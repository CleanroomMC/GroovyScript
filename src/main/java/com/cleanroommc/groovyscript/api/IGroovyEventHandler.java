package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import org.jetbrains.annotations.NotNull;

/**
 * Implement this on any class to make instances of that class be able to handle events.
 */
@FunctionalInterface
public interface IGroovyEventHandler {

    /**
     * Should return the event manger of this class. The manager should always be the same.
     * You should NOT create a new instance every time this is called.
     *
     * @return the event manager for this class
     */
    @GroovyBlacklist
    @NotNull
    GroovyEventManager getEventManager();
}
