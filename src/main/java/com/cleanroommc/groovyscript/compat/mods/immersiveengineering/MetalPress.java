package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription
public class MetalPress extends VirtualizedRegistry<MetalPressRecipe> {

    @RecipeBuilderDescription(example = @Example(".mold(item('minecraft:diamond')).input(ore('ingotGold')).output(item('minecraft:clay')).energy(100)"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> MetalPressRecipe.recipeList.get(recipe.mold).removeIf(r -> r == recipe));
        restoreFromBackup().forEach(recipe -> MetalPressRecipe.recipeList.put(recipe.mold, recipe));
    }

    public void add(MetalPressRecipe recipe) {
        if (recipe != null) {
            MetalPressRecipe.recipeList.put(recipe.mold, recipe);
            addScripted(recipe);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public MetalPressRecipe add(ItemStack output, IIngredient input, ItemStack mold, int energy) {
        MetalPressRecipe recipe = new MetalPressRecipe(output.copy(), ImmersiveEngineering.toIngredientStack(input), ApiUtils.createComparableItemStack(mold, true), energy);
        add(recipe);
        return recipe;
    }

    public boolean remove(MetalPressRecipe recipe) {
        if (recipe != null && MetalPressRecipe.recipeList.get(recipe.mold).removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('immersiveengineering:material:2')"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        List<MetalPressRecipe> list = MetalPressRecipe.removeRecipes(output);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    @MethodDescription(example = @Example("item('immersiveengineering:mold'), item('immersiveengineering:metal:31')"))
    public void removeByOutput(ItemStack mold, ItemStack output) {
        boolean moldEmpty = IngredientHelper.isEmpty(mold);
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                .add(moldEmpty, () -> "mold must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error();
        if (moldEmpty) {
            msg.post();
            return;
        }
        ComparableItemStack comparable = ApiUtils.createComparableItemStack(mold, false);
        msg.add(!MetalPressRecipe.recipeList.containsKey(comparable), () -> mold + " is not a valid mold");
        if (msg.postIfNotEmpty()) return;
        if (!MetalPressRecipe.recipeList.get(comparable).removeIf(recipe -> {
            if (ApiUtils.stackMatchesObject(output, recipe.output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                    .add("no recipes found for {} and {}", mold, output)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('immersiveengineering:mold'), item('immersiveengineering:metal:8')"))
    public void removeByInput(ItemStack mold, ItemStack input) {
        boolean moldEmpty = IngredientHelper.isEmpty(mold);
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                .add(moldEmpty, () -> "mold must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error();
        if (moldEmpty) {
            msg.post();
            return;
        }
        ComparableItemStack comparable = ApiUtils.createComparableItemStack(mold, false);
        msg.add(!MetalPressRecipe.recipeList.containsKey(comparable), () -> mold + " is not a valid mold");
        if (msg.postIfNotEmpty()) return;

        if (!MetalPressRecipe.recipeList.get(comparable).removeIf(recipe -> {
            if (ApiUtils.stackMatchesObject(input, recipe.input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                    .add("no recipes found for {} and {}", mold, input)
                    .error()
                    .post();
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:iron_ingot')"))
    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
        }
        if (!MetalPressRecipe.recipeList.values().removeIf(recipe -> {
            if (ApiUtils.stackMatchesObject(input, recipe.input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('immersiveengineering:mold:4')"))
    public void removeByMold(ItemStack mold) {
        boolean moldEmpty = IngredientHelper.isEmpty(mold);
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Immersive Engineering Metal Press recipe")
                .add(moldEmpty, () -> "mold must not be empty")
                .error();
        if (moldEmpty) {
            msg.post();
            return;
        }
        ComparableItemStack comparable = ApiUtils.createComparableItemStack(mold, false);
        msg.add(!MetalPressRecipe.recipeList.containsKey(comparable), () -> mold + " is not a valid mold");
        if (msg.postIfNotEmpty()) return;
        List<MetalPressRecipe> list = MetalPressRecipe.recipeList.removeAll(ApiUtils.createComparableItemStack(mold, false));
        if (!list.isEmpty()) list.forEach(this::addBackup);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        MetalPressRecipe.recipeList.values().forEach(this::addBackup);
        MetalPressRecipe.recipeList.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<MetalPressRecipe> streamRecipes() {
        List<MetalPressRecipe> recipes = new ArrayList<>(MetalPressRecipe.recipeList.values());
        return new SimpleObjectStream<>(recipes).setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MetalPressRecipe> {

        @Property
        private int energy;
        @Property(valid = @Comp(value = "empty", type = Comp.Type.NOT))
        private ItemStack mold = ItemStack.EMPTY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder mold(ItemStack mold) {
            this.mold = mold;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Metal Press recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(mold.isEmpty(), "mold must be defined");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MetalPressRecipe register() {
            if (!validate()) return null;
            MetalPressRecipe recipe = new MetalPressRecipe(output.get(0), ImmersiveEngineering.toIngredientStack(input.get(0)), ApiUtils.createComparableItemStack(mold, true), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().metalPress.add(recipe);
            return recipe;
        }
    }
}
