package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import lykrast.prodigytech.common.recipe.MagneticReassemblerManager;
import lykrast.prodigytech.common.recipe.RotaryGrinderManager;
import lykrast.prodigytech.common.recipe.SimpleRecipe;
import lykrast.prodigytech.common.recipe.SimpleRecipeManager;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleRecipeHandler extends SimpleRecipeHandlerAbstract<SimpleRecipe> {
    @RecipeBuilderDescription(example = {
        @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond')).time(50)"),
        @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:coal'))")
    })
    public SimpleRecipeHandler.RecipeBuilder recipeBuilder() {
        return new SimpleRecipeHandler.RecipeBuilder();
    }

    SimpleRecipeHandler(String name, SimpleRecipeManager instance) {
        super(name, instance);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public class RecipeBuilder extends AbstractRecipeBuilder<SimpleRecipe> {
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE), defaultValue = "(default time for this machine in the mod's config)")
        private int time = getDefaultTime();

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(time <= 0, "time must be greater than 0, got {}", time);
        }

        @Override
        public String getErrorMsg() {
            return String.format("Error adding ProdigyTech %s Recipe", SimpleRecipeHandler.this.name);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SimpleRecipe register() {
            if (!validate()) return null;
            SimpleRecipe recipe = null;
            IIngredient input1 = input.get(0);
            if (input1 instanceof OreDictIngredient) {
                recipe = new SimpleRecipe(((OreDictIngredient) input1).getOreDict(), output.get(0), time);
                addRecipe(recipe);
            } else {
                for (ItemStack input : input1.getMatchingStacks()) {
                    recipe = new SimpleRecipe(input, output.get(0), time);
                    addRecipe(recipe);
                }
            }
            return recipe;
        }
    }

    @RegistryDescription
    public static class RotaryGrinder extends SimpleRecipeHandler {
        RotaryGrinder() {
            super("Rotary Grinder", RotaryGrinderManager.INSTANCE);
        }

        @Override
        protected int getDefaultTime() {
            return Config.rotaryGrinderProcessTime;
        }

        @Override
        @MethodDescription(example = @Example("item('minecraft:gravel')"))
        public boolean removeByInput(IIngredient input) {
            return super.removeByInput(input);
        }
    }

    @RegistryDescription
    public static class MagneticReassembler extends SimpleRecipeHandler {
        MagneticReassembler() {
            super("Magnetic Reassembler", MagneticReassemblerManager.INSTANCE);
        }

        @Override
        protected int getDefaultTime() {
            return Config.magneticReassemblerProcessTime;
        }

        @Override
        @MethodDescription(example = @Example("item('minecraft:gravel')"))
        public boolean removeByInput(IIngredient input) {
            return super.removeByInput(input);
        }
    }
}
