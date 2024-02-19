package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin;
import org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasinConfig;

@RegistryDescription
public class MechanicalDryingBasin extends VirtualizedRegistry<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockMechanicalDryingBasinConfig.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 20000).duration(300)"), requirement = @Property(property = "mechanical", defaultValue = "true"))
    public DryingBasin.RecipeBuilder recipeBuilder() {
        return new DryingBasin.RecipeBuilder().mechanical();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput")
    public boolean removeByInput(ItemStack input) {
        return BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput")
    public boolean removeByOutput(ItemStack input) {
        return BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }
}
