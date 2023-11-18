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
import vazkii.botania.api.recipe.RecipeElvenTrade;

import java.util.Arrays;
import java.util.List;

public class ElvenTrade extends VirtualizedRegistry<RecipeElvenTrade> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.elvenTradeRecipes::remove);
        BotaniaAPI.elvenTradeRecipes.addAll(restoreFromBackup());
    }

    protected Object[] convertIngredients(IIngredient[] inputs) {
        return Arrays.stream(inputs).map(input -> input instanceof OreDictIngredient ? ((OreDictIngredient) input).getOreDict()
                                                                                     : input.getMatchingStacks()[0]).toArray();
    }

    public RecipeElvenTrade add(ItemStack[] outputs, IIngredient[] inputs) {
        RecipeElvenTrade recipe = new RecipeElvenTrade(outputs, convertIngredients(inputs));
        add(recipe);
        return recipe;
    }

    public RecipeElvenTrade add(ItemStack output, IIngredient[] inputs) {
        return add(new ItemStack[]{output}, inputs);
    }

    public void add(RecipeElvenTrade recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.elvenTradeRecipes.add(recipe);
    }

    public boolean remove(RecipeElvenTrade recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.elvenTradeRecipes.remove(recipe);
    }

    public boolean removeByOutputs(ItemStack... outputs) {
        if (BotaniaAPI.elvenTradeRecipes.removeIf(recipe -> {
            boolean found = Arrays.stream(outputs).allMatch(output -> recipe.getOutputs().stream().anyMatch(o -> ItemStack.areItemStacksEqual(o, output)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Elven Trade recipe")
                .add("could not find recipe with outputs {}", Arrays.toString(outputs))
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient... inputs) {
        List<Object> converted = Arrays.asList(convertIngredients(inputs));
        List<IIngredient> list = Arrays.asList(inputs);
        if (BotaniaAPI.elvenTradeRecipes.removeIf(recipe -> {
            boolean found = recipe.getInputs().stream().allMatch(input -> input instanceof String ? converted.contains(input)
                                                                                                  : list.stream().anyMatch(i -> i.test((ItemStack) input)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Elven Trade recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        BotaniaAPI.elvenTradeRecipes.forEach(this::addBackup);
        BotaniaAPI.elvenTradeRecipes.clear();
    }

    public SimpleObjectStream<RecipeElvenTrade> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.elvenTradeRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeElvenTrade> {

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Elven Trade recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 99, 1, 99);
        }

        @Override
        public @Nullable RecipeElvenTrade register() {
            if (!validate()) return null;
            RecipeElvenTrade recipe = new RecipeElvenTrade(output.toArray(new ItemStack[0]), convertIngredients(input.toArray(new IIngredient[0])));
            add(recipe);
            return recipe;
        }
    }
}
