package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.BarrelRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Barrel extends ForgeRegistryWrapper<BarrelRecipe> {

    public Barrel() {
        super(ModuleTechBasic.Registries.BARREL_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(
            ".input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:emerald')).fluidInput(fluid('water') * 1000).fluidOutput(fluid('amongium') * 1000).duration(1000).name('diamond_emerald_and_water_to_amongium')")
    )
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'iron_dirt_water_to_lava', ore('ingotIron'), ore('ingotIron'), item('minecraft:dirt'), item('minecraft:dirt'), fluid('water'), fluid('lava'), 1000"))
    public BarrelRecipe add(String name, IIngredient input1, IIngredient input2, IIngredient input3, IIngredient input4, FluidStack fInput, FluidStack fOutput, int duration) {
        return recipeBuilder()
                .duration(duration)
                .name(name)
                .input(input1, input2, input3, input4)
                .fluidInput(fInput)
                .fluidOutput(fOutput)
                .register();
    }

    @MethodDescription(example = @Example("fluid('freckleberry_wine') * 1000"))
    public void removeByOutput(FluidStack output) {
        if (GroovyLog.msg("Error removing barrel recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BarrelRecipe recipe : getRegistry()) {
            if (recipe.getOutput().isFluidEqual(output)) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp(type = Comp.Type.EQ, value = "4"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<BarrelRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Barrel Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 4, 4, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.BARREL_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BarrelRecipe register() {
            if (!validate()) return null;

            // Because you need Ingredient[] to register a recipe
            Ingredient[] inputIngredient = input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new);

            BarrelRecipe recipe = new BarrelRecipe(fluidOutput.get(0), inputIngredient, fluidInput.get(0), duration).setRegistryName(name);
            PyroTech.barrel.add(recipe);

            return recipe;
        }
    }
}
