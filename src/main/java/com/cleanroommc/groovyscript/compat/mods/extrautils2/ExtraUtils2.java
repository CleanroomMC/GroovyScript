package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ExtraUtils2 extends ModPropertyContainer {

    public final Resonator resonator = new Resonator();
    public final Crusher crusher = new Crusher();
    public final Enchanter enchanter = new Enchanter();
    public final Furnace furnace = new Furnace();
    public final GridPowerPassiveGenerator gridPowerPassiveGenerator = new GridPowerPassiveGenerator();

    public ExtraUtils2() {
        addRegistry(resonator);
        addRegistry(crusher);
        addRegistry(enchanter);
        addRegistry(furnace);
        addRegistry(gridPowerPassiveGenerator);
//        addRegistry(Generator.furnace);
//        addRegistry(Generator.survivalist);
//        addRegistry(Generator.culinary);
//        addRegistry(Generator.potion);
//        addRegistry(Generator.tnt);
//        addRegistry(Generator.lava);
//        addRegistry(Generator.pink);
//        addRegistry(Generator.netherstar);
//        addRegistry(Generator.ender);
//        addRegistry(Generator.redstone);
//        addRegistry(Generator.overclock);
//        addRegistry(Generator.dragon);
//        addRegistry(Generator.ice);
//        addRegistry(Generator.death);
//        addRegistry(Generator.enchant);
//        addRegistry(Generator.slime);
    }
}
