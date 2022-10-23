package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.GroovyScript;

public class VanillaModule {

    public static final Crafting crafting = new Crafting();
    public static final Furnace furnace = new Furnace();
    public static final OreDict oreDict = new OreDict();

    public static void initializeBinding() {
        GroovyScript.getSandbox().registerBinding("crafting", crafting);
        GroovyScript.getSandbox().registerBinding("Crafting", crafting);
        GroovyScript.getSandbox().registerBinding("furnace", furnace);
        GroovyScript.getSandbox().registerBinding("Furnace", furnace);
        GroovyScript.getSandbox().registerBinding("oredict", oreDict);
        GroovyScript.getSandbox().registerBinding("OreDict", oreDict);
        GroovyScript.getSandbox().registerBinding("OreDictionary", oreDict);
    }

}
