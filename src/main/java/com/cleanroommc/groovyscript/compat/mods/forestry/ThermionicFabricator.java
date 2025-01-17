package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.forestry.FabricatorRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.FabricatorRecipe;
import forestry.factory.recipes.FabricatorSmeltingRecipe;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ThermionicFabricator extends ForestryRegistry<IFabricatorRecipe> {

    public final Smelting smelting = new Smelting();

    public ThermionicFabricator() {
        super(Alias.generateOfClassAnd(ThermionicFabricator.class, "Fabricator"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(FabricatorRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(FabricatorRecipeManagerAccessor.getRecipes()::add);
    }

    public void add(IFabricatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        FabricatorRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(IFabricatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return FabricatorRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByFluid(FluidStack input) {
        if (FabricatorRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = recipe.getLiquid().isFluidEqual(input);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Thermionic Fabricator recipe")
                .add("could not find recipe with fluid input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByCatalyst(IIngredient catalyst) {
        if (FabricatorRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = catalyst.test(recipe.getPlan());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Thermionic Fabricator recipe")
                .add("could not find recipe with catalyst {}", catalyst)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(IIngredient output) {
        if (FabricatorRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = output.test(recipe.getRecipeOutput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Thermionic Fabricator recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient... input) {
        if (FabricatorRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = Arrays.stream(input).allMatch(i -> {
                boolean matches = false;
                if (i instanceof OreDictIngredient oreDictIngredient) {
                    matches = recipe.getOreDicts().contains(oreDictIngredient.getOreDict());
                } else {
                    for (int x = 0; x < recipe.getWidth(); x++) {
                        if (recipe.getIngredients().get(x).contains(i.getMatchingStacks()[0])) {
                            matches = true;
                            break;
                        }
                    }
                }
                return matches;
            });
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Thermionic Fabricator recipe")
                .add("could not find recipe with inputs {}", (Object[]) input)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        FabricatorRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        FabricatorRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<IFabricatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FabricatorRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<IFabricatorRecipe> {

        protected String[] pattern;
        protected IIngredient catalyst = IIngredient.EMPTY;
        protected final Char2ObjectOpenHashMap<IIngredient> keys = new Char2ObjectOpenHashMap<>();

        public RecipeBuilder catalyst(IIngredient item) {
            this.catalyst = item;
            return this;
        }

        public RecipeBuilder key(String key, IIngredient item) {
            if (key != null && !" ".equals(key) && !"\\".equals(key)) this.keys.put(key.charAt(0), item);
            return this;
        }

        public RecipeBuilder shape(String... pattern) {
            this.pattern = pattern;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Thermionic Fabricator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            Carpenter.validatePattern(msg, pattern, keys);
            validateStackSize(msg, 1, "grid input", keys.values());
            validateStackSize(msg, 1, "catalyst", catalyst);
        }

        @Override
        public @Nullable IFabricatorRecipe register() {
            if (!validate()) return null;
            ShapedRecipeCustom internal = Carpenter.convertPatternToInternal(output.get(0), pattern, keys);
            IFabricatorRecipe recipe = new FabricatorRecipe(catalyst.getMatchingStacks()[0], fluidInput.get(0), output.get(0), internal.getRawIngredients(), internal.getOreDicts(), internal.getWidth(), internal.getHeight());
            add(recipe);
            return recipe;
        }
    }

    public static class Smelting extends ForestryRegistry<IFabricatorSmeltingRecipe> {

        @Override
        @GroovyBlacklist
        public void onReload() {
            if (!isEnabled()) return;
            removeScripted().forEach(FabricatorSmeltingRecipeManager.recipes::remove);
            FabricatorSmeltingRecipeManager.recipes.addAll(restoreFromBackup());
        }

        public IFabricatorSmeltingRecipe add(FluidStack output, IIngredient input, int meltingPoint) {
            IFabricatorSmeltingRecipe recipe = new FabricatorSmeltingRecipe(input.getMatchingStacks()[0], output, meltingPoint);
            add(recipe);
            return recipe;
        }

        public void add(IFabricatorSmeltingRecipe recipe) {
            if (recipe == null) return;
            addScripted(recipe);
            FabricatorSmeltingRecipeManager.recipes.add(recipe);
        }

        public boolean remove(IFabricatorSmeltingRecipe recipe) {
            if (recipe == null) return false;
            addBackup(recipe);
            return FabricatorSmeltingRecipeManager.recipes.remove(recipe);
        }

        public boolean removeByInput(IIngredient input) {
            if (FabricatorSmeltingRecipeManager.recipes.removeIf(recipe -> {
                boolean found = input.test(recipe.getResource());
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Forestry Thermionic Fabricator Smelting recipe")
                    .add("Could not find recipe with input {}", input)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByOutput(FluidStack output) {
            if (FabricatorSmeltingRecipeManager.recipes.removeIf(recipe -> {
                boolean found = recipe.getProduct().isFluidEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Forestry Thermionic Fabricator Smelting recipe")
                    .add("Could not find recipe with output {}", output)
                    .error()
                    .post();
            return false;
        }

        public void removeAll() {
            FabricatorSmeltingRecipeManager.recipes.forEach(this::addBackup);
            FabricatorSmeltingRecipeManager.recipes.clear();
        }

        public SimpleObjectStream<IFabricatorSmeltingRecipe> streamRecipes() {
            return new SimpleObjectStream<>(FabricatorSmeltingRecipeManager.recipes).setRemover(this::remove);
        }
    }
}
