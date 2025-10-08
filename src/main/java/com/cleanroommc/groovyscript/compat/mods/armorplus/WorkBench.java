package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;

@RegistryDescription(override = @MethodOverride(recipeBuilder = {
        @RecipeBuilderDescription(
                method = "shapedBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredientMatrix", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:stone') * 8).matrix('BXX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))")),
        @RecipeBuilderDescription(
                method = "shapelessBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredients", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:clay') * 8).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"))
}, method = @MethodDescription(method = "removeByOutput", example = @Example("item('armorplus:the_gift_of_the_gods')"))))
public class WorkBench extends AbstractBenchRegistry {

    @Override
    public BaseCraftingManager getInstance() {
        return BaseCraftingManager.getWBInstance();
    }
}
