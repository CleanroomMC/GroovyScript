package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.SoakingPotRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class SoakingPot extends ForgeRegistryWrapper<SoakingPotRecipe> {

    public SoakingPot() {
        super(ModuleTechBasic.Registries.SOAKING_POT_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).fluidInput(fluid('amongium') * 125).output(item('minecraft:emerald')).time(400).campfireRequired(true).name('diamond_to_emerald_with_amongium_soaking_pot')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'dirt_to_apple', item('minecraft:dirt'), fluid('water'), item('minecraft:apple'), 1200"))
    public SoakingPotRecipe add(String name, IIngredient input, FluidStack fluidInput, ItemStack output, int time) {
        return recipeBuilder()
                .time(time)
                .name(name)
                .input(input)
                .fluidInput(fluidInput)
                .output(output)
                .register();
    }

    @MethodDescription
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing soaking pot recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (SoakingPotRecipe recipe : getRegistry()) {
            if (recipe.getInputItem().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('pyrotech:material', 54)"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing soaking pot recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (SoakingPotRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<SoakingPotRecipe> {

        @Property
        private boolean campfireRequired;

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder campfireRequired(boolean campfireRequired) {
            this.campfireRequired = campfireRequired;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Soaking Pot Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.SOAKING_POT_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @Override
        public @Nullable SoakingPotRecipe register() {
            if (!validate()) return null;
            SoakingPotRecipe recipe = new SoakingPotRecipe(output.get(0), input.get(0).toMcIngredient(), fluidInput.get(0), campfireRequired, time).setRegistryName(name);
            PyroTech.soakingPot.add(recipe);
            return recipe;
        }
    }
}
