package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlueprintCrafting extends VirtualizedRegistry<BlueprintCraftingRecipe> {

    public BlueprintCrafting() {
        super(VirtualizedRegistry.generateAliases("Blueprint"));
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> {
            BlueprintCraftingRecipe.recipeList.get(recipe.blueprintCategory).removeIf(r -> r == recipe);
            if (!BlueprintCraftingRecipe.recipeList.containsKey(recipe.blueprintCategory)) {
                BlueprintCraftingRecipe.blueprintCategories.remove(recipe.blueprintCategory);
            }
        });
        restoreFromBackup().forEach(r -> BlueprintCraftingRecipe.recipeList.get(r.blueprintCategory).add(r));
    }

    public void add(BlueprintCraftingRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            if (!BlueprintCraftingRecipe.blueprintCategories.contains(recipe.blueprintCategory)) {
                BlueprintCraftingRecipe.blueprintCategories.add(recipe.blueprintCategory);
            }
            BlueprintCraftingRecipe.recipeList.get(recipe.blueprintCategory).add(recipe);
        }
    }

    public BlueprintCraftingRecipe add(String blueprintCategory, ItemStack output, List<IIngredient> inputs) {
        Object[] inputs1 = ArrayUtils.mapToArray(inputs, ImmersiveEngineering::toIngredientStack);
        BlueprintCraftingRecipe recipe = new BlueprintCraftingRecipe(blueprintCategory, output.copy(), inputs1);
        add(recipe);
        return recipe;
    }

    public boolean remove(BlueprintCraftingRecipe recipe) {
        if (recipe != null && BlueprintCraftingRecipe.recipeList.get(recipe.blueprintCategory).removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByCategory(String blueprintCategory) {
        if (!BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory)) {
            GroovyLog.msg("Error removing Immersive Engineering Blueprint Crafting recipe")
                    .add("category {} does not exist", blueprintCategory)
                    .error()
                    .post();
            return;
        }
        List<BlueprintCraftingRecipe> list = BlueprintCraftingRecipe.recipeList.removeAll(blueprintCategory);
        if (list.size() > 0) {
            list.forEach(this::addBackup);
        }
    }

    public void removeByOutput(String blueprintCategory, ItemStack output) {
        if (GroovyLog.msg("Error removing Immersive Engineering Blueprint Crafting recipe")
                .add(!BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory), () -> "category " + blueprintCategory + " does not exist")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        if (!BlueprintCraftingRecipe.recipeList.get(blueprintCategory).removeIf(recipe -> {
            if (ApiUtils.stackMatchesObject(output, recipe.output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Blueprint Crafting recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
        }
    }

    public void removeByInput(String blueprintCategory, ItemStack... inputs) {
        if (GroovyLog.msg("Error removing Immersive Engineering Blueprint Crafting recipe")
                .add(!BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory), () -> "category " + blueprintCategory + " does not exist")
                .add(inputs == null || inputs.length == 0, () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }

        if (!BlueprintCraftingRecipe.recipeList.get(blueprintCategory).removeIf(recipe -> {
            if (recipe.inputs.length == inputs.length) {
                int i;
                for (i = 0; i < recipe.inputs.length; i++) {
                    ItemStack input = inputs[i];
                    IngredientStack recInput = recipe.inputs[i];
                    if (!ApiUtils.stackMatchesObject(input, recInput)) break;
                }

                if (i == recipe.inputs.length) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Blueprint Crafting recipe")
                    .add("no recipes found for {}", Arrays.toString(inputs))
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<BlueprintCraftingRecipe> streamRecipesByCategory(String blueprintCategory) {
        Collection<BlueprintCraftingRecipe> recipes = BlueprintCraftingRecipe.recipeList.get(blueprintCategory);
        return new SimpleObjectStream<>(recipes).setRemover(recipe -> {
            if (recipes.removeIf(r -> r == recipe)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public SimpleObjectStream<BlueprintCraftingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BlueprintCraftingRecipe.recipeList.values()).setRemover(this::remove);
    }

    public void removeAll() {
        BlueprintCraftingRecipe.recipeList.values().forEach(this::addBackup);
        BlueprintCraftingRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BlueprintCraftingRecipe> {

        private String category;

        public RecipeBuilder category(String category) {
            this.category = category;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Blueprint recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            validateFluids(msg);
            if (this.category == null) this.category = BlueprintCraftingRecipe.blueprintCategories.get(0);
        }

        @Override
        public @Nullable BlueprintCraftingRecipe register() {
            if (!validate()) return null;
            Object[] inputs = ArrayUtils.mapToArray(input, ImmersiveEngineering::toIngredientStack);
            BlueprintCraftingRecipe recipe = new BlueprintCraftingRecipe(category, output.get(0), inputs);
            ModSupport.IMMERSIVE_ENGINEERING.get().blueprint.add(recipe);
            return recipe;
        }
    }
}
