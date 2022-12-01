package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class Thaumcraft extends ModPropertyContainer {

    public final Crucible crucible = new Crucible();
    public final InfusionCrafting infusionCrafting = new InfusionCrafting();
    public final ArcaneWorkbench arcaneWorkbench = new ArcaneWorkbench();
    public Aspect Aspect = new Aspect();
    public LootBag LootBag = new LootBag();
    public Warp Warp = new Warp();
    public SmeltingBonus SmeltingBonus = new SmeltingBonus();
    public Research Research = new Research();
    public DustTrigger DustTrigger = new DustTrigger();
    public AspectHelper AspectHelper() { return new AspectHelper(); }

    public Thaumcraft() {
        addRegistry(crucible);
        addRegistry(infusionCrafting);
        addRegistry(arcaneWorkbench);
    }
}
