package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import lykrast.prodigytech.common.recipe.*;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleRecipeHandlerSecondaryOutput extends SimpleRecipeHandlerAbstract<SimpleRecipeSecondaryOutput> {
    @RecipeBuilderDescription(example = {
        @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).time(50)"),
        @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:coal'))"),
        @Example(".input(item('minecraft:iron_block')).output(item('minecraft:emerald'), item('minecraft:clay'))"),
        @Example(".input(item('minecraft:gold_block')).output(item('minecraft:emerald'), item('minecraft:nether_star')).secondaryChance(0.25).time(50)")
    })
    public SimpleRecipeHandlerSecondaryOutput.RecipeBuilder recipeBuilder() {
        return new SimpleRecipeHandlerSecondaryOutput.RecipeBuilder();
    }

    SimpleRecipeHandlerSecondaryOutput(String name, SimpleRecipeManagerSecondaryOutput instance) {
        super(name, instance);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    public class RecipeBuilder extends AbstractRecipeBuilder<SimpleRecipe> {
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time = getDefaultTime();

        @Property(valid = @Comp(value = "1", type = Comp.Type.LTE), defaultValue = "1.0f")
        private float secondaryChance = 1.0f;

        @RecipeBuilderMethodDescription
        public SimpleRecipeHandlerSecondaryOutput.RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public SimpleRecipeHandlerSecondaryOutput.RecipeBuilder secondaryChance(float secondaryOutput) {
            this.secondaryChance = secondaryOutput;
            return this;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(time <= 0, "time must be greater than 0, got {}", time);
            msg.add(secondaryChance > 1.0f || secondaryChance < 0.0f, "secondary output has to be between 0 and 1, got {}", secondaryChance);
        }

        @Override
        public String getErrorMsg() {
            return String.format("Error adding ProdigyTech %s Recipe", SimpleRecipeHandlerSecondaryOutput.this.name);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SimpleRecipeSecondaryOutput register() {
            if (!validate()) return null;
            SimpleRecipeSecondaryOutput recipe = null;
            IIngredient input1 = input.get(0);
            ItemStack secondaryOutput = output.size() == 1 ? ItemStack.EMPTY : output.get(1);
            if (input1 instanceof OreDictIngredient) {
                recipe = new SimpleRecipeSecondaryOutput(((OreDictIngredient) input1).getOreDict(), output.get(0), secondaryOutput, time, secondaryChance);
                addRecipe(recipe);
            } else {
                for (ItemStack input : input1.getMatchingStacks()) {
                    recipe = new SimpleRecipeSecondaryOutput(input, output.get(0), secondaryOutput, time, secondaryChance);
                    addRecipe(recipe);
                }
            }
            return recipe;
        }
    }

    @RegistryDescription
    public static class HeatSawmill extends SimpleRecipeHandlerSecondaryOutput {
        HeatSawmill() {
            super("Heat Sawmill", HeatSawmillManager.INSTANCE);
        }

        @Override
        protected int getDefaultTime() {
            return Config.heatSawmillProcessTime;
        }

        @Override
        @MethodDescription(example = @Example("ore('plankWood')"))
        public boolean removeByInput(IIngredient input) {
            return super.removeByInput(input);
        }
    }

    @RegistryDescription
    public static class OreRefinery extends SimpleRecipeHandlerSecondaryOutput {
        OreRefinery() {
            super("Ore Refinery", OreRefineryManager.INSTANCE);
        }

        @Override
        protected int getDefaultTime() {
            return Config.oreRefineryProcessTime;
        }

        @Override
        @MethodDescription(example = @Example("ore('oreLapis')"))
        public boolean removeByInput(IIngredient input) {
            return super.removeByInput(input);
        }
    }
}
