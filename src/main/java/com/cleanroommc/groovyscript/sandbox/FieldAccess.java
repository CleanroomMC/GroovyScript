package com.cleanroommc.groovyscript.sandbox;

public enum FieldAccess {
    GET, SET;

    public boolean isGetter() {
        return this == GET;
    }

    public boolean isSetter() {
        return this == SET;
    }
}
