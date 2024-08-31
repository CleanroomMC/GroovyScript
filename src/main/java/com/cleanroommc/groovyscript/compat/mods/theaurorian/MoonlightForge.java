package com.cleanroommc.groovyscript.compat.mods.theaurorian;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.shiroroku.theaurorian.Recipes.MoonlightForgeRecipe;
import com.shiroroku.theaurorian.Recipes.MoonlightForgeRecipeHandler;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class MoonlightForge extends VirtualizedRegistry<MoonlightForgeRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone_sword'), item('minecraft:diamond')).output(item('minecraft:diamond_sword'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        restoreFromBackup().forEach(MoonlightForgeRecipeHandler::addRecipe);
        removeScripted().forEach(r -> MoonlightForgeRecipeHandler.allRecipes.removeIf(u -> u.equals(r)));
    }

    public void add(MoonlightForgeRecipe recipe) {
        addScripted(recipe);
        MoonlightForgeRecipeHandler.addRecipe(recipe);
    }

    public boolean remove(MoonlightForgeRecipe recipe) {
        return MoonlightForgeRecipeHandler.allRecipes.removeIf(r -> {
            if (r.equals(recipe)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('theaurorian:moonstonesword'), item('theaurorian:aurorianiteingot')"))
    public boolean removeByInput(IIngredient input, IIngredient catalyst) {
        return MoonlightForgeRecipeHandler.allRecipes.removeIf(r -> {
            if (input.test(r.getInput1()) && catalyst.test(r.getInput2())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('theaurorian:queenschipper')"))
    public boolean removeByOutput(IIngredient output) {
        return MoonlightForgeRecipeHandler.allRecipes.removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        MoonlightForgeRecipeHandler.allRecipes.forEach(this::addBackup);
        MoonlightForgeRecipeHandler.allRecipes.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<MoonlightForgeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(MoonlightForgeRecipeHandler.allRecipes).setRemover(this::remove);
    }

    @Property(property = "input", comp = @Comp(types = Comp.Type.EQ, eq = 2))
    @Property(property = "output", comp = @Comp(types = Comp.Type.EQ, eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MoonlightForgeRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Moonlight Forge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MoonlightForgeRecipe register() {
            if (!validate()) return null;
            MoonlightForgeRecipe recipe = null;
            for (ItemStack input1 : input.get(0).getMatchingStacks()) {
                for (ItemStack input2 : input.get(1).getMatchingStacks()) {
                    recipe = new MoonlightForgeRecipe(input1, input2, output.get(0));
                    ModSupport.THE_AURORIAN.get().moonlightForge.add(recipe);
                }
            }
            return recipe;
        }
    }
}
