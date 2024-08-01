package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class BetterWithMods extends GroovyPropertyContainer {

    public final AnvilCrafting anvilCrafting = new AnvilCrafting();
    public final Cauldron cauldron = new Cauldron();
    public final Crucible crucible = new Crucible();
    public final Kiln kiln = new Kiln();
    public final MillStone millStone = new MillStone();
    public final Saw saw = new Saw();
    public final Turntable turntable = new Turntable();
    public final Heat heat = new Heat();
    public final Hopper hopper = new Hopper();
    public final HopperFilters hopperFilters = new HopperFilters();


    /**
     * Used to obtain the JEI category UIDs for categories with multiple levels of heat.
     *
     * @see betterwithmods.module.compat.jei.JEI#getHeatUID(String, int)
     */
    public static String getHeatUID(String base, int heat) {
        if (heat == 1) return base;
        if (heat == 2) return String.format("%s.%s", base, "stoked");
        return String.format("%s.%s", base, heat);
    }

}
