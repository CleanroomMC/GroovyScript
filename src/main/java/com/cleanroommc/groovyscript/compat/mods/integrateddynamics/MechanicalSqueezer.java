package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientRecipeComponent;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientsAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer;
import org.cyclops.integrateddynamics.block.BlockMechanicalSqueezerConfig;

@RegistryDescription
public class MechanicalSqueezer extends VirtualizedRegistry<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockMechanicalSqueezerConfig.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay') * 16, 0.9F)"), requirement = @Property(property = "mechanical", defaultValue = "true"))
    public Squeezer.RecipeBuilder recipeBuilder() {
        return new Squeezer.RecipeBuilder().mechanical();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
    public boolean removeByInput(ItemStack input) {
        return BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }
}
