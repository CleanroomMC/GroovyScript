package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Kiln extends StandardListRegistry<ICrushingTableRecipe> {

    @RecipeBuilderDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ICrushingTableRecipe> getRecipes() {
        return MagneticraftApi.getCrushingTableRecipeManager().getRecipes();
    }

    @MethodDescription
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutput()) && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICrushingTableRecipe> {

        @Property
        private boolean oreDict;

        @RecipeBuilderMethodDescription
        public RecipeBuilder oreDict(boolean oreDict) {
            this.oreDict = oreDict;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder oreDict() {
            this.oreDict = !oreDict;
            return this;
        }

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
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = MagneticraftApi.getCrushingTableRecipeManager().createRecipe(stack, output.get(0), oreDict);
                ModSupport.MAGNETICRAFT.get().crushingTable.add(recipe);
            }
            return recipe;
        }
    }

}
