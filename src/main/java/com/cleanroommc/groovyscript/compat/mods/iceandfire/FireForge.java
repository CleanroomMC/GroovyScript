package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;

import java.util.Collection;

@RegistryDescription(
        override = @MethodOverride(method = {
                @MethodDescription(method = "removeByOutput", example = @Example(value = "item('iceandfire:dragonsteel_fire_ingot')", commented = true)),
                @MethodDescription(method = "removeByInput", example = {
                        @Example("item('minecraft:iron_ingot')"),
                        @Example(value = "item('iceandfire:fire_dragon_blood')", commented = true)
                })
        }, recipeBuilder = {
                @RecipeBuilderDescription(method = "recipeBuilder", example = {
                        @Example(".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
                        @Example(".input(item('minecraft:diamond'), item('minecraft:clay')).output(item('minecraft:gold_ingot'))")
                })}
        )
)
public class FireForge extends DragonForge {

    public FireForge(Collection<DragonForgeRecipe> recipes) {
        super("Fire", recipes);
    }
}