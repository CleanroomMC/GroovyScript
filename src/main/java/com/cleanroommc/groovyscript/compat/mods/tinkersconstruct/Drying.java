package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.DryingRecipe;

public class Drying extends VirtualizedRegistry<DryingRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getDryingRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getDryingRegistry()::add);
    }

    public DryingRecipe add(IIngredient input, ItemStack output, int time) {
        DryingRecipe recipe = new DryingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input), output, time);
        add(recipe);
        return recipe;
    }

    public void add(DryingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getDryingRegistry().add(recipe);
    }

    public boolean remove(DryingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getDryingRegistry().remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (TinkerRegistryAccessor.getDryingRegistry().removeIf(recipe -> {
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

    public boolean removeByOutput(ItemStack output) {
        if (TinkerRegistryAccessor.getDryingRegistry().removeIf(recipe -> {
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

    public boolean removeByInputAndOutput(IIngredient input, ItemStack output) {
        NonNullList<ItemStack> matching = NonNullList.from(ItemStack.EMPTY, input.getMatchingStacks());
        if (TinkerRegistryAccessor.getDryingRegistry().removeIf(recipe -> {
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

    public void removeAll() {
        TinkerRegistryAccessor.getDryingRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getDryingRegistry().forEach(TinkerRegistryAccessor.getDryingRegistry()::remove);
    }

    public SimpleObjectStream<DryingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getDryingRegistry()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<DryingRecipe> {

        private int time = 20;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tinkers Construct Drying recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(time < 1, "Recipe time must be at least 1, got " + time);
        }

        @Override
        public @Nullable DryingRecipe register() {
            if (!validate()) return null;
            DryingRecipe recipe = new DryingRecipe(MeltingRecipeBuilder.recipeMatchFromIngredient(input.get(0)), output.get(0), time);
            add(recipe);
            return recipe;
        }
    }
}
