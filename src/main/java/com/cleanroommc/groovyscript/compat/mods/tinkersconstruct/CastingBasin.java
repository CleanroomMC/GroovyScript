package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;

@RegistryDescription
public class CastingBasin extends VirtualizedRegistry<ICastingRecipe> {

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('water')).output(item('minecraft:dirt')).cast(item('minecraft:cobblestone')).coolingTime(40)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public CastingBasin() {
        super(Alias.generateOfClass(CastingBasin.class).andGenerate("Basin"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(TinkerRegistryAccessor.getBasinCastRegistry()::remove);
        restoreFromBackup().forEach(TinkerRegistryAccessor.getBasinCastRegistry()::add);
    }

    public void add(ICastingRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        TinkerRegistryAccessor.getBasinCastRegistry().add(recipe);
    }

    public boolean remove(ICastingRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        TinkerRegistryAccessor.getBasinCastRegistry().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:iron_block')"))
    public boolean removeByOutput(ItemStack output) {
        if (TinkerRegistryAccessor.getBasinCastRegistry().removeIf(recipe -> {
            boolean found = ItemStack.areItemStacksEqual(recipe.getResult(ItemStack.EMPTY, FluidRegistry.WATER), output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Casting Basin recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("fluid('clay')"))
    public boolean removeByInput(FluidStack input) {
        if (TinkerRegistryAccessor.getBasinCastRegistry().removeIf(recipe -> {
            boolean found = recipe.getFluid(ItemStack.EMPTY, input.getFluid()).isFluidEqual(input);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Casting Basin recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:planks:0')"))
    public boolean removeByCast(IIngredient cast) {
        ItemStack castStack = cast.getMatchingStacks()[0];
        if (TinkerRegistryAccessor.getBasinCastRegistry().removeIf(recipe -> {
            boolean found = recipe.matches(castStack, recipe.getFluid(castStack, FluidRegistry.WATER).getFluid());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Tinkers Construct Casting Basin recipe")
                .add("could not find recipe with cast {}", cast)
                .error()
                .post();
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TinkerRegistryAccessor.getBasinCastRegistry().forEach(this::addBackup);
        TinkerRegistryAccessor.getBasinCastRegistry().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ICastingRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TinkerRegistryAccessor.getBasinCastRegistry()).setRemover(this::remove);
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public class RecipeBuilder extends AbstractRecipeBuilder<ICastingRecipe> {

        @Property
        private IIngredient cast;
        @Property(defaultValue = "200", valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time = 200;
        @Property
        private boolean consumesCast = false;

        @RecipeBuilderMethodDescription(field = "time")
        public RecipeBuilder coolingTime(int time) {
            this.time = Math.max(time, 1);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumesCast(boolean consumesCast) {
            this.consumesCast = consumesCast;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder consumesCast() {
            return consumesCast(!consumesCast);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder cast(IIngredient ingredient) {
            this.cast = ingredient;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tinkers Construct Casting Basin recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 1, 1, 0, 0);
            validateItems(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICastingRecipe register() {
            if (!validate()) return null;
            CastingRecipe recipe = new CastingRecipe(output.get(0), cast != null ? MeltingRecipeBuilder.recipeMatchFromIngredient(cast)
                                                                                 : null, fluidInput.get(0), time, consumesCast, false);
            add(recipe);
            return recipe;
        }
    }
}
