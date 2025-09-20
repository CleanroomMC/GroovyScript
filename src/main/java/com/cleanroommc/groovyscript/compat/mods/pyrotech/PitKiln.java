package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.KilnPitRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickKilnRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.StoneKilnRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneKilnRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class PitKiln extends ForgeRegistryWrapper<KilnPitRecipe> {

    public PitKiln() {
        super(ModuleTechBasic.Registries.KILN_PIT_RECIPE, Alias.generateOfClass(PitKiln.class).andGenerate("Kiln"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:gold_ingot')).burnTime(400).failureChance(1f).failureOutput(item('minecraft:wheat'), item('minecraft:carrot'), item('minecraft:sponge')).name('iron_to_gold_kiln_with_failure_items')"),
            @Example(".input(item('minecraft:record_11')).output(item('minecraft:record_13')).burnTime(200).failureChance(0f).inherit(true).name('record_11_to_record_13')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public KilnPitRecipe add(String name, IIngredient input, ItemStack output, int burnTime, float failureChance, ItemStack... failureOutput) {
        return add(name, input, output, burnTime, false, failureChance, failureOutput);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'brick_to_iron', item('minecraft:brick'), item('minecraft:iron_ingot'), 1200, true, 0.5f, item('minecraft:dirt'), item('minecraft:cobblestone')"))
    public KilnPitRecipe add(String name, IIngredient input, ItemStack output, int burnTime, boolean inherit, float failureChance, ItemStack... failureOutput) {
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

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('pyrotech:bucket_refractory_unfired')"))
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

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('pyrotech:bucket_clay')"))
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

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<KilnPitRecipe> {

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
        public String getErrorMsg() {
            return "Error adding Pyrotech Pit Kiln Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            this.failureOutput.trim();
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(failureChance < 0, "failureChance must be a non negative float, yet it was {}", failureChance);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.KILN_PIT_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable KilnPitRecipe register() {
            if (!validate()) return null;
            KilnPitRecipe recipe = new KilnPitRecipe(output.get(0), input.get(0).toMcIngredient(), burnTime, failureChance, failureOutput.toArray(new ItemStack[0])).setRegistryName(super.name);
            ModSupport.PYROTECH.get().pitKiln.add(recipe);
            if (inherit && ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class)) {
                ResourceLocation location = new ResourceLocation(super.name.getNamespace(), "pit_kiln/" + super.name.getPath());
                StoneKilnRecipe stoneKilnRecipe = StoneKilnRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(location);
                ModSupport.PYROTECH.get().stoneKiln.add(stoneKilnRecipe);
                ModSupport.PYROTECH.get().brickKiln.add(BrickKilnRecipesAdd.INHERIT_TRANSFORMER.apply(stoneKilnRecipe).setRegistryName(location));
            }
            return recipe;
        }
    }
}
