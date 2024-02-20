package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.api.machine.MachineSlotFluid;
import com.rwtema.extrautils2.api.machine.MachineSlotItem;
import com.rwtema.extrautils2.api.machine.XUMachineEnchanter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Enchanter extends VirtualizedRegistry<IMachineRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(XUMachineEnchanter.INSTANCE.recipes_registry::removeRecipe);
        restoreFromBackup().forEach(XUMachineEnchanter.INSTANCE.recipes_registry::addRecipe);
    }

    public IMachineRecipe add(IMachineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.addRecipe(recipe);
        }
        return recipe;
    }

    public boolean remove(IMachineRecipe recipe) {
        if (XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(IIngredient input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            for (Pair<Map<MachineSlotItem, List<ItemStack>>, Map<MachineSlotFluid, List<FluidStack>>> mapMapPair : recipe.getJEIInputItemExamples()) {
                for (ItemStack stack : mapMapPair.getKey().get(XUMachineEnchanter.INPUT)) {
                    if (input.test(stack)) {
                        agony.add(recipe);
                    }
                }
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }

    public void removeByOutput(ItemStack input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            for (Pair<Map<MachineSlotItem, List<ItemStack>>, Map<MachineSlotFluid, List<FluidStack>>> mapMapPair : recipe.getJEIInputItemExamples()) {
                for (ItemStack stack : mapMapPair.getKey().get(XUMachineEnchanter.OUTPUT)) {
                    if (input.isItemEqual(stack)) {
                        agony.add(recipe);
                    }
                }
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }


    public SimpleObjectStream<IMachineRecipe> streamRecipes() {
        List<IMachineRecipe> list = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            list.add(recipe);
        }
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    public void removeAll() {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineEnchanter.INSTANCE.recipes_registry) {
            agony.add(recipe);
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            XUMachineEnchanter.INSTANCE.recipes_registry.removeRecipe(recipe);
        }
    }


    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IMachineRecipe> {

        private int energy;
        private int time;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extra Utilities 2 Enchanter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            msg.add(energy < 0, () -> "energy must not be negative");
            msg.add(time <= 0, () -> "time must not be less than or equal to 0");
        }

        @Override
        public IMachineRecipe register() {
            if (!validate()) return null;
            com.rwtema.extrautils2.api.machine.RecipeBuilder builder = com.rwtema.extrautils2.api.machine.RecipeBuilder.newbuilder(XUMachineEnchanter.INSTANCE);
            builder.setItemInput(XUMachineEnchanter.INPUT, Arrays.asList(input.get(0).getMatchingStacks()), input.get(0).getAmount());
            builder.setItemOutput(XUMachineEnchanter.OUTPUT, output.get(0));
            if (input.get(1).getAmount() > 0) {
                builder.setItemInput(XUMachineEnchanter.INPUT_LAPIS, Arrays.asList(input.get(1).getMatchingStacks()), input.get(1).getAmount());
            }
            builder.setEnergy(energy);
            builder.setProcessingTime(time);

            IMachineRecipe recipe = builder.build();

            ModSupport.EXTRA_UTILITIES_2.get().enchanter.add(recipe);
            return recipe;
        }
    }
}
