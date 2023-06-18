package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.forestry.CentrifugeRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.factory.recipes.CentrifugeRecipe;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Centrifuge extends ForestryRegistry<ICentrifugeRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(CentrifugeRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(CentrifugeRecipeManagerAccessor.getRecipes()::add);
    }

    public void add(ICentrifugeRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        CentrifugeRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(ICentrifugeRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return CentrifugeRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByInput(IIngredient input) {
        if (CentrifugeRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = input.test(recipe.getInput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Centrifuge recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutputs(IIngredient... output) {
        Set<ItemStack> list = Arrays.stream(output).map(i -> i.getMatchingStacks()[0]).collect(Collectors.toSet());
        if (CentrifugeRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = list.containsAll(recipe.getAllProducts().keySet());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Centrifuge recipe")
                .add("could not find recipe with outputs {}", list)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(IIngredient... output) {
        return removeByOutputs(output);
    }

    public void removeAll() {
        CentrifugeRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        CentrifugeRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<ICentrifugeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CentrifugeRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<ICentrifugeRecipe> {

        protected int time = 20;
        protected Map<ItemStack, Float> outputs = new Object2FloatOpenHashMap<>();

        public RecipeBuilder time(int time) {
            this.time = Math.max(time, 1);
            return this;
        }

        public RecipeBuilder output(ItemStack output, float chance) {
            if (!output.isEmpty()) this.outputs.put(output, Math.max(chance, 0.01F));
            return this;
        }

        @Override
        public RecipeBuilder output(ItemStack output) {
            return this.output(output, 1.0F);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Centrifuge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 1, 0, 0);
            msg.add(outputs.isEmpty() || outputs.size() > 9, "Must have 1 - 9 outputs. got {}.", outputs.size());
        }

        @Override
        public @Nullable ICentrifugeRecipe register() {
            if (!validate()) return null;
            ICentrifugeRecipe recipe = new CentrifugeRecipe(time, input.get(0).getMatchingStacks()[0], outputs);
            add(recipe);
            return recipe;
        }
    }
}
