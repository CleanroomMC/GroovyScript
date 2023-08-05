package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe.MeltingRecipeBuilder;
import com.cleanroommc.groovyscript.core.mixin.tconstruct.TinkerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;

public class Casting {
    public final Table table = new Table();
    public final Basin basin = new Basin();

    public static class Table extends VirtualizedRegistry<ICastingRecipe> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
        }

        @Override
        @GroovyBlacklist
        public void onReload() {
            removeScripted().forEach(TinkerRegistryAccessor.getTableCastRegistry()::remove);
            restoreFromBackup().forEach(TinkerRegistryAccessor.getTableCastRegistry()::add);
        }

        public void add(ICastingRecipe recipe) {
            if (recipe == null) return;
            addScripted(recipe);
            TinkerRegistryAccessor.getTableCastRegistry().add(recipe);
        }

        public boolean remove(ICastingRecipe recipe) {
            if (recipe == null) return false;
            addBackup(recipe);
            TinkerRegistryAccessor.getTableCastRegistry().remove(recipe);
            return true;
        }

        public boolean removeByOutput(ItemStack output){
            if (TinkerRegistryAccessor.getTableCastRegistry().removeIf(recipe -> {
                boolean found = recipe.getResult(ItemStack.EMPTY, FluidRegistry.WATER).isItemEqual(output);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Construct Casting Table recipe")
                    .add("could not find recipe with output {}", output)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByInput(FluidStack input) {
            if (TinkerRegistryAccessor.getTableCastRegistry().removeIf(recipe -> {
                boolean found = recipe.getFluid(ItemStack.EMPTY, input.getFluid()).isFluidEqual(input);
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Construct Casting Table recipe")
                    .add("could not find recipe with input {}", input)
                    .error()
                    .post();
            return false;
        }

        public boolean removeByCast(IIngredient cast) {
            if (TinkerRegistryAccessor.getTableCastRegistry().removeIf(recipe -> {
                boolean found = recipe.matches(cast.getMatchingStacks()[0], recipe.getFluid(cast.getMatchingStacks()[0], FluidRegistry.WATER).getFluid());
                if (found) addBackup(recipe);
                return found;
            })) return true;

            GroovyLog.msg("Error removing Tinkers Construct Casting Table recipe")
                    .add("could not find recipe with cast {}", cast)
                    .error()
                    .post();
            return false;
        }

        public void removeAll() {
            TinkerRegistryAccessor.getTableCastRegistry().forEach(this::addBackup);
            TinkerRegistryAccessor.getTableCastRegistry().forEach(TinkerRegistryAccessor.getTableCastRegistry()::remove);
        }

        public SimpleObjectStream<ICastingRecipe> streamRecipes() {
            return new SimpleObjectStream<>(TinkerRegistryAccessor.getTableCastRegistry()).setRemover(this::remove);
        }

        public class RecipeBuilder extends AbstractRecipeBuilder<ICastingRecipe> {

            private IIngredient cast;
            private int time = 200;
            private boolean consumesCast = false;

            public RecipeBuilder coolingTime(int time) {
                this.time = Math.max(time, 1);
                return this;
            }

            public RecipeBuilder consumesCast(boolean consumesCast) {
                this.consumesCast = consumesCast;
                return this;
            }

            public RecipeBuilder consumesCast() {
                return consumesCast(!consumesCast);
            }

            public RecipeBuilder cast(IIngredient ingredient) {
                this.cast = ingredient;
                return this;
            }

            @Override
            public String getErrorMsg() {
                return "Error adding Tinkers Construct Casting Table recipe";
            }

            @Override
            public void validate(GroovyLog.Msg msg) {
                validateFluids(msg, 1, 1, 0, 0);
                validateItems(msg, 0, 0, 1, 1);
            }

            @Override
            public @Nullable ICastingRecipe register() {
                if (!validate()) return null;
                CastingRecipe recipe = new CastingRecipe(output.get(0), cast != null ? MeltingRecipeBuilder.recipeMatchFromIngredient(cast) : null, fluidInput.get(0), time, consumesCast, false);
                add(recipe);
                return recipe;
            }
        }
    }

    public static class Basin extends VirtualizedRegistry<ICastingRecipe> {

        public RecipeBuilder recipeBuilder() {
            return new RecipeBuilder();
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

        public boolean removeByOutput(ItemStack output){
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

        public void removeAll() {
            TinkerRegistryAccessor.getBasinCastRegistry().forEach(this::addBackup);
            TinkerRegistryAccessor.getBasinCastRegistry().forEach(TinkerRegistryAccessor.getBasinCastRegistry()::remove);
        }

        public SimpleObjectStream<ICastingRecipe> streamRecipes() {
            return new SimpleObjectStream<>(TinkerRegistryAccessor.getBasinCastRegistry()).setRemover(this::remove);
        }

        public class RecipeBuilder extends AbstractRecipeBuilder<ICastingRecipe> {

            private IIngredient cast;
            private int time = 200;
            private boolean consumesCast = false;

            public RecipeBuilder coolingTime(int time) {
                this.time = Math.max(time, 1);
                return this;
            }

            public RecipeBuilder consumesCast(boolean consumesCast) {
                this.consumesCast = consumesCast;
                return this;
            }

            public RecipeBuilder consumesCast() {
                return consumesCast(!consumesCast);
            }

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
            public @Nullable ICastingRecipe register() {
                if (!validate()) return null;
                CastingRecipe recipe = new CastingRecipe(output.get(0), cast != null ? MeltingRecipeBuilder.recipeMatchFromIngredient(cast) : null, fluidInput.get(0), time, consumesCast, false);
                add(recipe);
                return recipe;
            }
        }
    }
}
