package com.cleanroommc.groovyscript.compat.mods.arcanearchives;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ArcaneArchives extends ModPropertyContainer {

    public final GemCuttingTable gemCuttingTable = new GemCuttingTable();

    public ArcaneArchives() {
        addRegistry(gemCuttingTable);
    }

}
