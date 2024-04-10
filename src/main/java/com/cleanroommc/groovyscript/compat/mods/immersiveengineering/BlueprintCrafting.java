package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
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

@RegistryDescription
public class BlueprintCrafting extends VirtualizedRegistry<BlueprintCraftingRecipe> {

    public BlueprintCrafting() {
        super(Alias.generateOfClassAnd(BlueprintCrafting.class, "Blueprint"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond'), ore('ingotGold')).output(item('minecraft:clay')).category('groovy')"))
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

    @MethodDescription(type = MethodDescription.Type.ADDITION)
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

    @MethodDescription(example = @Example("'electrode'"))
    public void removeByCategory(String blueprintCategory) {
        if (!BlueprintCraftingRecipe.recipeList.containsKey(blueprintCategory)) {
            GroovyLog.msg("Error removing Immersive Engineering Blueprint Crafting recipe")
                    .add("category {} does not exist", blueprintCategory)
                    .error()
                    .post();
            return;
        }
        List<BlueprintCraftingRecipe> list = BlueprintCraftingRecipe.recipeList.removeAll(blueprintCategory);
        if (!list.isEmpty()) {
            list.forEach(this::addBackup);
        }
    }

    @MethodDescription(example = @Example("'components', item('immersiveengineering:material:8')"))
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

    @MethodDescription(example = @Example("'components', item('immersiveengineering:metal:38'), item('immersiveengineering:metal:38'), item('immersiveengineering:metal')"))
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

    @MethodDescription(type = MethodDescription.Type.QUERY, example = @Example("'molds'"))
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<BlueprintCraftingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BlueprintCraftingRecipe.recipeList.values()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BlueprintCraftingRecipe.recipeList.values().forEach(this::addBackup);
        BlueprintCraftingRecipe.recipeList.clear();
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "Integer.MAX_VALUE", type = Comp.Type.LTE)})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BlueprintCraftingRecipe> {

        @Property
        private String category;

        @RecipeBuilderMethodDescription
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
        @RecipeBuilderRegistrationMethod
        public @Nullable BlueprintCraftingRecipe register() {
            if (!validate()) return null;
            Object[] inputs = ArrayUtils.mapToArray(input, ImmersiveEngineering::toIngredientStack);
            BlueprintCraftingRecipe recipe = new BlueprintCraftingRecipe(category, output.get(0), inputs);
            ModSupport.IMMERSIVE_ENGINEERING.get().blueprint.add(recipe);
            return recipe;
        }
    }
}
