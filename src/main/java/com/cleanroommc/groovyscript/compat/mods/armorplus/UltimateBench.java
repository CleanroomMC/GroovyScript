package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;

@RegistryDescription(override = @MethodOverride(recipeBuilder = {
        @RecipeBuilderDescription(
                method = "shapedBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredientMatrix", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:diamond')).matrix('BXXXBX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))")),
        @RecipeBuilderDescription(
                method = "shapelessBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredients", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:stone') * 64).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"))
}, method = @MethodDescription(method = "removeByOutput", example = @Example("item('armorplus:the_ultimate_helmet')"))))
public class UltimateBench extends AbstractBenchRegistry {

    @Override
    public BaseCraftingManager getInstance() {
        return BaseCraftingManager.getUTBInstance();
    }
}
