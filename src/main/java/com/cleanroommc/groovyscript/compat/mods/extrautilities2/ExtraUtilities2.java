package com.cleanroommc.groovyscript.compat.mods.extrautilities2;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ExtraUtilities2 extends ModPropertyContainer {

    public final Resonator resonator = new Resonator();
    public final Crusher crusher = new Crusher();
    public final Enchanter enchanter = new Enchanter();
    public final Furnace furnace = new Furnace();

    public ExtraUtilities2() {
        addRegistry(resonator);
        addRegistry(crusher);
        addRegistry(enchanter);
        addRegistry(furnace);
    }
}
