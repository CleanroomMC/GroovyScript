package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.sandbox.SandboxRunner;

public class VanillaModule {

    public static void initializeBinding() {
        SandboxRunner.registerBinding("crafting", new Crafting());
    }

}
