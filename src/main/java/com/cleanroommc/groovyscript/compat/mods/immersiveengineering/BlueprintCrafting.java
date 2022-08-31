package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.RecipeStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlueprintCrafting extends VirtualizedRegistry<BlueprintCraftingRecipe> {
    public BlueprintCrafting() {
        super("Blueprint", "blueprint");
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
            if (!BlueprintCraftingRecipe.blueprintCategories.contains(recipe.blueprintCategory)) BlueprintCraftingRecipe.blueprintCategories.add(recipe.blueprintCategory);
            BlueprintCraftingRecipe.recipeList.get(recipe.blueprintCategory).add(recipe);
        }
    }

    public BlueprintCraftingRecipe add(String blueprintCategory, ItemStack output, Object... inputs) {
        BlueprintCraftingRecipe recipe = create(blueprintCategory, output, inputs);
        add(recipe);
        return recipe;
    }

    public void remove(BlueprintCraftingRecipe recipe) {
        if (recipe != null && BlueprintCraftingRecipe.recipeList.get(recipe.blueprintCategory).removeIf(r -> r == recipe)) addBackup(recipe);
    }

    public void removeByCategory(String blueprintCategory) {
        if (BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory)) {
            List<BlueprintCraftingRecipe> list = BlueprintCraftingRecipe.recipeList.removeAll(blueprintCategory);
            if (list.size() > 0) {
                list.forEach(this::addBackup);
            }
        }
    }

    public void removeByOutput(String blueprintCategory, ItemStack output) {
        if (BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory)) {
            for (BlueprintCraftingRecipe recipe : BlueprintCraftingRecipe.recipeList.get(blueprintCategory)) {
                if (ApiUtils.stackMatchesObject(output, recipe.output)) {
                    addBackup(recipe);
                    BlueprintCraftingRecipe.recipeList.remove(blueprintCategory, recipe);
                    break;
                }
            }
        }
    }

    public void removeByInput(String blueprintCategory, ItemStack... inputs) {
        if (BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory)) {
            for (BlueprintCraftingRecipe recipe : BlueprintCraftingRecipe.recipeList.get(blueprintCategory)) {
                if (recipe.inputs.length == inputs.length) {
                    int i;
                    for (i = 0; i < recipe.inputs.length; i++) {
                        ItemStack input = inputs[i];
                        IngredientStack recInput = recipe.inputs[i];
                        if (!ApiUtils.stackMatchesObject(input, recInput)) break;
                    }

                    if (i != recipe.inputs.length) continue;
                    addBackup(recipe);
                    BlueprintCraftingRecipe.recipeList.remove(blueprintCategory, recipe);
                }
            }
        }
    }

    public RecipeStream<BlueprintCraftingRecipe> streamByCategory(String blueprintCategory) {
        return new RecipeStream<>(BlueprintCraftingRecipe.recipeList.get(blueprintCategory)).setRemover(recipe -> {
            NonNullList<ItemStack> list = NonNullList.create();
            for (IngredientStack stack : recipe.inputs) {
                list.add(stack.stack);
            }

            for (BlueprintCraftingRecipe r : BlueprintCraftingRecipe.recipeList.get(blueprintCategory)) {
                if (r.inputs.length == recipe.inputs.length && ApiUtils.stacksMatchIngredientList(r.getFormattedInputs(), list)) {
                    remove(r);
                    addBackup(r);
                    return true;
                }
            }

            return false;
        });
    }

    public RecipeStream<BlueprintCraftingRecipe> stream() {
        return new RecipeStream<>(BlueprintCraftingRecipe.recipeList.values()).setRemover(recipe -> {
            NonNullList<ItemStack> list = NonNullList.create();
            for (IngredientStack stack : recipe.inputs) {
                list.add(stack.stack);
            }

            for (BlueprintCraftingRecipe r : BlueprintCraftingRecipe.recipeList.values()) {
                if (r.inputs.length == recipe.inputs.length && ApiUtils.stacksMatchIngredientList(r.getFormattedInputs(), list)) {
                    remove(r);
                    addBackup(r);
                    return true;
                }
            }

            return false;
        });
    }

    public void removeAll() {
        BlueprintCraftingRecipe.recipeList.values().forEach(this::addBackup);
        BlueprintCraftingRecipe.recipeList.clear();
    }

    private static BlueprintCraftingRecipe create(String blueprintCategory, ItemStack output, Object... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            Object obj = inputs[i];
            if (obj instanceof IIngredient) inputs[i] = ((IIngredient) obj).getMatchingStacks();
        }

        return new BlueprintCraftingRecipe(blueprintCategory, output, inputs);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BlueprintCraftingRecipe> {
        protected String category;

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
            validateItems(msg, 1, 6, 1, 1);
            validateFluids(msg);
            if (this.category == null) this.category = BlueprintCraftingRecipe.blueprintCategories.get(0);
        }

        @Override
        public @Nullable BlueprintCraftingRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().blueprint.add(category, output.get(0), input.toArray());
        }
    }
}
