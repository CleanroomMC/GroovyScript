package com.cleanroommc.groovyscript.sandbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum LoadStage {

    MIXIN_EARLY("mixin_early", false, -2000000000),
    MIXIN_LATE("mixin_late", false, -1000000000),
    PRE_INIT("preInit", false, -1000000),
    INIT("init", false, -1000),
    POST_INIT("postInit", true, 0);

    private static List<LoadStage> stages;

    public static List<LoadStage> getLoadStages() {
        if (stages == null) {
            stages = new ArrayList<>();
            Collections.addAll(stages, values());
        }
        return Collections.unmodifiableList(stages);
    }

    private final String name;
    private final boolean reloadable;
    private final int priority;

    LoadStage(String name, boolean reloadable, int priority) {
        this.name = name;
        this.reloadable = reloadable;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isMixin() {
        return this == MIXIN_EARLY || this == MIXIN_LATE;
    }

    @Override
    public String toString() {
        return name;
    }
}
