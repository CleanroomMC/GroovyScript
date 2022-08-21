package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ScriptRunEvent extends Event {

    /**
     * Called before anything on script run (first load and reload)
     */
    public static class Pre extends ScriptRunEvent {
    }

    /**
     * Called after anything on script run (first load and reload)
     */
    public static class Post extends ScriptRunEvent {
    }
}
