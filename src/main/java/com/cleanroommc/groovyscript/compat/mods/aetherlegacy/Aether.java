package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Aether extends ModPropertyContainer {

    public static final Enchanter enchanter = new Enchanter();
    public static final EnchanterFuel enchanterFuel = new EnchanterFuel();
    public static final Freezer freezer = new Freezer();
    public static final FreezerFuel freezerFuel = new FreezerFuel();
    public static final Accessory accessory = new Accessory();

    public Aether() {
        addRegistry(enchanter);
        addRegistry(enchanterFuel);
        addRegistry(freezer);
        addRegistry(freezerFuel);
        addRegistry(accessory);
    }

}
