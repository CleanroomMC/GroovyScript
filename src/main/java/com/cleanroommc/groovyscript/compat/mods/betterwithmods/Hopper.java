package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.registry.HopperInteractions;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Hopper extends StandardListRegistry<HopperInteractions.HopperRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('betterwithmods:iron_bar').input(ore('sand')).output(item('minecraft:clay')).inWorldItemOutput(item('minecraft:gold_ingot'))"),
            @Example(".name('betterwithmods:wicker').input(item('minecraft:clay') * 3).inWorldItemOutput(item('minecraft:gold_ingot'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<HopperInteractions.HopperRecipe> getRecipes() {
        return HopperInteractions.RECIPES;
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (ItemStack item : r.getInputs().getMatchingStacks()) {
                if (input.test(item)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "name", value = "groovyscript.wiki.betterwithmods.hopper.name.value", comp = @Comp(not = "null"))
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<HopperInteractions.HopperRecipe> {

        @Property(comp = @Comp(gte = 0, lte = 2))
        protected final ItemStackList inWorldItemOutput = new ItemStackList();

        @RecipeBuilderMethodDescription
        public RecipeBuilder inWorldItemOutput(ItemStack inWorldItemOutput) {
            this.inWorldItemOutput.add(inWorldItemOutput);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inWorldItemOutput(ItemStack... inWorldItemOutputs) {
            for (ItemStack inWorldItemOutput : inWorldItemOutputs) {
                inWorldItemOutput(inWorldItemOutput);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inWorldItemOutput(Collection<ItemStack> inWorldItemOutputs) {
            for (ItemStack inWorldItemOutput : inWorldItemOutputs) {
                inWorldItemOutput(inWorldItemOutput);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Filtered Hopper recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(super.name == null, "name cannot be null");
            validateItems(msg, 1, 1, 0, 2);
            validateCustom(msg, inWorldItemOutput, 0, 2, "item in world output");
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable HopperInteractions.HopperRecipe register() {
            if (!validate()) return null;

            HopperInteractions.HopperRecipe recipe = new HopperInteractions.HopperRecipe(super.name.toString(), BetterWithMods.Helper.fromIIngredient(input.get(0)), output, inWorldItemOutput);
            ModSupport.BETTER_WITH_MODS.get().hopper.add(recipe);
            return recipe;
        }
    }
}
