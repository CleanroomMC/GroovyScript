package com.cleanroommc.groovyscript.sandbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LoadStage {

    private static final List<LoadStage> STAGES = new ArrayList<>();

    public static List<LoadStage> getLoadStages() {
        return Collections.unmodifiableList(STAGES);
    }

    public static final LoadStage PRE_INIT = new LoadStage("preInit", false, -1000000);
    public static final LoadStage INIT = new LoadStage("init", false, -1000);
    public static final LoadStage POST_INIT = new LoadStage("postInit", true, 0);

    private final String name;
    private final boolean reloadable;
    private final int priority;

    public LoadStage(String name, boolean reloadable, int priority) {
        this.name = name;
        this.reloadable = reloadable;
        this.priority = priority;
        STAGES.add(this);
        STAGES.sort(Comparator.comparingInt(LoadStage::getPriority));
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

    @Override
    public String toString() {
        return name;
    }
}
