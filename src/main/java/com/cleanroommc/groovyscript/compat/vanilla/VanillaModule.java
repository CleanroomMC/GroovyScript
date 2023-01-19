package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.loot.Loot;

public class VanillaModule {

    public static final Crafting crafting = new Crafting();
    public static final Furnace furnace = new Furnace();
    public static final Loot loot = new Loot();

    public static void initializeBinding() {
        GroovyScript.getSandbox().registerBinding("crafting", crafting);
        GroovyScript.getSandbox().registerBinding("Crafting", crafting);
        GroovyScript.getSandbox().registerBinding("furnace", furnace);
        GroovyScript.getSandbox().registerBinding("Furnace", furnace);
        GroovyScript.getSandbox().registerBinding("loot", loot);
        GroovyScript.getSandbox().registerBinding("Loot", loot);
    }

}
