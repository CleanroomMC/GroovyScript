package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;

@RegistryDescription(override = @MethodOverride(recipeBuilder = {
        @RecipeBuilderDescription(
                method = "shapedBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredientMatrix", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:diamond') * 32).matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]])")),
        @RecipeBuilderDescription(
                method = "shapelessBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredients", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:clay') * 8).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"))
}, method = @MethodDescription(method = "removeByOutput", example = @Example("item('armorplus:emerald_helmet')"))))
public class HighTechBench extends AbstractBenchRegistry {

    @Override
    public BaseCraftingManager getInstance() {
        return BaseCraftingManager.getHTBInstance();
    }
}
