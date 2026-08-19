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
public class LightningForge extends DragonForge {

    public LightningForge(Collection<DragonForgeRecipe> recipes) {
        super("Lightning", recipes);
    }

    @RecipeBuilderDescription(example = {
            @Example(value = ".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))", commented = true),
            @Example(value = ".input(item('minecraft:diamond'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))", commented = true)
    })
    @Override
    public RecipeBuilder recipeBuilder() {
        return super.recipeBuilder();
    }

    @MethodDescription(example = {
            @Example(value = "item('minecraft:iron_ingot')", commented = true), @Example(value = "item('iceandfire:lightning_dragon_blood')", commented = true)
    })
    @Override
    public boolean removeByInput(IIngredient input) {
        return super.removeByInput(input);
    }

    @MethodDescription(example = @Example(value = "item('iceandfire:dragonsteel_lightning_ingot')", commented = true))
    @Override
    public boolean removeByOutput(IIngredient output) {
        return super.removeByOutput(output);
    }
}