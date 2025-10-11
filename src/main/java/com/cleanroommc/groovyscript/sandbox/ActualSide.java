package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.relauncher.Side;

public enum ActualSide {

    PHYSICAL_CLIENT("PH_CLIENT", "CLIENT", true, true),
    PHYSICAL_SERVER("PH_SERVER", "SERVER", true, false),
    LOGICAL_CLIENT("LO_CLIENT", "CLIENT", false, true),
    LOGICAL_SERVER("LO_SERVER", "SERVER", false, false);

    private final String name, shortName;
    private final boolean physical, client;

    ActualSide(String name, String shortName, boolean physical, boolean client) {
        this.name = name;
        this.shortName = shortName;
        this.physical = physical;
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isClient() {
        return client;
    }

    public boolean isServer() {
        return !client;
    }

    public boolean isPhysical() {
        return physical;
    }

    public boolean isLogical() {
        return !physical;
    }

    public static ActualSide ofPhysicalSide(Side side) {
        return side.isClient() ? PHYSICAL_CLIENT : PHYSICAL_SERVER;
    }

    public static ActualSide ofLogicalSide(Side side) {
        return side.isClient() ? LOGICAL_CLIENT : LOGICAL_SERVER;
    }
}
