package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientRecipeComponent;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientsAndFluidStackRecipeComponent;

public class MechanicalSqueezer extends VirtualizedRegistry<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    public MechanicalSqueezer() {
        super();
    }

    public Squeezer.RecipeBuilder recipeBuilder() {
        return new Squeezer.RecipeBuilder().mechanical();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    public boolean removeByInput(ItemStack input) {
        return org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    public SimpleObjectStream<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }
}
