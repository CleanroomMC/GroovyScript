package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickOvenRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = @Admonition(
                value = "groovyscript.wiki.pyrotech.oven.note0",
                type = Admonition.Type.WARNING,
                format = Admonition.Format.STANDARD,
                hasTitle = true))
public class BrickOven extends ForgeRegistryWrapper<BrickOvenRecipe> {

    public BrickOven() {
        super(ModuleTechMachine.Registries.BRICK_OVEN_RECIPES);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:chorus_fruit')).output(item('minecraft:chorus_fruit_popped')).duration(800).name('chorus_fruit_whats_popping')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'lead_poisoning', item('minecraft:slime_ball'), item('minecraft:lead') * 16, 1000"))
    public BrickOvenRecipe add(String name, IIngredient input, ItemStack output, int duration) {
        return recipeBuilder()
                .duration(duration)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:porkchop')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing brick oven recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickOvenRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:cooked_porkchop')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing brick oven recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickOvenRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<BrickOvenRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Brick Oven Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechMachine.Registries.BRICK_OVEN_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BrickOvenRecipe register() {
            if (!validate()) return null;
            BrickOvenRecipe recipe = new BrickOvenRecipe(output.get(0), input.get(0).toMcIngredient(), duration).setRegistryName(super.name);
            ModSupport.PYROTECH.get().brickOven.add(recipe);
            return recipe;
        }
    }
}
