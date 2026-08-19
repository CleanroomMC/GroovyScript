package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;

import java.util.Collection;

@RegistryDescription(
        override = @MethodOverride(method = {
                @MethodDescription(method = "removeByOutput", example = @Example(value = "item('iceandfire:dragonsteel_ice_ingot')", commented = true)),
                @MethodDescription(method = "removeByInput", example = {
                        @Example("item('minecraft:iron_ingot')"),
                        @Example(value = "item('iceandfire:ice_dragon_blood')", commented = true)
                })
        }, recipeBuilder = {
                @RecipeBuilderDescription(method = "recipeBuilder", example = {
                        @Example(".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
                        @Example(".input(item('minecraft:diamond'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
                })
        })
)
public class IceForge extends DragonForge {

    public IceForge(Collection<DragonForgeRecipe> recipes) {
        super("Ice", recipes);
    }
}