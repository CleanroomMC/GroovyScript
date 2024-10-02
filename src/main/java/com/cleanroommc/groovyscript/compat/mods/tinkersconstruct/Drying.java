package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.DryingRecipe;

import java.util.Collection;

@RegistryDescription
public class Drying extends StandardListRegistry<DryingRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:dirt')).time(45)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<DryingRecipe> getRecipes() {
        return TinkerRegistryAccessor.getDryingRegistry();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public DryingRecipe add(IIngredient input, ItemStack output, int time) {
        DryingRecipe recipe = new DryingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input), output, time);
        add(recipe);
        return recipe;
    }

    @MethodDescription
    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(matching).isPresent();
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Drying recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription
    public boolean removeByOutput(ItemStack output) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = ItemStack.areItemStacksEqual(recipe.output, output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Drying recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription
    public boolean removeByInputAndOutput(IIngredient input, ItemStack output) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.input.matches(matching).isPresent() && ItemStack.areItemStacksEqual(recipe.output, output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Drying recipe")
                .add("could not find recipe with input {} and output {}", input, output)
                .error()
                .post();
        return false;
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public class RecipeBuilder extends AbstractRecipeBuilder<DryingRecipe> {

        @Property(defaultValue = "20", valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time = 20;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tinkers Construct Drying recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(time < 1, "Recipe time must be at least 1, got " + time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DryingRecipe register() {
            if (!validate()) return null;
            DryingRecipe recipe = new DryingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input.get(0)), output.get(0), time);
            add(recipe);
            return recipe;
        }
    }
}
