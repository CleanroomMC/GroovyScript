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

@RegistryDescription
public class Reclaimer extends StandardListRegistry<MachineRecipes.MachineRecipe<ItemStack, ItemStack>> {

    @Override
    public Collection<MachineRecipes.MachineRecipe<ItemStack, ItemStack>> getRecipes() {
        return MachineRecipes.RECLAIMER;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).allowStoneParts()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('factorytech:salvagepart:22')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> input.test(r.input()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:iron_nugget')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutputStack()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MachineRecipes.MachineRecipe<ItemStack, ItemStack>> {

        @Property("groovyscript.wiki.factorytech.allowStoneParts.value")
        private boolean allowStoneParts;

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

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Reclaimer recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MachineRecipes.MachineRecipe<ItemStack, ItemStack> register() {
            if (!validate()) return null;
            MachineRecipes.MachineRecipe<ItemStack, ItemStack> recipe = null;
            for (var stack : input.get(0).getMatchingStacks()) {
                recipe = new MachineRecipes.MachineRecipe<>(stack, output.get(0), allowStoneParts);
                ModSupport.FACTORY_TECH.get().reclaimer.add(recipe);
            }
            return recipe;
        }
    }
}
