package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;

public class VanillaModule {

    public static void initializeBinding() {
        GroovyScript.getSandbox().registerBinding("crafting", new Crafting());
    }

}
