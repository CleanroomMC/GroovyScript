package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipe;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipeManager;
import com.blakebr0.extendedcrafting.item.ItemSingularity;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CompressionCrafting extends VirtualizedRegistry<CompressorRecipe> {

    public CompressionCrafting() {
        super(Alias.generateOf("Compression"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe));
        CompressorRecipeManager.getInstance().getRecipes().addAll(restoreFromBackup());
    }

    public CompressorRecipe add(CompressorRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            CompressorRecipeManager.getInstance().getRecipes().add(recipe);
        }
        return recipe;
    }

    public CompressorRecipe add(ItemStack output, IIngredient input, int inputCount, IIngredient catalyst, boolean consumeCatalyst, int powerCost) {
        return add(output, input, inputCount, catalyst, consumeCatalyst, powerCost, ModConfig.confCompressorRFRate);
    }

    public CompressorRecipe add(ItemStack output, IIngredient input, int inputCount, IIngredient catalyst, boolean consumeCatalyst, int powerCost, int powerRate) {
        return add(new CompressorRecipe(output, input.toMcIngredient(), inputCount, catalyst.toMcIngredient(), consumeCatalyst, powerCost, powerRate));
    }

    public boolean removeByOutput(ItemStack output) {
        return CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getOutput().equals(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByCatalyst(IIngredient catalyst) {
        return CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getCatalyst().equals(catalyst.toMcIngredient())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByInput(IIngredient input) {
        return CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getInput().equals(input.toMcIngredient())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean remove(CompressorRecipe recipe) {
        if (CompressorRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<CompressorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CompressorRecipeManager.getInstance().getRecipes()).setRemover(this::remove);
    }

    public void removeAll() {
        CompressorRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        CompressorRecipeManager.getInstance().getRecipes().clear();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CompressorRecipe> {

        private int inputCount;
        private IIngredient catalyst = IngredientHelper.toIIngredient(ItemSingularity.getCatalystStack());
        private boolean consumeCatalyst = false;
        private int powerCost;
        private int powerRate = ModConfig.confCompressorRFRate;

        public RecipeBuilder input(IIngredient input) {
            if (input == null) return this;
            if (input.getAmount() > 1) {
                this.inputCount = input.getAmount();
            }
            this.input.add(input.withAmount(1));
            return this;
        }

        public RecipeBuilder inputCount(int inputCount) {
            this.inputCount = inputCount;
            return this;
        }

        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst.withAmount(1);
            return this;
        }

        public RecipeBuilder consumeCatalyst(boolean consumeCatalyst) {
            this.consumeCatalyst = consumeCatalyst;
            return this;
        }

        public RecipeBuilder powerCost(int powerCost) {
            this.powerCost = powerCost;
            return this;
        }

        public RecipeBuilder powerRate(int powerRate) {
            this.powerRate = powerRate;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extended Crafting Compression Crafting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);

            msg.add(IngredientHelper.isEmpty(catalyst), "catalyst must not be empty");
            msg.add(powerCost < 0, "power cost must not be negative");
            msg.add(powerRate < 0, "power rate must not be negative");
        }

        @Nullable
        @Override
        public CompressorRecipe register() {
            if (!validate()) return null;
            CompressorRecipe recipe = new CompressorRecipe(output.get(0), input.get(0).toMcIngredient(), inputCount, catalyst.toMcIngredient(), consumeCatalyst, powerCost, powerRate);
            ModSupport.EXTENDED_CRAFTING.get().compressionCrafting.add(recipe);
            return recipe;
        }
    }
}
