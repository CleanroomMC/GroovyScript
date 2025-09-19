package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickCrucibleRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BrickCrucible extends ForgeRegistryWrapper<BrickCrucibleRecipe> {

    public BrickCrucible() {
        super(ModuleTechMachine.Registries.BRICK_CRUCIBLE_RECIPES);
    }

    @RecipeBuilderDescription(example = @Example("input(item('minecraft:vine')).fluidOutput(fluid('water') * 250).burnTime(60).name('water_from_vine')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'lava_from_obsidian', ore('obsidian'), fluid('lava') * 1000, 2000"))
    public BrickCrucibleRecipe add(String name, IIngredient input, FluidStack output, int burnTime) {
        return recipeBuilder()
                .burnTime(burnTime)
                .name(name)
                .input(input)
                .fluidOutput(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:gravel')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing refractory crucible recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickCrucibleRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('water') * 125"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing refractory crucible recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickCrucibleRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<BrickCrucibleRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int burnTime;

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnTime(int burnTime) {
            this.burnTime = burnTime;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Refractory Crucible Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
            msg.add(burnTime < 0,  "burnTime must be a non negative integer, yet it was {}", burnTime);
            msg.add(super.name == null, "name cannot be null");
            msg.add(ModuleTechMachine.Registries.STONE_CRUCIBLE_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BrickCrucibleRecipe register() {
            if (!validate()) return null;
            BrickCrucibleRecipe recipe = new BrickCrucibleRecipe(fluidOutput.get(0), input.get(0).toMcIngredient(), burnTime).setRegistryName(super.name);
            ModSupport.PYROTECH.get().brickCrucible.add(recipe);
            return recipe;
        }
    }
}
