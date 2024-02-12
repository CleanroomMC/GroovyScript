package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompactingBinRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompostBinRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;

public class CompostBin extends VirtualizedRegistry<CompostBinRecipe> {
    @Override
    public void onReload() {
        ForgeRegistry<CompostBinRecipe> registry = (ForgeRegistry<CompostBinRecipe>) ModuleTechBasic.Registries.COMPOST_BIN_RECIPE;
        if (registry.isLocked()) {
            registry.unfreeze();
        }
        getScriptedRecipes().forEach(recipe -> {
            registry.remove(recipe.getRegistryName());
        });
        getBackupRecipes().forEach(registry::register);
    }

    public void add(CompostBinRecipe recipe) {
        if (recipe != null && ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.getValue(recipe.getRegistryName()) == null) {
            addScripted(recipe);
            ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.register(recipe);
        }
    }

    public CompostBinRecipe add(ItemStack input, ItemStack output, int compostValue, String resource) {
        ResourceLocation location = CompostBinRecipe.getResourceLocation(resource, input, input.getMetadata());
        CompostBinRecipe recipe = new CompostBinRecipe(input, output, compostValue).setRegistryName(location);
        add(recipe);
        return recipe;
    }

    public void add(ItemStack input, ItemStack output, int compostValue) {
        add(input, output, compostValue, GroovyScript.getRunConfig().getPackId());
    }

    public boolean remove(CompostBinRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.remove(recipe.getRegistryName());
        return true;
    }

    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compost bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getInput().isItemEqual(input)) {
                remove(recipe);
            }
        });
    }

    public void removeByOutput(ItemStack output) {
        if (GroovyLog.msg("Error removing compost bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.getValuesCollection().forEach(recipe -> {
            if (recipe.getOutput().isItemEqual(output)) {
                remove(recipe);
            }
        });
    }

    public void removeAll() {
        ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.getValuesCollection().forEach(this::addBackup);
        ModuleTechBasic.Registries.COMPOST_BIN_RECIPE.clear();
    }

}
