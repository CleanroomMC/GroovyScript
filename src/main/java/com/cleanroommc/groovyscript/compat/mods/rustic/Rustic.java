package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Rustic extends ModPropertyContainer {

    public final Alchemy alchemy = new Alchemy();
    public final BrewingBarrel brewingBarrel = new BrewingBarrel();
    public final CrushingTub crushingTub = new CrushingTub();
    public final EvaporatingBasin evaporatingBasin = new EvaporatingBasin();

    public Rustic() {
        addRegistry(alchemy);
        addRegistry(brewingBarrel);
        addRegistry(crushingTub);
        addRegistry(evaporatingBasin);
    }

}
