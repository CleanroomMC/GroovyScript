package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.sandbox.SandboxRunner;

public class VanillaModule {

    public static final Crafting crafting = new Crafting();
    public static final Furnace furnace = new Furnace();

    public static void initializeBinding() {
        SandboxRunner.registerBinding("crafting", crafting);
        SandboxRunner.registerBinding("Crafting", crafting);
        SandboxRunner.registerBinding("furnace", furnace);
        SandboxRunner.registerBinding("Furnace", furnace);
    }

}
