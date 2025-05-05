package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientRecipeComponent;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientsAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer;
import org.cyclops.integrateddynamics.block.BlockMechanicalSqueezerConfig;

import java.util.Collection;

@RegistryDescription
public class MechanicalSqueezer extends StandardListRegistry<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockMechanicalSqueezerConfig.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay') * 16, 0.9F)"), override = @RecipeBuilderOverride(requirement = @Property(property = "mechanical", defaultValue = "true")))
    public Squeezer.RecipeBuilder recipeBuilder() {
        return new Squeezer.RecipeBuilder().mechanical();
    }

    @Override
    public Collection<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DurationRecipeProperties>> getRecipes() {
        return BlockMechanicalSqueezer.getInstance().getRecipeRegistry().allRecipes();
    }

    @MethodDescription
    public boolean removeByInput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }
}
