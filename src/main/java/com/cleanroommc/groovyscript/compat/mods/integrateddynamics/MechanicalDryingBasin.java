package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientAndFluidStackRecipeComponent;

public class MechanicalDryingBasin extends VirtualizedRegistry<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    public MechanicalDryingBasin() {
        super();
    }

    public DryingBasin.RecipeBuilder recipeBuilder() {
        return new DryingBasin.RecipeBuilder().mechanical();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    public boolean removeByInput(ItemStack input) {
        return org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByOutput(ItemStack input) {
        return org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    public SimpleObjectStream<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }
}
