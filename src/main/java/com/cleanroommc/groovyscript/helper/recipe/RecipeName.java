package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.util.ResourceLocation;

public class RecipeName {

    private static int nextId = -1;
    private static final String prefix = "groovyscript_";

    public static String generate() {
        return generate(prefix);
    }

    public static String generate(String prefix) {
        return prefix + Integer.toHexString(nextId--);
    }

    public static ResourceLocation generateRl(String prefix) {
        return new ResourceLocation(GroovyScript.ID, prefix + Integer.toHexString(nextId--));
    }
}
