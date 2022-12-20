package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class BloodMagic extends ModPropertyContainer {

    public final BloodAltar bloodAltar = new BloodAltar();
    public final AlchemyArray alchemyArray = new AlchemyArray();
    public final TartaricForge tartaricForge = new TartaricForge();
    public final AlchemyTable alchemyTable = new AlchemyTable();
    public final Tranquility tranquility = new Tranquility();
    public final Sacrificial sacrificial = new Sacrificial();
    public final Meteor meteor = new Meteor();

    public BloodMagic() {
        addRegistry(bloodAltar);
        addRegistry(alchemyArray);
        addRegistry(tartaricForge);
        addRegistry(alchemyTable);
        addRegistry(tranquility);
        addRegistry(sacrificial);
        addRegistry(meteor);
    }

}
