package com.cleanroommc.groovyscript.compat.mods.betterwithaddons;

import betterwithaddons.crafting.manager.CraftingManagerInfuser;
import betterwithaddons.crafting.recipes.infuser.InfuserRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;

import java.util.Collection;

@RegistryDescription
public class Infuser extends StandardListRegistry<InfuserRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:stone')).matrix('BXX', 'X B').key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot')).spirits(1).mirrored()"),
            @Example(".output(item('minecraft:diamond') * 32).matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).spirits(6)")
    })
    public InfuserRecipeBuilder.Shaped shapedBuilder() {
        return new InfuserRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay') * 8).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"),
            @Example(".output(item('minecraft:clay') * 32).input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond')).spirits(8)")
    })
    public InfuserRecipeBuilder.Shapeless shapelessBuilder() {
        return new InfuserRecipeBuilder.Shapeless();
    }

    @Override
    public Collection<InfuserRecipe> getRecipes() {
        return CraftingManagerInfuser.getInstance().getRecipeList();
    }

    @MethodDescription(example = @Example("item('betterwithaddons:japanmat:16')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (var ingredient : r.internal.getIngredients()) {
                for (var stack : ingredient.getMatchingStacks()) {
                    if (input.test(stack)) {
                        return doAddBackup(r);
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('betterwithaddons:ya')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.internal.getRecipeOutput()) && doAddBackup(r));
    }
}
