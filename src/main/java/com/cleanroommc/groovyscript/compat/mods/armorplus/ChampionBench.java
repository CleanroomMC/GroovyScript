package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;

@RegistryDescription(override = @MethodOverride(recipeBuilder = {
        @RecipeBuilderDescription(
                method = "shapedBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredientMatrix", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:stone') * 64).matrix('DLLLLLDDD', '  DNIGIND', 'DDDNIGIND', '  DLLLLLD').key('D', item('minecraft:diamond')).key('L', item('minecraft:redstone')).key('N', item('minecraft:stone')).key('I', item('minecraft:iron_ingot')).key('G', item('minecraft:gold_ingot'))")),
        @RecipeBuilderDescription(
                method = "shapelessBuilder",
                override = @RecipeBuilderOverride(requirement = @Property(property = "ingredients", comp = @Comp(gte = 1, lte = 9, unique = "groovyscript.wiki.craftingrecipe.matrix.required"))),
                example = @Example(".output(item('minecraft:clay') * 32).input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'))"))
}, method = @MethodDescription(method = "removeByOutput", example = @Example(commented = true))))
public class ChampionBench extends AbstractBenchRegistry {

    @Override
    public BaseCraftingManager getInstance() {
        return BaseCraftingManager.getCBInstance();
    }
}
