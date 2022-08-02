package com.cleanroommc.groovyscript.helper.recipe;

public class RecipeName {

    private static int nextId = 0;
    private static final String prefix = "groovyscript_";

    public static String generate() {
        return prefix + Integer.toHexString(nextId++);
    }
}
