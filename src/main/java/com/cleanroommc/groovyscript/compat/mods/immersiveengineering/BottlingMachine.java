package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BottlingMachineRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class BottlingMachine extends StandardListRegistry<BottlingMachineRecipe> {

    public BottlingMachine() {
        super(Alias.generateOfClassAnd(BottlingMachine.class, "Bottling"));
    }

    @Override
    public Collection<BottlingMachineRecipe> getRecipes() {
        return BottlingMachineRecipe.recipeList;
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).fluidInput(fluid('water')).output(item('minecraft:clay'))"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public BottlingMachineRecipe add(ItemStack output, IIngredient input, FluidStack fluidInput) {
        BottlingMachineRecipe recipe = new BottlingMachineRecipe(output.copy(), ImmersiveEngineering.toIngredientStack(input), fluidInput);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('minecraft:potion').withNbt([Potion:'minecraft:mundane'])"))
    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Bottling Machine recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!getRecipes().removeIf(recipe -> {
            if (ApiUtils.stackMatchesObject(output, recipe.output, true)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Bottling Machine recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
        }
    }

    @MethodDescription(example = @Example("item('minecraft:sponge'), fluid('water') * 1000"))
    public void removeByInput(ItemStack input, FluidStack inputFluid) {
        if (GroovyLog.msg("Error removing Immersive Engineering Bottling Machine recipe")
                .add(IngredientHelper.isEmpty(input), () -> "item input must not be empty")
                .add(IngredientHelper.isEmpty(inputFluid), () -> "fluid input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        List<BottlingMachineRecipe> recipes = getRecipes().stream().filter(r -> ApiUtils.stackMatchesObject(input, r.input) && inputFluid.isFluidEqual(r.fluidInput)).collect(Collectors.toList());
        for (BottlingMachineRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Bottling Machine recipe")
                    .add("no recipes found for {} and {}", input, inputFluid)
                    .error()
                    .post();
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<BottlingMachineRecipe> {

        @Override
        protected int getMaxItemInput() {
            // Recipe cannot be executed
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Bottling recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BottlingMachineRecipe register() {
            if (!validate()) return null;
            BottlingMachineRecipe recipe = new BottlingMachineRecipe(output.get(0), ImmersiveEngineering.toIngredientStack(input.get(0)), fluidInput.get(0));
            ModSupport.IMMERSIVE_ENGINEERING.get().bottlingMachine.add(recipe);
            return recipe;
        }

    }

}
