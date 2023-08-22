package com.cleanroommc.groovyscript.compat.mods.extrautilities2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.api.machine.MachineSlotFluid;
import com.rwtema.extrautils2.api.machine.MachineSlotItem;
import com.rwtema.extrautils2.api.machine.XUMachineCrusher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Crusher extends VirtualizedRegistry<IMachineRecipe> {

    public Crusher() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(XUMachineCrusher.INSTANCE.recipes_registry::removeRecipe);
        restoreFromBackup().forEach(XUMachineCrusher.INSTANCE.recipes_registry::addRecipe);
    }

    public IMachineRecipe add(IMachineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            XUMachineCrusher.INSTANCE.recipes_registry.addRecipe(recipe);
        }
        return recipe;
    }

    public boolean remove(IMachineRecipe recipe) {
        if (XUMachineCrusher.INSTANCE.recipes_registry.removeRecipe(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(ItemStack input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineCrusher.INSTANCE.recipes_registry) {
            for (Pair<Map<MachineSlotItem, List<ItemStack>>, Map<MachineSlotFluid, List<FluidStack>>> mapMapPair : recipe.getJEIInputItemExamples()) {
                for (ItemStack stack : mapMapPair.getKey().get(XUMachineCrusher.INPUT)) {
                    if (input.isItemEqual(stack)) {
                        agony.add(recipe);
                    }
                }
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineCrusher.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }

    public void removeByInput(IIngredient input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineCrusher.INSTANCE.recipes_registry) {
            for (Pair<Map<MachineSlotItem, List<ItemStack>>, Map<MachineSlotFluid, List<FluidStack>>> mapMapPair : recipe.getJEIInputItemExamples()) {
                for (ItemStack stack : mapMapPair.getKey().get(XUMachineCrusher.INPUT)) {
                    if (input.test(stack)) {
                        agony.add(recipe);
                    }
                }
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineCrusher.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }

    public void removeByOutput(ItemStack input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineCrusher.INSTANCE.recipes_registry) {
            for (Pair<Map<MachineSlotItem, List<ItemStack>>, Map<MachineSlotFluid, List<FluidStack>>> mapMapPair : recipe.getJEIInputItemExamples()) {
                for (ItemStack stack : mapMapPair.getKey().get(XUMachineCrusher.OUTPUT)) {
                    if (input.isItemEqual(stack)) {
                        agony.add(recipe);
                    }
                }
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineCrusher.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }

    public SimpleObjectStream<IMachineRecipe> streamRecipes() {
        List<IMachineRecipe> list = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineCrusher.INSTANCE.recipes_registry) {
            list.add(recipe);
        }
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    public void removeAll() {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineCrusher.INSTANCE.recipes_registry) {
            agony.add(recipe);
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineCrusher.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IMachineRecipe> {

        private int energy;
        private int time;
        private int chance;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extra Utilities 2 Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(energy < 0, () -> "energy must not be negative");
            msg.add(time <= 0, () -> "time must not be less than or equal to 0");
            msg.add(chance < 0, () -> "chance must not be negative");
        }

        @Nullable
        @Override
        public IMachineRecipe register() {
            if (!validate()) return null;
            com.rwtema.extrautils2.api.machine.RecipeBuilder builder = com.rwtema.extrautils2.api.machine.RecipeBuilder.newbuilder(XUMachineCrusher.INSTANCE);
            builder.setItemInput(XUMachineCrusher.INPUT, Arrays.asList(input.get(0).getMatchingStacks()), input.get(0).getAmount());
            builder.setItemOutput(XUMachineCrusher.OUTPUT, output.get(0));
            if (!IngredientHelper.isEmpty(output.getOrEmpty(1))) {
                builder.setItemOutput(XUMachineCrusher.OUTPUT_SECONDARY, output.get(1));
                builder.setProbability(XUMachineCrusher.OUTPUT_SECONDARY, chance);
            }
            builder.setEnergy(energy);
            builder.setProcessingTime(time);

            IMachineRecipe recipe = builder.build();

            ModSupport.EXTRA_UTILITIES_2.get().crusher.add(recipe);
            return recipe;
        }
    }
}
