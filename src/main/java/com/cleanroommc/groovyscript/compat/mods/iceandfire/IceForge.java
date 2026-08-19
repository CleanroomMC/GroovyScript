package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;

import java.util.Collection;

@RegistryDescription
public class IceForge extends DragonForge {

    public IceForge(Collection<DragonForgeRecipe> recipes) {
        super("Ice", recipes);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    @Override
    public RecipeBuilder recipeBuilder() {
        return super.recipeBuilder();
    }

    @MethodDescription(example = {
            @Example("item('minecraft:iron_ingot')"), @Example(value = "item('iceandfire:ice_dragon_blood')", commented = true)
    })
    @Override
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @MethodDescription(example = @Example(value = "item('iceandfire:dragonsteel_ice_ingot')", commented = true))
    @Override
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }
}