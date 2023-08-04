package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeBrew;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BrewRecipe extends VirtualizedRegistry<RecipeBrew> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.brewRecipes::remove);
        BotaniaAPI.brewRecipes.addAll(restoreFromBackup());
    }

    public void add(RecipeBrew recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.brewRecipes.add(recipe);
    }

    public boolean remove(RecipeBrew recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.brewRecipes.remove(recipe);
    }

    public boolean removeByOutput(String brew) {
        if (BotaniaAPI.brewRecipes.removeIf(recipe -> {
            boolean found = recipe.getBrew().getKey().equals(brew);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Brew recipe")
                .add("could not find recipe with input {}", brew)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(vazkii.botania.api.brew.Brew brew) {
        return removeByOutput(brew.getKey());
    }

    public boolean removeByInput(IIngredient... inputs) {
        List<Object> converted = Arrays.stream(inputs).map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict() : i.getMatchingStacks()[0]).collect(Collectors.toList());
        if (BotaniaAPI.brewRecipes.removeIf(recipe -> {
            boolean found = converted.stream().allMatch(o -> recipe.getInputs().stream().anyMatch(i -> (i instanceof String || o instanceof String) ? i.equals(o) : ItemStack.areItemStacksEqual((ItemStack) i, (ItemStack) o)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Brew recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient... inputs) {
        return removeByInput(inputs);
    }

    public void removeAll() {
        BotaniaAPI.brewRecipes.forEach(this::addBackup);
        BotaniaAPI.brewRecipes.clear();
    }

    public SimpleObjectStream<RecipeBrew> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.brewRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeBrew> {

        protected vazkii.botania.api.brew.Brew brew;

        public RecipeBuilder output(vazkii.botania.api.brew.Brew brew) {
            this.brew = brew;
            return this;
        }

        public RecipeBuilder brew(vazkii.botania.api.brew.Brew brew) {
            return output(brew);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Brew recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg);
            validateItems(msg, 1, 6, 0, 0);
            msg.add(brew == null, "Expected a valid output brew, got " + brew);
        }

        @Override
        public @Nullable RecipeBrew register() {
            if (!validate()) return null;
            RecipeBrew recipe = new RecipeBrew(brew, input.stream().map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict() : i.getMatchingStacks()[0]).toArray());
            add(recipe);
            return recipe;
        }
    }

}
