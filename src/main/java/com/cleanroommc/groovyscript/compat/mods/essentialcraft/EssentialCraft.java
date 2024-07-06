package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class EssentialCraft extends ModPropertyContainer {
    public final DemonTradeManager demonTrade = new DemonTradeManager();
    public final MagicianTable magicianTable = new MagicianTable();
    public final MagmaticSmeltery magmaticSmeltery = new MagmaticSmeltery();
    public final MithrilineFurnace mithrilineFurnace = new MithrilineFurnace();
    public final RadiatingChamber radiatingChamber = new RadiatingChamber();
    public final WindRune windRune = new WindRune();

    public EssentialCraft() {
        addRegistry(demonTrade);
        addRegistry(magicianTable);
        addRegistry(magmaticSmeltery);
        addRegistry(mithrilineFurnace);
        addRegistry(radiatingChamber);
        addRegistry(windRune);
    }
}
