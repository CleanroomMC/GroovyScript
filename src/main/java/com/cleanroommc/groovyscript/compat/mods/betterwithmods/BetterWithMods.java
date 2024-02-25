package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class BetterWithMods extends ModPropertyContainer {

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

    public BetterWithMods() {
        addRegistry(anvilCrafting);
        addRegistry(cauldron);
        addRegistry(crucible);
        addRegistry(kiln);
        addRegistry(millStone);
        addRegistry(saw);
        addRegistry(turntable);
        addRegistry(heat);
        addRegistry(hopper);
        addRegistry(hopperFilters);
    }

}
