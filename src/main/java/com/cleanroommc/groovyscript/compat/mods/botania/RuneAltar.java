package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.block.ModBlocks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RuneAltar extends VirtualizedRegistry<RecipeRuneAltar> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.runeAltarRecipes::remove);
        BotaniaAPI.runeAltarRecipes.addAll(restoreFromBackup());
    }

    public RecipeRuneAltar add(ItemStack output, int mana, IIngredient... inputs) {
        RecipeRuneAltar recipe = new RecipeRuneAltar(output, mana, Arrays.stream(inputs).map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict() : i.getMatchingStacks()[0]).toArray());
        add(recipe);
        return recipe;
    }

    public void add(RecipeRuneAltar recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.runeAltarRecipes.add(recipe);
    }

    public boolean remove(RecipeRuneAltar recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.runeAltarRecipes.remove(recipe);
    }

    public boolean removeByOutput(IIngredient output) {
        if (BotaniaAPI.runeAltarRecipes.removeIf(recipe -> {
            boolean found = output.test(recipe.getOutput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Rune Altar recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient... inputs) {
        List<Object> converted = Arrays.stream(inputs).map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict() : i.getMatchingStacks()[0]).collect(Collectors.toList());
        if (BotaniaAPI.runeAltarRecipes.removeIf(recipe -> {
            boolean found = converted.stream().allMatch(o -> recipe.getInputs().stream().anyMatch(i -> (i instanceof String || o instanceof String) ? i.equals(o) : ItemStack.areItemStacksEqual((ItemStack) i, (ItemStack) o)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Rune Altar recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient... inputs) {
        return removeByInput(inputs);
    }

    public void removeAll() {
        BotaniaAPI.runeAltarRecipes.forEach(this::addBackup);
        BotaniaAPI.runeAltarRecipes.clear();
    }

    public SimpleObjectStream<RecipeRuneAltar> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.runeAltarRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeRuneAltar> {

        protected int mana;

        public RecipeBuilder mana(int amount) {
            this.mana = amount;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Rune Altar recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 20, 1, 1);
            msg.add(input.stream().anyMatch(x -> x.test(new ItemStack(Item.getItemFromBlock(ModBlocks.livingrock), 1, 0))),
                    "input cannot contain a livingrock item");
            msg.add(mana < 1, "mana must be at least 1, got " + mana);
        }

        @Override
        public @Nullable RecipeRuneAltar register() {
            if (!validate()) return null;
            RecipeRuneAltar recipe = new RecipeRuneAltar(output.get(0), mana, input.stream().map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict() : i.getMatchingStacks()[0]).toArray());
            add(recipe);
            return recipe;
        }
    }
}
