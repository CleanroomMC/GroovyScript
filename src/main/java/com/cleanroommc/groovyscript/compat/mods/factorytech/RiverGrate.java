package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class RiverGrate extends StandardListRegistry<MachineRecipes.MachineRecipe<Double, ItemStack>> {

    @Override
    public Collection<MachineRecipes.MachineRecipe<Double, ItemStack>> getRecipes() {
        return MachineRecipes.RIVER_GRATE;
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:diamond')).weight(10)"),
            @Example(".output(item('minecraft:clay')).allowStoneParts().weight(30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:fish')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutputStack()) && doAddBackup(r));
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MachineRecipes.MachineRecipe<Double, ItemStack>> {

        @Property("groovyscript.wiki.factorytech.allowStoneParts.value")
        private boolean allowStoneParts;
        @Property(comp = @Comp(gte = 0))
        private double weight;

        @RecipeBuilderMethodDescription
        public RecipeBuilder allowStoneParts(boolean allowStoneParts) {
            this.allowStoneParts = allowStoneParts;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder allowStoneParts() {
            this.allowStoneParts = !this.allowStoneParts;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(double weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech River Grate recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight < 0, "weight must be a non-negative double, yet it was {}", weight);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MachineRecipes.MachineRecipe<Double, ItemStack> register() {
            if (!validate()) return null;
            var recipe = new MachineRecipes.MachineRecipe<>(weight, output.get(0), allowStoneParts);
            ModSupport.FACTORY_TECH.get().riverGrate.add(recipe);
            return recipe;
        }
    }
}
