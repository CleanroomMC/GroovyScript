package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipe;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipeManager;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CombinationCrafting extends VirtualizedRegistry<CombinationRecipe> {

    public CombinationCrafting() {
        super(Alias.generateOf("Combination"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> CombinationRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe));
        CombinationRecipeManager.getInstance().getRecipes().addAll(restoreFromBackup());
    }

    public CombinationRecipe add(CombinationRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            CombinationRecipeManager.getInstance().getRecipes().add(recipe);
        }
        return recipe;
    }

    public CombinationRecipe add(ItemStack output, long cost, Ingredient input, NonNullList<Ingredient> pedestals) {
        return add(output, cost, ModConfig.confCraftingCoreRFRate, input, pedestals);
    }

    public CombinationRecipe add(ItemStack output, long cost, int perTick, Ingredient input, NonNullList<Ingredient> pedestals) {
        return add(new CombinationRecipe(output, cost, perTick, input, pedestals));
    }

    public boolean removeByOutput(ItemStack output) {
        return CombinationRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getOutput().equals(output)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByInput(ItemStack input) {
        return CombinationRecipeManager.getInstance().getRecipes().removeIf(r -> {
            if (r.getInput().equals(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByInput(IIngredient input) {
        return removeByInput(IngredientHelper.toItemStack(input));
    }

    public boolean remove(CombinationRecipe recipe) {
        if (CombinationRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<CombinationRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CombinationRecipeManager.getInstance().getRecipes()).setRemover(this::remove);
    }

    public void removeAll() {
        CombinationRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        CombinationRecipeManager.getInstance().getRecipes().clear();
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CombinationRecipe> {

        private final NonNullList<IIngredient> pedestals = NonNullList.create();
        private long cost;
        private int perTick = ModConfig.confCraftingCoreRFRate;

        public RecipeBuilder cost(long cost) {
            this.cost = cost;
            return this;
        }

        public RecipeBuilder totalCost(long totalCost) {
            return cost(totalCost);
        }

        public RecipeBuilder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }

        public RecipeBuilder costPerTick(int costPerTick) {
            return perTick(costPerTick);
        }

        public RecipeBuilder input(IIngredient ingredient) {
            this.input.add(ingredient.withAmount(1));
            return this;
        }

        public RecipeBuilder pedestals(IIngredient pedestals) {
            for (int x = 0; x < pedestals.getAmount(); x++) {
                this.pedestals.add(pedestals.withAmount(1));
            }
            return this;
        }

        public RecipeBuilder pedestals(Collection<IIngredient> pedestals) {
            for (IIngredient x : pedestals) {
                this.pedestals(x);
            }
            return this;
        }

        public RecipeBuilder pedestals(IIngredient... pedestals) {
            for (IIngredient x : pedestals) {
                this.pedestals(x);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extended Crafting Combination Crafting recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(cost < 0, () -> "cost must not be negative");
            msg.add(perTick < 0, () -> "per tick must not be negative");
        }

        @Nullable
        @Override
        public CombinationRecipe register() {
            if (!validate()) return null;
            CombinationRecipe recipe = new CombinationRecipe(output.get(0), cost, perTick, input.get(0).toMcIngredient(), IngredientHelper.toIngredientNonNullList(pedestals));
            ModSupport.EXTENDED_CRAFTING.get().combinationCrafting.add(recipe);
            return recipe;
        }
    }
}
