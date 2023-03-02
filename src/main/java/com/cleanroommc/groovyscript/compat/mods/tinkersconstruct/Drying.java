package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.DryingRecipe;

public class Drying extends VirtualizedRegistry<DryingRecipe> {
    public Drying() {
        super();
    }

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
        DryingRecipe recipe = new DryingRecipe(RecipeMatch.of(input.getMatchingStacks()[0]), output, time);
        add(recipe);
        return recipe;
    }

    public DryingRecipe add(String oreDict, ItemStack output, int time) {
        DryingRecipe recipe = new DryingRecipe(RecipeMatch.of(oreDict), output, time);
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
        if (TinkerRegistryAccessor.getDryingRegistry().removeIf(recipe -> {
            boolean found = input.test(recipe.input.getInputs().get(0));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Drying recipe")
                .add("could not find recipe with input %s", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        if (TinkerRegistryAccessor.getDryingRegistry().removeIf(recipe -> {
            boolean found = recipe.output.isItemEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Drying recipe")
                .add("could not find recipe with output %s", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputAndOutput(IIngredient input, ItemStack output) {
        if (TinkerRegistryAccessor.getDryingRegistry().removeIf(recipe -> {
            boolean found = input.test(recipe.input.getInputs().get(0)) && recipe.output.isItemEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Drying recipe")
                .add("could not find recipe with input %s and output %s", input, output)
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
        private String oreDict;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public RecipeBuilder input(String oreDict) {
            this.oreDict = oreDict;
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
            RecipeMatch match = oreDict != null && !oreDict.isEmpty() ? RecipeMatch.of(oreDict) : RecipeMatch.of(input.get(0).getMatchingStacks()[0]);
            DryingRecipe recipe = new DryingRecipe(match, output.get(0), time);
            add(recipe);
            return recipe;
        }
    }
}
