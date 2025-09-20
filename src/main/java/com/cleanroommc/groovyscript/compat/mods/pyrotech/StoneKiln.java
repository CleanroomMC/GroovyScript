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
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickKilnRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneKilnRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = @Admonition(
                value = "groovyscript.wiki.pyrotech.stone_kiln.note0",
                format = Admonition.Format.STANDARD,
                hasTitle = true))
public class StoneKiln extends ForgeRegistryWrapper<StoneKilnRecipe> {

    public StoneKiln() {
        super(ModuleTechMachine.Registries.STONE_KILN_RECIPES);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).burnTime(800).failureChance(0.6f).failureOutput(item('minecraft:egg'), item('minecraft:fish')).name('diamond_to_emerald_with_failure_outputs')"),
            @Example(".input(item('minecraft:compass')).output(item('minecraft:clock')).burnTime(1200).failureChance(0f).inherit(true).name('compass_to_clock')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public StoneKilnRecipe add(String name, IIngredient input, ItemStack output, int burnTime, float failureChance, ItemStack failureOutput) {
        return add(name, input, output, burnTime, false, failureChance, failureOutput);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'clay_to_iron_stone', item('minecraft:clay_ball'), item('minecraft:iron_ingot'), 1200, true, 0.5f, item('minecraft:dirt'), item('minecraft:cobblestone')"))
    public StoneKilnRecipe add(String name, IIngredient input, ItemStack output, int burnTime, boolean inherit, float failureChance, ItemStack... failureOutput) {
        return recipeBuilder()
                .inherit(inherit)
                .burnTime(burnTime)
                .failureChance(failureChance)
                .failureOutput(failureOutput)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:sand')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing stone kiln recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneKilnRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('pyrotech:bucket_clay')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing stone iln recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneKilnRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneKilnRecipe> {

        @Property
        private final ItemStackList failureOutput = new ItemStackList();
        @Property(comp = @Comp(gt = 0))
        private int burnTime;
        @Property(comp = @Comp(gte = 0))
        private float failureChance;
        @Property
        private boolean inherit;

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

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Stone Kiln Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            this.failureOutput.trim();
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(failureChance < 0, "failureChance must be a non negative float, yet it was {}", failureChance);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechMachine.Registries.STONE_KILN_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable StoneKilnRecipe register() {
            if (!validate()) return null;
            StoneKilnRecipe recipe = new StoneKilnRecipe(output.get(0), input.get(0).toMcIngredient(), burnTime, failureChance, failureOutput.toArray(new ItemStack[0])).setRegistryName(super.name);
            ModSupport.PYROTECH.get().stoneKiln.add(recipe);
            if (inherit) {
                ModSupport.PYROTECH.get().brickKiln.add(BrickKilnRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(super.name.getNamespace(), "stone_kiln/" + super.name.getPath()));
            }
            return recipe;
        }
    }
}
