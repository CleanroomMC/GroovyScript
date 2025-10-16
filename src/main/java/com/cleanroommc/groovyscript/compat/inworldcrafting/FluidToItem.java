package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import groovy.lang.Closure;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class FluidToItem extends VirtualizedRegistry<FluidToItem.Recipe> {

    @Override
    public void onReload() {
        getScriptedRecipes().forEach(FluidRecipe::remove);
        getBackupRecipes().forEach(FluidRecipe::add);
    }

    public void add(Recipe recipe) {
        addScripted(recipe);
        FluidRecipe.add(recipe);
    }

    public boolean remove(Recipe recipe) {
        if (FluidRecipe.remove(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription
    public boolean removeByInput(FluidStack fluid) {
        if (IngredientHelper.isEmpty(fluid)) {
            GroovyLog.msg("Error removing in world fluid to item recipe")
                    .add("input fluid must not be empty")
                    .error()
                    .post();
            return false;
        }
        if (!FluidRecipe.removeIf(fluid.getFluid(), fluidRecipe -> fluidRecipe.getClass() == Recipe.class, fluidRecipe -> addBackup((Recipe) fluidRecipe))) {
            GroovyLog.msg("Error removing in world fluid to item recipe")
                    .add("no recipes found for {}", fluid.getFluid().getName())
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    @MethodDescription
    public boolean removeByInput(FluidStack fluid, ItemStack... input) {
        if (GroovyLog.msg("Error removing in world fluid to item recipe")
                .add(IngredientHelper.isEmpty(fluid), () -> "input fluid must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input ingredients must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        if (!FluidRecipe.removeIf(fluid.getFluid(), fluidRecipe -> fluidRecipe.getClass() == Recipe.class && fluidRecipe.matches(input), fluidRecipe -> addBackup((Recipe) fluidRecipe))) {
            GroovyLog.msg("Error removing in world fluid to item recipe")
                    .add("no recipes found for {}", fluid.getFluid().getName())
                    .error()
                    .post();
            return false;
        }
        return true;
    }

    @MethodDescription
    public boolean removeAll() {
        return FluidRecipe.removeIf(fluidRecipe -> fluidRecipe.getClass() == Recipe.class, fluidRecipe -> addBackup((Recipe) fluidRecipe));
    }

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('water'), 0.22f).input(item('minecraft:netherrack')).input(item('minecraft:gold_ingot'), 0.1f).output(item('minecraft:nether_star'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription
    public SimpleObjectStream<Recipe> streamRecipes() {
        return new SimpleObjectStream<>(FluidRecipe.findRecipesOfType(Recipe.class)).setRemover(this::remove);
    }

    public static class Recipe extends FluidRecipe {

        private final ItemStack output;
        private final float fluidConsumptionChance;

        public Recipe(Fluid input, IIngredient[] itemInputs, float[] itemConsumeChance, Closure<Boolean> startCondition, Closure<?> afterRecipe, ItemStack output, float fluidConsumptionChance) {
            super(input, itemInputs, itemConsumeChance, startCondition, afterRecipe);
            this.output = output;
            this.fluidConsumptionChance = fluidConsumptionChance;
        }

        public ItemStack getOutput() {
            return output;
        }

        public float getFluidConsumptionChance() {
            return fluidConsumptionChance;
        }

        @Override
        public void setJeiOutput(IIngredients ingredients) {
            ingredients.setOutput(VanillaTypes.ITEM, getOutput());
        }

        @Override
        public void handleRecipeResult(World world, BlockPos pos) {
            if (this.fluidConsumptionChance > 0 && (this.fluidConsumptionChance >= 1 || GroovyScript.RND.nextFloat() <= this.fluidConsumptionChance)) {
                world.setBlockToAir(pos);
            }
            InWorldCrafting.spawnItem(world, pos, getOutput().copy());
        }
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = Recipe.MAX_ITEM_INPUT))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends FluidRecipe.RecipeBuilder<FluidToItem.Recipe> {

        @Property(comp = @Comp(gte = 0, lte = 1), defaultValue = "1.0f")
        private float fluidConsumptionChance = 1.0f;

        @RecipeBuilderMethodDescription(field = {"fluidInput", "fluidConsumptionChance"})
        public RecipeBuilder fluidInput(FluidStack fluidStack, float fluidConsumptionChance) {
            fluidInput(fluidStack);
            return fluidConsumptionChance(fluidConsumptionChance);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluidConsumptionChance(float fluidConsumptionChance) {
            this.fluidConsumptionChance = fluidConsumptionChance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding in world fluid to item recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Recipe.MAX_ITEM_INPUT, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            validateChances(msg);
            this.fluidConsumptionChance = MathHelper.clamp(this.fluidConsumptionChance, 0.0f, 1.0f);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FluidToItem.Recipe register() {
            Recipe recipe = new Recipe(
                    this.fluidInput.get(0).getFluid(),
                    this.input.toArray(new IIngredient[0]),
                    this.chances.toFloatArray(),
                    this.startCondition,
                    this.afterRecipe,
                    this.output.get(0),
                    this.fluidConsumptionChance);
            VanillaModule.INSTANCE.inWorldCrafting.fluidToItem.add(recipe);
            return recipe;
        }
    }
}
