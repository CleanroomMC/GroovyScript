package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.bulk.recipes.MillRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

@RegistryDescription
public class MillStone extends StandardListRegistry<MillRecipe> {

    public MillStone() {
        super(Alias.generateOfClass(MillStone.class).andGenerate("Mill"));
    }

    @Override
    public Collection<MillRecipe> getRecipes() {
        return BWRegistry.MILLSTONE.getRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 16)"),
            @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:gold_ingot'), item('minecraft:gold_block'), item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:blaze_powder')"))
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

    @MethodDescription(example = @Example("item('minecraft:netherrack')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (Ingredient ingredient : r.getInputs()) {
                for (ItemStack item : ingredient.getMatchingStacks()) {
                    if (input.test(item)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 3))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 3))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MillRecipe> {

        @Property
        private SoundEvent soundEvent;
        @Property(defaultValue = "1", comp = @Comp(gte = 1))
        private int ticks = 1;
        @Property(defaultValue = "1")
        private int priority = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "ticks")
        public RecipeBuilder time(int ticks) {
            this.ticks = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder soundEvent(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Mill recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 3);
            validateFluids(msg);
            msg.add(ticks <= 0, "ticks must be a positive integer greater than 0, yet it was {}", ticks);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MillRecipe register() {
            if (!validate()) return null;

            MillRecipe recipe = new MillRecipe(input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList()), output);
            recipe.setSound(soundEvent);
            recipe.setTicks(ticks);
            recipe.setPriority(priority);
            ModSupport.BETTER_WITH_MODS.get().millStone.add(recipe);
            return recipe;
        }

    }

}
