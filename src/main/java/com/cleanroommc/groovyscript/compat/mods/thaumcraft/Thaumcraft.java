package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.brackets.AspectBracketHandler;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane.ArcaneWorkbench;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.Aspect;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectHelper;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectItemStackExpansion;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp.Warp;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp.WarpItemStackExpansion;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

import java.util.Collection;

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

    public final AspectHelper aspectHelper = new AspectHelper();

    public Thaumcraft() {
        addRegistry(crucible);
        addRegistry(infusionCrafting);
        addRegistry(lootBag);
        addRegistry(dustTrigger);
        addRegistry(smeltingBonus);
        addRegistry(warp);
        addRegistry(aspectHelper);
        VirtualizedRegistry.putAll("ArcaneWorkbench", arcaneWorkbench, altNames);
        VirtualizedRegistry.putAll("Aspect", aspect, altNames);
        VirtualizedRegistry.putAll("Research", research, altNames);
    }

    @Override
    public void initialize() {
        BracketHandlerManager.registerBracketHandler("aspect", AspectBracketHandler.INSTANCE);
        BracketHandlerManager.registerBracketHandler("crystal", s -> {
            thaumcraft.api.aspects.Aspect aspect = thaumcraft.api.aspects.Aspect.getAspect(s);
            return aspect == null ? null : ThaumcraftApiHelper.makeCrystal(aspect);
        });
        ExpansionHelper.mixinClass(ItemStack.class, AspectItemStackExpansion.class);
        ExpansionHelper.mixinClass(ItemStack.class, WarpItemStackExpansion.class);
    }

    @Override
    public @Nullable Object getProperty(String name) {
        Object o = super.getProperty(name);
        return o != null ? o : altNames.get(name);
    }

    public static AspectList makeAspectList(Collection<AspectStack> aspects) {
        AspectList list = new AspectList();
        for (AspectStack aspectStack : aspects) {
            list.add(aspectStack.getAspect(), aspectStack.getAmount());
        }
        return list;
    }

}
