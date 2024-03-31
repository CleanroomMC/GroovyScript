package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ExtraUtils2 extends ModPropertyContainer {

    public final Resonator resonator = new Resonator();
    public final Crusher crusher = new Crusher();
    public final Enchanter enchanter = new Enchanter();
    public final Furnace furnace = new Furnace();
    public final GridPowerPassiveGenerator gridPowerPassiveGenerator = new GridPowerPassiveGenerator();
    public final Generator generator = new Generator();

    public ExtraUtils2() {
        addRegistry(resonator);
        addRegistry(crusher);
        addRegistry(enchanter);
        addRegistry(furnace);
        addRegistry(gridPowerPassiveGenerator);
        addRegistry(generator);
    }

}
