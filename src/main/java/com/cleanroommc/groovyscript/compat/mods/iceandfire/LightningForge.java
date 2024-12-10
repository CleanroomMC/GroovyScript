package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

// want all the examples to be commented
@RegistryDescription(admonition = @Admonition("groovyscript.wiki.iceandfire.lightning_forge.note"))
public class LightningForge extends StandardListRegistry<DragonForgeRecipe> {

    @Override
    public Collection<DragonForgeRecipe> getRecipes() {
        return IafRecipeRegistry.LIGHTNING_FORGE_RECIPES;
    }

    @RecipeBuilderDescription(example = {
            @Example(value = ".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))", commented = true),
            @Example(value = ".input(item('minecraft:diamond'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))", commented = true)
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {
            @Example(value = "item('minecraft:iron_ingot')", commented = true), @Example(value = "item('iceandfire:lightning_dragon_blood')", commented = true)
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (input.test(r.getInput()) || input.test(r.getBlood())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example(value = "item('iceandfire:dragonsteel_lightning_ingot')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DragonForgeRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Ice And Fire Lightning Forge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DragonForgeRecipe register() {
            if (!validate()) return null;
            DragonForgeRecipe recipe = null;
            for (var inputStack : input.get(0).getMatchingStacks()) {
                for (var blood : input.get(1).getMatchingStacks()) {
                    recipe = new DragonForgeRecipe(inputStack, blood, output.get(0));
                    ModSupport.ICE_AND_FIRE.get().lightningForge.add(recipe);
                }
            }
            return recipe;
        }
    }
}
