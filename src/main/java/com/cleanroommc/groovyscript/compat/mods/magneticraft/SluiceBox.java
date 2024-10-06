package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class SluiceBox extends StandardListRegistry<ISluiceBoxRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).chances(0.5)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).chances(0.5, 0.3, 0.2, 0.1)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ISluiceBoxRecipe> getRecipes() {
        return MagneticraftApi.getSluiceBoxRecipeManager().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:sand')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> r.getOutputs().stream().map(Pair::getFirst).anyMatch(output) && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ISluiceBoxRecipe> {

        @Property(comp = @Comp(gt = 0, lte = 100, unique = "groovyscript.wiki.magneticraft.sluice_box.chances.required"))
        private final FloatArrayList chances = new FloatArrayList();
        @Property("groovyscript.wiki.magneticraft.oreDict.value")
        private boolean oreDict;

        @RecipeBuilderMethodDescription
        public RecipeBuilder chances(float chance) {
            this.chances.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chances(float... chances) {
            for (float chance : chances) {
                chances(chance);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder chances(Collection<Float> chances) {
            for (float chance : chances) {
                chances(chance);
            }
            return this;
        }

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
            return "Error adding Magneticraft Sluice Box recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, Integer.MAX_VALUE);
            validateFluids(msg);
            msg.add(output.size() != chances.size(), "output and chances must be the same length, yet output was {} and chances was {}", output.size(), chances.size());
            chances.trim();
            for (float chance : chances.elements()) {
                msg.add(chance <= 0 || chance > 1, "each chance value must be a float greater than 0 and less than or equal to 1, yet a chance value was {}", chance);
            }
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ISluiceBoxRecipe register() {
            if (!validate()) return null;
            ISluiceBoxRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = MagneticraftApi.getSluiceBoxRecipeManager().createRecipe(stack, output, chances, oreDict);
                ModSupport.MAGNETICRAFT.get().sluiceBox.add(recipe);
            }
            return recipe;
        }

    }

}
