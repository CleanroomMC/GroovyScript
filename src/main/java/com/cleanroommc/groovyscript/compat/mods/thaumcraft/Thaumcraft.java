package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Thaumcraft extends ModPropertyContainer {

    public final Crucible crucible = new Crucible();
    public final InfusionCrafting infusionCrafting = new InfusionCrafting();
    public final ArcaneWorkbench arcaneWorkbench = new ArcaneWorkbench();

    public Thaumcraft() {
        addRegistry(crucible);
        addRegistry(infusionCrafting);
        addRegistry(arcaneWorkbench);
    }
}