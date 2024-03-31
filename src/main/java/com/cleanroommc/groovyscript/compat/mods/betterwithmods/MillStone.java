package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.bulk.recipes.MillRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

@RegistryDescription
public class MillStone extends VirtualizedRegistry<MillRecipe> {

    public MillStone() {
        super(Alias.generateOfClass(MillStone.class).andGenerate("Mill"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 16)"),
            @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:gold_ingot'), item('minecraft:gold_block'), item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BWRegistry.MILLSTONE.getRecipes().removeIf(r -> r == recipe));
        BWRegistry.MILLSTONE.getRecipes().addAll(restoreFromBackup());
    }

    public MillRecipe add(MillRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BWRegistry.MILLSTONE.getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(MillRecipe recipe) {
        if (BWRegistry.MILLSTONE.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:blaze_powder')"))
    public boolean removeByOutput(IIngredient output) {
        return BWRegistry.MILLSTONE.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:netherrack')"))
    public boolean removeByInput(IIngredient input) {
        return BWRegistry.MILLSTONE.getRecipes().removeIf(r -> {
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

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<MillRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BWRegistry.MILLSTONE.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BWRegistry.MILLSTONE.getRecipes().forEach(this::addBackup);
        BWRegistry.MILLSTONE.getRecipes().clear();
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "3", type = Comp.Type.LTE)})
    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "3", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<MillRecipe> {

        @Property
        private SoundEvent soundEvent;
        @Property(defaultValue = "1", valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int ticks = 1;
        @Property(defaultValue = "1")
        private int priority = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription
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
