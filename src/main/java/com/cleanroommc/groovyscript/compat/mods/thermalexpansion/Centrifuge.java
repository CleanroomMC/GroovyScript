package com.cleanroommc.groovyscript.compat.mods.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.item.ItemMorb;
import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import cofh.thermalfoundation.init.TFFluids;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.mixin.thermalexpansion.CentrifugeManagerAccessor;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Centrifuge extends VirtualizedRegistry<CentrifugeManager.CentrifugeRecipe> {

    private final List<CentrifugeManager.CentrifugeRecipe> scriptedMobs = new ArrayList<>();
    private final List<CentrifugeManager.CentrifugeRecipe> backupMobs = new ArrayList<>();

    public Centrifuge() {
        super("Centrifuge", "centrifuge");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        Map<ComparableItemStackValidatedNBT, CentrifugeManager.CentrifugeRecipe> map = CentrifugeManagerAccessor.getRecipeMap();
        removeScripted().forEach(recipe -> map.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(r -> map.put(CentrifugeManager.convertInput(r.getInput()), r));
        Map<ComparableItemStackValidatedNBT, CentrifugeManager.CentrifugeRecipe> map1 = CentrifugeManagerAccessor.getRecipeMapMobs();
        scriptedMobs.forEach(recipe -> map1.values().removeIf(r -> r == recipe));
        backupMobs.forEach(r -> map1.put(CentrifugeManager.convertInput(r.getInput()), r));
        scriptedMobs.clear();
        backupMobs.clear();
    }

    public CentrifugeManager.CentrifugeRecipe add(int energy, ItemStack input, List<ItemStack> output, List<Integer> chance, FluidStack fluid) {
        CentrifugeManager.CentrifugeRecipe recipe = CentrifugeManager.addRecipe(energy, input, output, chance, fluid);
        if (recipe != null) {
            addScripted(recipe);
        }
        return recipe;
    }

    public CentrifugeManager.CentrifugeRecipe addMob(int energy, ItemStack input, List<ItemStack> output, List<Integer> chance, FluidStack fluid) {
        CentrifugeManager.CentrifugeRecipe recipe = CentrifugeManager.addRecipeMob(energy, input, output, chance, fluid);
        if (recipe != null) {
            scriptedMobs.add(recipe);
        }
        return recipe;
    }

    public boolean remove(CentrifugeManager.CentrifugeRecipe recipe) {
        if (CentrifugeManagerAccessor.getRecipeMap().values().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public boolean removeMob(CentrifugeManager.CentrifugeRecipe recipe) {
        if (CentrifugeManagerAccessor.getRecipeMapMobs().values().removeIf(r -> r == recipe)) {
            backupMobs.add(recipe);
            return true;
        }
        return false;
    }

    public void removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Thermal Expansion Centrifuge recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        boolean found = false;
        for (ItemStack stack : input.getMatchingStacks()) {
            CentrifugeManager.CentrifugeRecipe recipe = CentrifugeManager.removeRecipe(stack);
            if (recipe != null) {
                found = true;
                addBackup(recipe);
            }
        }
        if (!found) {
            GroovyLog.msg("Error removing Thermal Expansion Centrifuge recipe")
                    .add("could not find recipe for %s", input)
                    .error()
                    .post();
        }
    }

    public void removeMob(String entity) {
        if (entity == null || !ItemMorb.validMobs.contains(entity)) {
            GroovyLog.msg("Error removing Thermal Expansion Centrifuge mob recipe")
                    .add("%s is not a valid mob", entity)
                    .error()
                    .post();
        }
        int oldSize = CentrifugeManagerAccessor.getRecipeMap().size();
        CentrifugeManagerAccessor.getRecipeMap().values().removeIf(recipe -> {
            ItemStack input = recipe.getInput();
            if (input.getItem() instanceof ItemMorb && input.hasTagCompound()) {
                String entity1 = input.getTagCompound().getString("id");
                if (entity.equals(entity1)) {
                    backupMobs.add(recipe);
                    return true;
                }
                return false;
            }
            return false;
        });
        if (CentrifugeManagerAccessor.getRecipeMap().size() == oldSize) {
            GroovyLog.msg("Error removing Thermal Expansion Centrifuge recipe")
                    .add("could not find recipe for entity %s", entity)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<CentrifugeManager.CentrifugeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CentrifugeManagerAccessor.getRecipeMap().values())
                .setRemover(this::remove);
    }

    public SimpleObjectStream<CentrifugeManager.CentrifugeRecipe> streamMobRecipes() {
        return new SimpleObjectStream<>(CentrifugeManagerAccessor.getRecipeMapMobs().values())
                .setRemover(this::removeMob);
    }

    public SimpleObjectStream<CentrifugeManager.CentrifugeRecipe> streamAllRecipes() {
        List<CentrifugeManager.CentrifugeRecipe> allRecipes = new ArrayList<>();
        allRecipes.addAll(CentrifugeManagerAccessor.getRecipeMap().values());
        allRecipes.addAll(CentrifugeManagerAccessor.getRecipeMapMobs().values());
        return new SimpleObjectStream<>(allRecipes, false)
                .setRemover(recipe -> {
                    if (!remove(recipe)) {
                        return removeMob(recipe);
                    }
                    return true;
                });
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<CentrifugeManager.CentrifugeRecipe> {

        private final IntList outputChances = new IntArrayList();
        private String mob = null;
        private int xp = 2;

        public RecipeBuilder mob(String mob) {
            this.mob = mob;
            return this;
        }

        public RecipeBuilder xp(int xp) {
            this.xp = xp;
            return this;
        }

        @Override
        public RecipeBuilder output(ItemStack output) {
            return output(output, 100);
        }

        public RecipeBuilder output(ItemStack output, int chance) {
            outputChances.add(chance);
            return (RecipeBuilder) super.output(output);
        }

        public RecipeBuilder output(ItemStack output, float chance) {
            return output(output, (int) (chance * 100));
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Centrifuge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            if (mob == null) {
                validateItems(msg, 1, 1, 0, 4);
            } else {
                validateItems(msg, 0, 0, 0, 4);
            }
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(mob != null && !ItemMorb.validMobs.contains(mob), () -> mob + " is not a valid mob");
            if (energy <= 0) energy = 4000;
            if (xp < 0) xp = 2;
        }

        @Override
        public @Nullable CentrifugeManager.CentrifugeRecipe register() {
            if (!validate()) return null;
            Centrifuge centrifuge = ModSupport.THERMAL_EXPANSION.get().centrifuge;
            CentrifugeManager.CentrifugeRecipe recipe = null;
            if (mob != null) {
                recipe = centrifuge.addDefaultMobRecipe(mob, output, outputChances, energy, xp);
            } else {
                for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                    FluidStack fluid = fluidOutput.isEmpty() ? null : fluidOutput.get(0);

                    CentrifugeManager.CentrifugeRecipe recipe1 = centrifuge.add(energy, itemStack, output, outputChances, fluid);
                    if (recipe == null) recipe = recipe1;
                }
            }
            return recipe;
        }
    }

    private CentrifugeManager.CentrifugeRecipe addDefaultMobRecipe(String entityId, List<ItemStack> output, List<Integer> chance, int energy, int xp) {
        if (ItemMorb.validMobs.contains(entityId)) {
            ArrayList<ItemStack> outputStandard = new ArrayList<>(output);
            ArrayList<ItemStack> outputReusable = new ArrayList<>(output);
            ArrayList<Integer> chanceStandard = new ArrayList<>(chance);
            ArrayList<Integer> chanceReusable = new ArrayList<>(chance);
            outputStandard.add(ItemHelper.cloneStack(ItemMorb.morbStandard));
            outputReusable.add(ItemHelper.cloneStack(ItemMorb.morbReusable));
            chanceStandard.add(25);
            chanceReusable.add(100);
            CentrifugeManager.CentrifugeRecipe recipe = addMob(energy, ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbStandard), entityId, false), outputStandard, chanceStandard, new FluidStack(TFFluids.fluidExperience, xp * 20));
            addMob(energy, ItemMorb.setTag(ItemHelper.cloneStack(ItemMorb.morbReusable), entityId, false), outputReusable, chanceReusable, new FluidStack(TFFluids.fluidExperience, xp * 20));
            return recipe;
        }
        return null;
    }
}
