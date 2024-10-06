package com.cleanroommc.groovyscript.event;

import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ScriptRunEvent extends Event {

    private final LoadStage loadStage;

    public ScriptRunEvent(LoadStage loadStage) {
        this.loadStage = loadStage;
    }

    public LoadStage getLoadStage() {
        return loadStage;
    }

    /**
     * Called before anything on script run (first load and reload)
     */
    public static class Pre extends ScriptRunEvent {

        public Pre(LoadStage loadStage) {
            super(loadStage);
        }
    }

    /**
     * Called after anything on script run (first load and reload)
     */
    public static class Post extends ScriptRunEvent {

        public Post(LoadStage loadStage) {
            super(loadStage);
        }
    }
}
