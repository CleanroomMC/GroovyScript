package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.woot.AnvilManagerAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ipsis.Woot;
import ipsis.woot.crafting.AnvilRecipe;
import ipsis.woot.crafting.IAnvilRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class StygianIronAnvil extends VirtualizedRegistry<IAnvilRecipe> {

    public StygianIronAnvil() {
        super(Alias.generateOfClass(StygianIronAnvil.class).andGenerate("Anvil"));
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(Woot.anvilManager.getRecipes()::remove);
        Woot.anvilManager.getRecipes().addAll(restoreFromBackup());
    }

    @Override
    public void afterScriptLoad() {
        // Recalculate valid base items
        ((AnvilManagerAccessor) Woot.anvilManager).getValidBaseItems().clear();
        Woot.anvilManager.getRecipes().forEach(x -> {
            if (((AnvilManagerAccessor) Woot.anvilManager).getValidBaseItems().contains(x.getBaseItem())) return;
            ((AnvilManagerAccessor) Woot.anvilManager).getValidBaseItems().add(x.getBaseItem());
        });
    }

    public void add(IAnvilRecipe recipe) {
        Woot.anvilManager.getRecipes().add(recipe);
        addScripted(recipe);
    }

    public boolean remove(IAnvilRecipe recipe) {
        Woot.anvilManager.getRecipes().remove(recipe);
        addBackup(recipe);
        return true;
    }

    public boolean removeByBase(ItemStack base) {
        return Woot.anvilManager.getRecipes().removeIf(x -> {
            if (ItemStack.areItemsEqual(x.getBaseItem(), base)) {
                addBackup(x);
                return true;
            }
            return false;
        });
    }

    public boolean removeByOutput(ItemStack output) {
        return Woot.anvilManager.getRecipes().removeIf(x -> {
            if (ItemStack.areItemsEqual(x.getCopyOutput(), output)) {
                addBackup(x);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        Woot.anvilManager.getRecipes().forEach(this::addBackup);
        Woot.anvilManager.getRecipes().clear();
    }


    public SimpleObjectStream<IAnvilRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Woot.anvilManager.getRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IAnvilRecipe> {

        private ItemStack base = ItemStack.EMPTY;
        private boolean preserveBase = false;

        public RecipeBuilder base(ItemStack base) {
            this.base = base;
            return this;
        }

        public RecipeBuilder preserveBase(boolean preserveBase) {
            this.preserveBase = preserveBase;
            return this;
        }

        public RecipeBuilder preserveBase() {
            this.preserveBase = !this.preserveBase;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Woot Stygian Iron Anvil recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            // Note: JEI can only display 6 inputs, but there doesnt appear to be a limit for the actual recipe
            // validateItems(msg, 1, 6, 1, 1);
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            validateFluids(msg);
            msg.add(IngredientHelper.isEmpty(base), "base must be defined");
        }

        @Override
        public @Nullable IAnvilRecipe register() {
            if (!validate()) return null;

            if (((AnvilManagerAccessor) Woot.anvilManager).getValidBaseItems().stream().noneMatch(x -> x.isItemEqual(base)))
                ((AnvilManagerAccessor) Woot.anvilManager).getValidBaseItems().add(base);

            IAnvilRecipe recipe = new AnvilRecipe(output.get(0), base, preserveBase);
            recipe.getInputs().addAll(input.stream().map(x -> x.toMcIngredient().getMatchingStacks()[0]).collect(Collectors.toList()));
            ModSupport.WOOT.get().stygianIronAnvil.add(recipe);
            return recipe;
        }
    }

}
