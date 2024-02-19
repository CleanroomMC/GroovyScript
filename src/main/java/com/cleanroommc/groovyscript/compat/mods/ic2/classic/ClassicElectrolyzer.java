package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IElectrolyzerRecipeList;
import net.minecraft.item.ItemStack;

public class ClassicElectrolyzer extends VirtualizedRegistry<ClassicElectrolyzer.ElectrolyzerRecipe> {

    public ClassicElectrolyzer() {
        super(Alias.generateOfClassAnd(ClassicElectrolyzer.class, "Electrolyzer"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    public ElectrolyzerRecipe addBoth(ItemStack output, IIngredient input, int energy) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Electrolyzer recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(energy <= 0, () -> "energy must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(RecipeType.BOTH, input, output, energy);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public ElectrolyzerRecipe addCharge(ItemStack output, IIngredient input, int energy) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Electrolyzer recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(energy <= 0, () -> "energy must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(RecipeType.CHARGE, input, output, energy);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public ElectrolyzerRecipe addDischarge(ItemStack output, IIngredient input, int energy) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Electrolyzer recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(energy <= 0, () -> "energy must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(RecipeType.DISCHARGE, input, output, energy);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public SimpleObjectStream<IElectrolyzerRecipeList.RecipeEntry> streamRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.electrolyzer.getRecipeList()).setRemover(r -> removeByInput(r.getInput()));
    }

    public boolean removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Electrolyzer recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return false;
        }
        for (IElectrolyzerRecipeList.RecipeEntry entry : ClassicRecipes.electrolyzer.getRecipeList()) {
            if (ItemStack.areItemStacksEqual(entry.getOutput(), output)) {
                ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(getFromEntry(entry), IngredientHelper.toIIngredient(entry.getInput()), entry.getOutput(), entry.getEnergy());
                remove(recipe);
                addBackup(recipe);
                return true;
            }
        }
        GroovyLog.msg("Error removing Industrialcraft 2 Electrolyzer recipe")
                .add("no recipes found for {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Electrolyzer recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        for (IElectrolyzerRecipeList.RecipeEntry entry : ClassicRecipes.electrolyzer.getRecipeList()) {
            if (ItemStack.areItemStacksEqual(entry.getInput(), input)) {
                ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(getFromEntry(entry), IngredientHelper.toIIngredient(entry.getInput()), entry.getOutput(), entry.getEnergy());
                remove(recipe);
                addBackup(recipe);
                return true;
            }
        }
        GroovyLog.msg("Error removing Industrialcraft 2 Electrolyzer recipe")
                .add("no recipes found for {}", input)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        for (IElectrolyzerRecipeList.RecipeEntry entry : ClassicRecipes.electrolyzer.getRecipeList()) {
            ElectrolyzerRecipe recipe = new ElectrolyzerRecipe(getFromEntry(entry), IngredientHelper.toIIngredient(entry.getInput()), entry.getOutput(), entry.getEnergy());
            remove(recipe);
            addBackup(recipe);
        }
    }

    private void add(ElectrolyzerRecipe recipe) {
        switch (recipe.type) {
            case CHARGE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.electrolyzer.addChargeRecipe(stack, recipe.output, recipe.energy, String.valueOf(recipe.hashCode()));
                }
                break;
            case DISCHARGE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.electrolyzer.addDischargeRecipe(stack, recipe.output, recipe.energy, String.valueOf(recipe.hashCode()));
                }
                break;
            case BOTH:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.electrolyzer.addBothRecipe(stack, recipe.output, recipe.energy, String.valueOf(recipe.hashCode()));
                }
                break;
        }
    }

    private void remove(ElectrolyzerRecipe recipe) {
        switch (recipe.type) {
            case CHARGE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.electrolyzer.removeRecipe(stack, true, false);
                    removeFromMap(stack);
                }
                break;
            case DISCHARGE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.electrolyzer.removeRecipe(stack, false, false);
                    removeFromMap(stack);
                }
                break;
            case BOTH:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.electrolyzer.removeRecipe(stack, true, true);
                    removeFromMap(stack);
                }
                break;
        }
    }

    private void removeFromMap(ItemStack stack) {
        ClassicRecipes.electrolyzer.getRecipeList().removeIf(entry -> ItemStack.areItemStacksEqual(entry.getInput(), stack));
    }

    public static class ElectrolyzerRecipe {

        public RecipeType type;
        public IIngredient input;
        public ItemStack output;
        public int energy;

        public ElectrolyzerRecipe(RecipeType type, IIngredient input, ItemStack output, int energy) {
            this.type = type;
            this.output = output;
            this.input = input;
            this.energy = energy;
        }
    }

    private RecipeType getFromEntry(IElectrolyzerRecipeList.RecipeEntry entry) {
        return entry.isDualRecipe() ? RecipeType.BOTH : entry.isChargeRecipe() ? RecipeType.CHARGE : RecipeType.DISCHARGE;
    }

    private enum RecipeType {
        CHARGE,
        DISCHARGE,
        BOTH
    }
}
