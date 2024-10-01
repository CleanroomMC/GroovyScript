package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CampfireRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneOvenRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.pyrotech.oven.note0",
                                               type = Admonition.Type.WARNING,
                                               format = Admonition.Format.STANDARD,
                                               hasTitle = true))
public class StoneOven extends ForgeRegistryWrapper<StoneOvenRecipe> {

    public StoneOven() {
        super(ModuleTechMachine.Registries.STONE_OVEN_RECIPES);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).duration(400).name('diamond_campfire_to_emerald_stone')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_dirt_stone', item('minecraft:apple'), item('minecraft:dirt'), 1000"))
    public StoneOvenRecipe add(String name, IIngredient input, ItemStack output, int duration) {
        return recipeBuilder()
                .duration(duration)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:porkchop')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing stone oven recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneOvenRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('minecraft:cooked_porkchop')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing stone oven recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneOvenRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneOvenRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Stone Oven Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechMachine.Registries.STONE_OVEN_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable StoneOvenRecipe register() {
            if (!validate()) return null;
            StoneOvenRecipe recipe = new StoneOvenRecipe(output.get(0), input.get(0).toMcIngredient(), duration).setRegistryName(super.name);
            PyroTech.stoneOven.add(recipe);
            return recipe;
        }
    }
}
