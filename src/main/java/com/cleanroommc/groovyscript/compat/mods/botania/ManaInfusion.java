package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

public class ManaInfusion extends VirtualizedRegistry<RecipeManaInfusion> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.manaInfusionRecipes::remove);
        BotaniaAPI.manaInfusionRecipes.addAll(restoreFromBackup());
    }

    public RecipeManaInfusion add(ItemStack output, IIngredient input, int mana) {
        RecipeManaInfusion recipe = new RecipeManaInfusion(output, input instanceof OreDictIngredient ? ((OreDictIngredient) input).getOreDict() : input.getMatchingStacks()[0], mana);
        add(recipe);
        return recipe;
    }

    public void add(RecipeManaInfusion recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.manaInfusionRecipes.add(recipe);
    }

    public boolean remove(RecipeManaInfusion recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.manaInfusionRecipes.remove(recipe);
    }

    public boolean removeByOutput(ItemStack output) {
        if (BotaniaAPI.manaInfusionRecipes.removeIf(recipe -> {
            boolean found = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Mana Infusion recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient input) {
        if (BotaniaAPI.manaInfusionRecipes.removeIf(recipe -> {
            boolean found = recipe.getInput() instanceof ItemStack ? input.test((ItemStack) recipe.getInput()) : (input instanceof OreDictIngredient && ((OreDictIngredient) input).getOreDict().equals(recipe.getInput()));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Mana Infusion recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByCatalyst(IBlockState catalyst) {
        if (BotaniaAPI.manaInfusionRecipes.removeIf(recipe -> {
            if (recipe.getCatalyst() == null) return false;
            boolean found = recipe.getCatalyst().equals(catalyst);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Mana Infusion recipe")
                .add("could not find recipe with catalyst {}", catalyst)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        BotaniaAPI.manaInfusionRecipes.forEach(this::addBackup);
        BotaniaAPI.manaInfusionRecipes.clear();
    }

    public SimpleObjectStream<RecipeManaInfusion> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.manaInfusionRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipeManaInfusion> {

        protected int mana = 100;
        protected IBlockState catalyst;

        public RecipeBuilder mana(int amount) {
            this.mana = amount;
            return this;
        }

        public RecipeBuilder catalyst(IBlockState block) {
            this.catalyst = block;
            return this;
        }

        public RecipeBuilder useAlchemy() {
            return catalyst(RecipeManaInfusion.alchemyState);
        }

        public RecipeBuilder useConjuration() {
            return catalyst(RecipeManaInfusion.conjurationState);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Mana Infusion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 1, 1, 1);
            msg.add(mana < 1, "Mana amount must be at least 1, got " + mana);
        }

        @Override
        public @Nullable RecipeManaInfusion register() {
            if (!validate()) return null;
            RecipeManaInfusion recipe = new RecipeManaInfusion(output.get(0), input.get(0) instanceof OreDictIngredient ? ((OreDictIngredient) input.get(0)).getOreDict() : input.get(0).getMatchingStacks()[0], mana);
            if (catalyst != null) recipe.setCatalyst(catalyst);
            add(recipe);
            return recipe;
        }
    }
}
