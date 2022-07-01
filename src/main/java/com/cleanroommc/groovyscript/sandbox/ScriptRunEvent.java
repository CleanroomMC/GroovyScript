package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ScriptRunEvent extends Event {

    public static class Pre extends ScriptRunEvent {
    }

    public static class Post extends ScriptRunEvent {
    }
}
