package com.cleanroommc.groovyscript.compat.mods.thaumcraft.arcane;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ArcaneWorkbench {
public class ArcaneWorkbench extends VirtualizedRegistry<Void> {

    public static final ResourceLocation DEFAULT = new ResourceLocation("");

    @Override
    @GroovyBlacklist
    public void onReload() {
        // do nothing
    }

    public void add(String name, IRecipe recipe) {
        ReloadableRegistryManager.addRegistryEntry(ForgeRegistries.RECIPES, name, recipe);
    }

    public void remove(String name) {
        ReloadableRegistryManager.removeRegistryEntry(ForgeRegistries.RECIPES, name);
    }

    public void removeByOutput(IIngredient output) {
        VanillaModule.crafting.removeByOutput(output, true);
    }

    public ArcaneRecipeBuilder.Shaped shapedBuilder() {
        return new ArcaneRecipeBuilder.Shaped();
    }

    public ArcaneRecipeBuilder.Shapeless shapelessBuilder() {
        return new ArcaneRecipeBuilder.Shapeless();
    }

}

