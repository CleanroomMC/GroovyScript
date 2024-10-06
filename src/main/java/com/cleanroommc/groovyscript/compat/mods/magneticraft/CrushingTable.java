package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.magneticraft.CrushingTableRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class CrushingTable extends StandardListRegistry<ICrushingTableRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    /**
     * Internally, the API method uses {@link java.util.Collections#unmodifiableList},
     * so a mixin is required to access the actual list and avoid {@link UnsupportedOperationException}s.
     *
     * @see com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager#getRecipes() MagneticraftApi.getCrushingTableRecipeManager().getRecipes()
     */
    @Override
    public Collection<ICrushingTableRecipe> getRecipes() {
        return CrushingTableRecipeManagerAccessor.getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ore')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICrushingTableRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Magneticraft Crushing Table recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICrushingTableRecipe register() {
            if (!validate()) return null;
            ICrushingTableRecipe recipe = null;
            if (input.get(0) instanceof OreDictIngredient ore) {
                recipe = MagneticraftApi.getCrushingTableRecipeManager().createRecipe(ore.getMatchingStacks()[0], output.get(0), true);
                ModSupport.MAGNETICRAFT.get().crushingTable.add(recipe);
            } else {
                for (var stack : input.get(0).getMatchingStacks()) {
                    recipe = MagneticraftApi.getCrushingTableRecipeManager().createRecipe(stack, output.get(0), false);
                    ModSupport.MAGNETICRAFT.get().crushingTable.add(recipe);
                }
            }
            return recipe;
        }
    }
}
