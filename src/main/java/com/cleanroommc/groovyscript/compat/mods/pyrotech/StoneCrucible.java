package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickCrucibleRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickCrucibleRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneCrucibleRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class StoneCrucible extends ForgeRegistryWrapper<StoneCrucibleRecipe> {

    public StoneCrucible() {
        super(ModuleTechMachine.Registries.STONE_CRUCIBLE_RECIPES);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(ore('sugarcane')).fluidOutput(fluid('water') * 500).burnTime(1000).inherit(true).name('water_from_sugarcane')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public StoneCrucibleRecipe add(String name, IIngredient input, FluidStack output, int burnTime) {
        return add(name, input, output, burnTime, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.stone_crucible.add.inherit", example = @Example("'water_from_cactus', ore('blockCactus'), fluid('water') * 1000, 600, true"))
    public StoneCrucibleRecipe add(String name, IIngredient input, FluidStack output, int burnTime, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .burnTime(burnTime)
                .name(name)
                .input(input)
                .fluidOutput(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:ice')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing stone crucible recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneCrucibleRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('water') * 500"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing stone crucible recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneCrucibleRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneCrucibleRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int burnTime;
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnTime(int burnTime) {
            this.burnTime = burnTime;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_stone_crucible_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Stone Crucible Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
            msg.add(burnTime <= 0, "burnTime must be a non negative integer that is larger than 0, yet it was {}", burnTime);
            msg.add(ModuleTechMachine.Registries.STONE_CRUCIBLE_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable StoneCrucibleRecipe register() {
            if (!validate()) return null;
            StoneCrucibleRecipe recipe = new StoneCrucibleRecipe(fluidOutput.get(0), input.get(0).toMcIngredient(), burnTime).setRegistryName(super.name);
            ModSupport.PYROTECH.get().stoneCrucible.add(recipe);
            if (inherit) {
                BrickCrucibleRecipe brickCrucibleRecipe = BrickCrucibleRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(new ResourceLocation(super.name.getNamespace(), "stone_crucible/" + super.name.getPath()));
                ModSupport.PYROTECH.get().brickCrucible.add(brickCrucibleRecipe);
            }
            return recipe;
        }
    }
}
