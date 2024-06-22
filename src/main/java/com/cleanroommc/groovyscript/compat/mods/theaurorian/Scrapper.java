package com.cleanroommc.groovyscript.compat.mods.theaurorian;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.shiroroku.theaurorian.Recipes.ScrapperRecipe;
import com.shiroroku.theaurorian.Recipes.ScrapperRecipeHandler;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Scrapper extends VirtualizedRegistry<ScrapperRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone_sword')).output(item('minecraft:cobblestone'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        restoreFromBackup().forEach(ScrapperRecipeHandler::addRecipe);
        removeScripted().forEach(r -> ScrapperRecipeHandler.allRecipes.removeIf(u -> u.equals(r)));
    }

    public void add(ScrapperRecipe recipe) {
        addScripted(recipe);
        ScrapperRecipeHandler.addRecipe(recipe);
    }

    @MethodDescription(example = @Example("item('minecraft:iron_sword')"))
    public boolean removeByInput(IIngredient output) {
        return ScrapperRecipeHandler.allRecipes.removeIf(r -> {
            if (output.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('theaurorian:scrapaurorianite')"))
    public boolean removeByOutput(IIngredient output) {
        return ScrapperRecipeHandler.allRecipes.removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ScrapperRecipeHandler.allRecipes.forEach(this::addBackup);
        ScrapperRecipeHandler.allRecipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ScrapperRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ScrapperRecipeHandler.allRecipes).setRemover(r ->
            ScrapperRecipeHandler.allRecipes.removeIf(u -> {
                if (u.equals(r)) {
                    addBackup(r);
                    return true;
                }
                return false;
            })
        );
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ScrapperRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Scrapper recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ScrapperRecipe register() {
            if (!validate()) return null;
            ScrapperRecipe recipe = null;
            for (ItemStack input1 : input.get(0).getMatchingStacks()) {
                recipe = new ScrapperRecipe(input1, output.get(0));
                ModSupport.THE_AURORIAN.get().scrapper.add(recipe);
            }
            return recipe;
        }
    }
}
