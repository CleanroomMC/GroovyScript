package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.RecipeStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class ArcFurnace extends VirtualizedRegistry<ArcFurnaceRecipe> {
    public ArcFurnace() {
        super("ArcFurnace", "arcfurnace", "arc_furnace");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> ArcFurnaceRecipe.recipeList.removeIf(recipe -> recipe == r));
        ArcFurnaceRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(ArcFurnaceRecipe recipe) {
        if (recipe != null) {
            ArcFurnaceRecipe.recipeList.add(recipe);
            addScripted(recipe);
        }
    }

    public ArcFurnaceRecipe add(ItemStack output, Object input, @Nonnull ItemStack slag, int time, int energyPerTick, Object... additives) {
        ArcFurnaceRecipe recipe = create(output, input, slag, time, energyPerTick, additives);
        addScripted(recipe);
        return recipe;
    }

    public void remove(ArcFurnaceRecipe recipe) {
        if (ArcFurnaceRecipe.recipeList.removeIf(r -> r == recipe)) addBackup(recipe);
    }

    public void removeByOutput(ItemStack output) {
        List<ArcFurnaceRecipe> list = ArcFurnaceRecipe.removeRecipes(output);
        if (list.size() > 0) {
            list.forEach(this::addBackup);
        }
    }

    public void removeByInput(ItemStack... inputAndAdditives) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 1; i < inputAndAdditives.length; i++) list.add(inputAndAdditives[i]);
        ArcFurnaceRecipe recipe = ArcFurnaceRecipe.findRecipe(inputAndAdditives[0], list);
        if (recipe != null) {
            remove(recipe);
        }
    }

    public RecipeStream<ArcFurnaceRecipe> stream() {
        return new RecipeStream<>(ArcFurnaceRecipe.recipeList).setRemover(recipe -> {
            NonNullList<ItemStack> list = NonNullList.create();
            for (IngredientStack additive : recipe.additives) {
                list.add(additive.stack);
            }

            ArcFurnaceRecipe recipe1 = ArcFurnaceRecipe.findRecipe(recipe.input.stack, list);
            if (recipe1 != null) {
                remove(recipe1);
                addBackup(recipe1);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        ArcFurnaceRecipe.recipeList.forEach(this::addBackup);
        ArcFurnaceRecipe.recipeList.clear();
    }

    private static ArcFurnaceRecipe create(ItemStack output, Object input, @Nonnull ItemStack slag, int time, int energyPerTick, Object... additives) {
        if (input instanceof IIngredient) input = ((IIngredient) input).getMatchingStacks();
        for (int i = 0; i < additives.length; i++) {
            Object obj = additives[i];
            if (obj instanceof IIngredient) additives[i] = ((IIngredient) obj).getMatchingStacks();
        }

        return ArcFurnaceRecipe.addRecipe(output, input, slag, time, energyPerTick, additives);
    }

    public static class RecipeBuilder extends TimeRecipeBuilder<ArcFurnaceRecipe> {
        protected int energyPerTick;
        protected ItemStack slag = ItemStack.EMPTY;
        protected Object[] additives = null;

        public RecipeBuilder energyPerTick(int energy) {
            this.energyPerTick = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Arc Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 5, 1, 2);
            validateFluids(msg);
            if (time < 0) time = 200;
            if (energyPerTick < 0) energyPerTick = 100;
            if (output.size() > 1) slag = output.get(1);
            if (input.size() > 1) {
                additives = new Object[input.size()-1];
                for (int i = 1; i < input.size(); i++) {
                    additives[i-1] = input.get(i);
                }
            }
        }

        @Override
        public @Nullable ArcFurnaceRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().arcFurnace.add(output.get(0), input.get(0), slag, time, energyPerTick, additives);
        }
    }
}
