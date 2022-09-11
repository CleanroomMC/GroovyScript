package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.core.mixin.ic2.ClassicCanningMachineRegistryAccessor;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.ItemsIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.ICannerRegistry;
import ic2.api.recipe.IRecipeInput;
import ic2.core.util.helpers.ItemWithMeta;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import java.util.*;

public class Canner extends VirtualizedRegistry<Canner.CanningRecipe> {

    private static Map<Integer, List<ItemWithMeta>> idToItems;

    public Canner() {
        super("Canner", "canner");
        idToItems = ((ClassicCanningMachineRegistryAccessor) ClassicRecipes.canningMachine).getIdToItems();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }
    
    public CanningRecipe addCanning(ItemStack output, IIngredient input, ItemStack container) {
        CanningRecipe recipe = new CanningRecipe(RecipeType.CANNING_RECIPE).setOutput(output).setInput(input).setContainer(container);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public CanningRecipe registerItemEffect(int id, IIngredient input) {
        CanningRecipe recipe = new CanningRecipe(RecipeType.ITEM_EFFECT).setInt(id).setInput(input);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public CanningRecipe registerFuelValue(IIngredient stack, int value) {
        CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInt(value).setInput(stack);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public CanningRecipe registerFuelValue(IIngredient stack, int value, float value1) {
        CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInt(value).setInput(stack).setFloat(value1);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public void removeFuelValue(IIngredient fuel) {
        if (IngredientHelper.isEmpty(fuel)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canner recipe")
                    .add("fuel must not be empty")
                    .error()
                    .post();
            return;
        }
        ICannerRegistry.FuelInfo info = ClassicRecipes.canningMachine.getFuelInfo(fuel.getMatchingStacks()[0]);
        if (info == null) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canner recipe")
                    .add("no recipes found for %s", fuel)
                    .error()
                    .post();
            return;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInput(fuel).setInt(info.getAmount()).setFloat(info.getMultiplier());
        remove(recipe);
        addBackup(recipe);
    }

    public void removeAllFuelValue() {
        for (ICannerRegistry.FuelInfo info : ClassicRecipes.canningMachine.getAllFuelTypes()) {
            CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInput((IIngredient) (Object) info.getItem()).setInt(info.getAmount()).setFloat(info.getMultiplier());
            remove(recipe);
            addBackup(recipe);
        }
    }

    public void removeItemEffect(int id) {
        if (id < 0 || id >= idToItems.size()) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canner Item Effect recipe")
                    .add("id must be between 0-%s", idToItems.size())
                    .error()
                    .post();
            return;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.ITEM_EFFECT).setInput(new ItemsIngredient(asItemStackList(idToItems.get(id)))).setInt(id);
        ClassicRecipes.canningMachine.deleteEffectID(id, true);
        addBackup(recipe);
    }

    public void removeAllItemEffect() {
        for (int i = 0; i < ClassicRecipes.canningMachine.getEffectMap().size(); i++) {
            CanningRecipe recipe = new CanningRecipe(RecipeType.ITEM_EFFECT).setInput(new ItemsIngredient(asItemStackList(idToItems.get(i)))).setInt(i);
            remove(recipe);
            addBackup(recipe);
        }
    }

    public boolean removeCanning(ItemStack container) {
        if (IngredientHelper.isEmpty(container)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canning Machine recipe")
                    .add("container must not be empty")
                    .error()
                    .post();
            return false;
        }
        for (Map.Entry<ItemStack, List<Tuple<IRecipeInput, ItemStack>>> entry : ClassicRecipes.canningMachine.getCanningMap().entrySet()) {
            if (ItemStack.areItemStacksEqual(entry.getKey(), container)) {
                for (Tuple<IRecipeInput, ItemStack> tuple : entry.getValue()) {
                    CanningRecipe recipe = new CanningRecipe(RecipeType.CANNING_RECIPE).setContainer(container).setInput(new ItemsIngredient(tuple.getFirst().getInputs())).setOutput(tuple.getSecond());
                    addBackup(recipe);
                }
                ClassicRecipes.canningMachine.removeCanningRecipe(container);
                return true;
            }
        }
        GroovyLog.msg("Error removing Industrialcraft 2 Canning Machine recipe")
                .add("no recipes found for %s", container)
                .error()
                .post();
        return false;
    }

    public void removeCanningByInputs(ItemStack input, ItemStack container) {
        if (GroovyLog.msg("Error removing Industrialcraft 2 Canning Machine recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(IngredientHelper.isEmpty(container), () -> "container must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (Map.Entry<ItemStack, List<Tuple<IRecipeInput, ItemStack>>> entry : ClassicRecipes.canningMachine.getCanningMap().entrySet()) {
            if (ItemStack.areItemStacksEqual(entry.getKey(), container)) {
                for (Tuple<IRecipeInput, ItemStack> tuple : entry.getValue()) {
                    if (tuple.getFirst().matches(input)) {
                        CanningRecipe recipe = new CanningRecipe(RecipeType.CANNING_RECIPE).setContainer(container).setInput(new ItemsIngredient(tuple.getFirst().getInputs())).setOutput(tuple.getSecond());
                        remove(recipe);
                        addBackup(recipe);
                    }
                }
            }
        }
    }

    public void removeCanningByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canning Machine recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (Map.Entry<ItemStack, List<Tuple<IRecipeInput, ItemStack>>> entry : ClassicRecipes.canningMachine.getCanningMap().entrySet()) {
            for (Tuple<IRecipeInput, ItemStack> tuple : entry.getValue()) {
                if (ItemStack.areItemStacksEqual(tuple.getSecond(), output)) {
                    CanningRecipe recipe = new CanningRecipe(RecipeType.CANNING_RECIPE).setContainer(entry.getKey()).setInput(new ItemsIngredient(tuple.getFirst().getInputs())).setOutput(tuple.getSecond());
                    remove(recipe);
                    addBackup(recipe);
                }
            }
        }
    }

    public void removeAllCanning() {
        for (Map.Entry<ItemStack, List<Tuple<IRecipeInput, ItemStack>>> entry : ClassicRecipes.canningMachine.getCanningMap().entrySet()) {
            for (Tuple<IRecipeInput, ItemStack> tuple : entry.getValue()) {
                CanningRecipe recipe = new CanningRecipe(RecipeType.CANNING_RECIPE).setContainer(entry.getKey().copy()).setInput(new ItemsIngredient(new ArrayList<>(tuple.getFirst().getInputs()))).setOutput(tuple.getSecond().copy());
                addBackup(recipe);
            }
            ClassicRecipes.canningMachine.removeCanningRecipe(entry.getKey());
        }
    }

    private void add(CanningRecipe recipe) {
        switch (recipe.type) {
            case CANNING_RECIPE:
                ClassicRecipes.canningMachine.registerCannerItem(recipe.container, new RecipeInput(recipe.input), recipe.output);
            case ITEM_EFFECT:
                ClassicRecipes.canningMachine.registerItemsForEffect(recipe.intValue, recipe.input.getMatchingStacks());
            case FUEL_VALUE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.canningMachine.registerFuelValue(stack, recipe.intValue);
                    if (recipe.floatValue > 0) ClassicRecipes.canningMachine.registerFuelMultiplier(stack, recipe.floatValue);
                }
        }
    }

    private void remove(CanningRecipe recipe) {
        switch (recipe.type) {
            case CANNING_RECIPE:
                ClassicRecipes.canningMachine.removeCanningRecipe(recipe.container, recipe.input.getMatchingStacks()[0]);
            case ITEM_EFFECT:
                ClassicRecipes.canningMachine.deleteEffectID(recipe.intValue, true);
            case FUEL_VALUE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.canningMachine.deleteItemFuel(stack);
                }
        }
    }

    private List<ItemStack> asItemStackList(Collection<ItemWithMeta> list) {
        List<ItemStack> l = new ArrayList<>();
        for (ItemWithMeta item : list) {
            l.add(item.toStack());
        }
        return l;
    }

    public static class CanningRecipe {
        public RecipeType type;

        public IIngredient input;
        public ItemStack output;

        public int intValue;
        public float floatValue;

        public ItemStack container;

        public CanningRecipe(RecipeType type) {
            this.type = type;
        }

        public CanningRecipe setInt(int value) {
            this.intValue = value;
            return this;
        }

        public CanningRecipe setFloat(float value) {
            this.floatValue = value;
            return this;
        }

        public CanningRecipe setInput(IIngredient input) {
            this.input = input;
            return this;
        }

        public CanningRecipe setOutput(ItemStack output) {
            this.output = output;
            return this;
        }

        public CanningRecipe setContainer(ItemStack container) {
            this.container = container;
            return this;
        }
    }

    private enum RecipeType {
        ITEM_EFFECT,
        FUEL_VALUE,
        CANNING_RECIPE
    }
}
