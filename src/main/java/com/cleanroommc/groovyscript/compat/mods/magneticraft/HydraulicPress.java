package com.cleanroommc.groovyscript.compat.mods.magneticraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.magneticraft.HydraulicPressRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode;
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


@RegistryDescription
public class HydraulicPress extends StandardListRegistry<IHydraulicPressRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).ticks(50)"),
            @Example(value = ".input(item('minecraft:diamond')).output(item('minecraft:clay')).ticks(100).mode(HydraulicPressMode.HEAVY)", imports = "com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    /**
     * Internally, the API method uses {@link kotlin.collections.CollectionsKt#toList} and then {@link java.util.Collections#synchronizedList},
     * meaning that you get a synchronized copy of the list instead of the actual list.
     * Thus, a mixin is required to access the actual list.
     *
     * @see com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager#getRecipes() MagneticraftApi.getHydraulicPressRecipeManager().getRecipes()
     */
    @Override
    public Collection<IHydraulicPressRecipe> getRecipes() {
        return HydraulicPressRecipeManagerAccessor.getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && addBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && addBackup(r));
    }

    @MethodDescription(example = @Example(value = "HydraulicPressMode.MEDIUM", imports = "com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode"))
    public boolean removeByMode(HydraulicPressMode mode) {
        return getRecipes().removeIf(r -> mode == r.getMode() && addBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IHydraulicPressRecipe> {

        @Property(comp = @Comp(gt = 0))
        private float ticks;
        @Property(comp = @Comp(not = "null"))
        private HydraulicPressMode mode = HydraulicPressMode.LIGHT;
        @Property("groovyscript.wiki.magneticraft.oreDict.value")
        private boolean oreDict;

        @RecipeBuilderMethodDescription
        public RecipeBuilder ticks(float ticks) {
            this.ticks = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder mode(HydraulicPressMode mode) {
            this.mode = mode;
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
            return "Error adding Magneticraft Hydraulic Press recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(mode == null, "mode must be defined");
            msg.add(ticks <= 0, "ticks must be a float greater than 0, yet it was {}", ticks);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IHydraulicPressRecipe register() {
            if (!validate()) return null;
            IHydraulicPressRecipe recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = MagneticraftApi.getHydraulicPressRecipeManager().createRecipe(stack, output.get(0), ticks, mode, oreDict);
                ModSupport.MAGNETICRAFT.get().hydraulicPress.add(recipe);
            }
            return recipe;
        }

    }

}
