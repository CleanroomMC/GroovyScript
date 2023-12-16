package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.util.ResourceLocation;

public class RecipeName {

    private static int nextId = -1;
    private static String prefix = "";

    public static String generate() {
        if (prefix.isEmpty())
            prefix = GroovyScript.getRunConfig().getPackId() + "_";

        return generate(prefix);
    }

    public static String generate(String prefix) {
        return prefix + Integer.toHexString(nextId--);
    }

    public static ResourceLocation generateRl(String prefix) {
        return new ResourceLocation(GroovyScript.getRunConfig().getPackId(), generate(prefix));
    }
}
