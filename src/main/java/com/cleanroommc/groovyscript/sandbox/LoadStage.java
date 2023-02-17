package com.cleanroommc.groovyscript.sandbox;

public class LoadStage {

    public static final LoadStage PRE_INIT = new LoadStage("preInit", false);
    public static final LoadStage POST_INIT = new LoadStage("postInit", false);

    private final String name;
    private final boolean reloadable;

    public LoadStage(String name, boolean reloadable) {
        this.name = name;
        this.reloadable = reloadable;
    }

    public String getName() {
        return name;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    @Override
    public String toString() {
        return name;
    }
}
