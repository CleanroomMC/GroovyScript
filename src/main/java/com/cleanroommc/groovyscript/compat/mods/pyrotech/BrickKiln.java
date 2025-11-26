package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickKilnRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = @Admonition(
                value = "groovyscript.wiki.pyrotech.brick_kiln.note0",
                format = Admonition.Format.STANDARD,
                hasTitle = true))
public class BrickKiln extends ForgeRegistryWrapper<BrickKilnRecipe> {

    public BrickKiln() {
        super(ModuleTechMachine.Registries.BRICK_KILN_RECIPES);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:fish')).output(item('minecraft:cooked_fish')).burnTime(200000).failureChance(0.99f).failureOutput(item('minecraft:dragon_egg'), item('minecraft:dragon_breath')).name('meaning_of_life')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'beetroot_soup', item('minecraft:beetroot'), item('minecraft:beetroot_soup'), 1200, 0.1f, item('minecraft:beetroot_seeds')"))
    public BrickKilnRecipe add(String name, IIngredient input, ItemStack output, int burnTime, float failureChance, ItemStack... failureOutput) {
        return recipeBuilder()
                .burnTime(burnTime)
                .failureChance(failureChance)
                .failureOutput(failureOutput)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:cobblestone')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing refractory oven recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickKilnRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('pyrotech:bucket_clay')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing refractory oven recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickKilnRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<BrickKilnRecipe> {

        @Property
        private final ItemStackList failureOutput = new ItemStackList();
        @Property(comp = @Comp(gt = 0))
        private int burnTime;
        @Property(comp = @Comp(gte = 0, lte = 1))
        private float failureChance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnTime(int time) {
            this.burnTime = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureChance(float chance) {
            this.failureChance = chance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(ItemStack failureOutputs) {
            this.failureOutput.add(failureOutputs);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(ItemStack... failureOutputs) {
            for (ItemStack itemStack : failureOutputs) {
                failureOutput(itemStack);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureOutput(Iterable<ItemStack> failureOutputs) {
            for (ItemStack itemStack : failureOutputs) {
                failureOutput(itemStack);
            }
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_brick_kiln_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Refractory Kiln Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            this.failureOutput.trim();
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(failureChance < 0 || failureChance > 1, "failureChance must not be negative nor larger than 1.0, yet it was {}", failureChance);
            msg.add(ModuleTechMachine.Registries.BRICK_KILN_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BrickKilnRecipe register() {
            if (!validate()) return null;
            BrickKilnRecipe recipe = new BrickKilnRecipe(output.get(0), input.get(0).toMcIngredient(), burnTime, failureChance, failureOutput.toArray(new ItemStack[0])).setRegistryName(super.name);
            ModSupport.PYROTECH.get().brickKiln.add(recipe);
            return recipe;
        }
    }
}
