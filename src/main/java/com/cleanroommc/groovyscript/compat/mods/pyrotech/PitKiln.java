package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.KilnPitRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class PitKiln extends ForgeRegistryWrapper<KilnPitRecipe> {

    public PitKiln() {
        super(ModuleTechBasic.Registries.KILN_PIT_RECIPE, Alias.generateOfClass(PitKiln.class).andGenerate("Kiln"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:gold_ingot')).burnTime(400).failureChance(1f).failureOutput(item('minecraft:wheat'), item('minecraft:carrot'), item('minecraft:sponge')).name('iron_to_gold_kiln_with_failure_items')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'clay_to_iron', item('minecraft:clay_ball') * 5, item('minecraft:iron_ingot'), 1200, 0.5f, [item('minecraft:dirt'), item('minecraft:cobblestone')]"))
    public KilnPitRecipe add(String name, IIngredient input, ItemStack output, int burnTime, float failureChance, Iterable<ItemStack> failureOutput) {
        return recipeBuilder()
                .burnTime(burnTime)
                .failureChance(failureChance)
                .failureOutput(failureOutput)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing pit kiln recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (KilnPitRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('pyrotech:bucket_clay')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing pit kiln recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (KilnPitRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<KilnPitRecipe> {

        @Property
        private final ItemStackList failureOutput = new ItemStackList();
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int burnTime;
        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
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
            for (ItemStack itemStack : failureOutputs) failureOutput(itemStack);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Pit Kiln Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            this.failureOutput.trim();
            validateCustom(msg, failureOutput, 1, 100, "failure output");
            msg.add(burnTime < 0, "burnTime must be a non negative integer, yet it was {}", burnTime);
            msg.add(failureChance < 0, "failureChance must be a non negative float, yet it was {}", failureChance);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.KILN_PIT_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable KilnPitRecipe register() {
            if (!validate()) return null;
            KilnPitRecipe recipe = new KilnPitRecipe(output.get(0), input.get(0).toMcIngredient(), burnTime, failureChance, failureOutput.toArray(new ItemStack[0])).setRegistryName(super.name);
            PyroTech.pitKiln.add(recipe);
            return recipe;
        }
    }
}
