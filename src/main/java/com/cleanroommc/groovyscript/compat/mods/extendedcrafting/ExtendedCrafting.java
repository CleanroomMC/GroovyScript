package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class ExtendedCrafting extends ModPropertyContainer {

    public final TableCrafting tableCrafting = new TableCrafting();
    public final EnderCrafting enderCrafting = new EnderCrafting();
    public final CombinationCrafting combinationCrafting = new CombinationCrafting();
    public final CompressionCrafting compressionCrafting = new CompressionCrafting();

    public ExtendedCrafting() {
        addRegistry(tableCrafting);
        addRegistry(enderCrafting);
        addRegistry(combinationCrafting);
        addRegistry(compressionCrafting);
    }
}
