package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin;
import org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasinConfig;

import java.util.Collection;

@RegistryDescription
public class MechanicalDryingBasin extends StandardListRegistry<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockMechanicalDryingBasinConfig.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 20000).duration(300)"), override = @RecipeBuilderOverride(requirement = @Property(property = "mechanical", defaultValue = "true")))
    public DryingBasin.RecipeBuilder recipeBuilder() {
        return new DryingBasin.RecipeBuilder().mechanical();
    }

    @Override
    public Collection<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> getRecipes() {
        return BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().allRecipes();
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

    @MethodDescription
    public boolean removeByOutput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }
}
