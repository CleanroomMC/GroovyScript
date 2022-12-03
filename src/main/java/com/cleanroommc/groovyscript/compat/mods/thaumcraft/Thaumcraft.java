package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane.ArcaneWorkbench;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.Aspect;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

public class Thaumcraft extends ModPropertyContainer {

    private final Object2ObjectOpenHashMap<String, Object> altNames = new Object2ObjectOpenHashMap<>();

    public final Crucible crucible = new Crucible();
    public final InfusionCrafting infusionCrafting = new InfusionCrafting();
    public final ArcaneWorkbench arcaneWorkbench = new ArcaneWorkbench();
    public final Aspect aspect = new Aspect();
    public final LootBag lootBag = new LootBag();
    public final Warp warp = new Warp();
    public final SmeltingBonus smeltingBonus = new SmeltingBonus();
    public final Research research = new Research();
    public final DustTrigger dustTrigger = new DustTrigger();

    public AspectHelper AspectHelper() {
        return new AspectHelper();
    }

    public Thaumcraft() {
        addRegistry(crucible);
        addRegistry(infusionCrafting);
        altNames.put("ArcaneWorkbench", arcaneWorkbench);
        altNames.put("arcane_workbench", arcaneWorkbench);
        altNames.put("Aspect", aspect);
        altNames.put("LootBag", lootBag);
        altNames.put("loot_bag", lootBag);
        altNames.put("Warp", warp);
        altNames.put("SmeltingBonus", smeltingBonus);
        altNames.put("smelting_bonus", smeltingBonus);
        altNames.put("Research", research);
        altNames.put("DustTrigger", dustTrigger);
        altNames.put("dust_trigger", dustTrigger);
    }

    @Override
    public @Nullable Object getProperty(String name) {
        Object o = super.getProperty(name);
        return o != null ? o : altNames.get(name);
    }
}
