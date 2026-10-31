package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;

import java.util.Collection;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.iceandfire.lightning_forge.note"),
        override = @MethodOverride(method = {
                @MethodDescription(method = "removeByOutput", example = @Example(value = "item('iceandfire:dragonsteel_lightning_ingot')", commented = true)),
                @MethodDescription(method = "removeByInput", example = {
                        @Example(value = "item('minecraft:iron_ingot')", commented = true),
                        @Example(value = "item('iceandfire:lightning_dragon_blood')", commented = true)
                })
        }, recipeBuilder = {
                @RecipeBuilderDescription(method = "recipeBuilder", example = {
                        @Example(value = ".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))", commented = true),
                        @Example(value = ".input(item('minecraft:diamond'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))", commented = true)
                })
        })
)
public class LightningForge extends DragonForge {

    public LightningForge(Collection<DragonForgeRecipe> recipes) {
        super("Lightning", recipes);
    }
}