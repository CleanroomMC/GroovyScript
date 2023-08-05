package com.cleanroommc.groovyscript.compat.mods.ic2.classic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.core.mixin.ic2.ClassicCanningMachineRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.ICannerRegistry;
import ic2.api.classic.recipe.machine.IFoodCanEffect;
import ic2.api.item.ICustomDamageItem;
import ic2.api.recipe.IRecipeInput;
import ic2.core.util.helpers.ItemWithMeta;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Canner extends VirtualizedRegistry<Canner.CanningRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    public CanningRecipe addCanning(ItemStack output, IIngredient input, ItemStack container) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Canner Canning recipe")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.CANNING_RECIPE).setOutput(output).setInput(input).setContainer(container);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public CanningRecipe registerItemEffect(int id, IIngredient input) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Canner Item Effect recipe")
                .add(id < 0, () -> "id must not be negative")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.ITEM_EFFECT).setInt(id).setInput(input);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public CanningRecipe registerFuelValue(IIngredient ingredient, int value) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Canner Fuel Value")
                .add(IngredientHelper.isEmpty(ingredient), () -> "ingredient must not be empty")
                .add(value <= 0, () -> "value must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInt(value).setInput(ingredient);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public CanningRecipe registerFuelValue(IIngredient ingredient, int value, float value1) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Canner Fuel Value")
                .add(IngredientHelper.isEmpty(ingredient), () -> "ingredient must not be empty")
                .add(value <= 0, () -> "value must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInt(value).setInput(ingredient).setFloat(value1);
        add(recipe);
        addScripted(recipe);
        return recipe;
    }

    public SimpleObjectStream<Map.Entry<ItemStack, List<Tuple<IRecipeInput, ItemStack>>>> streamCanningRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.canningMachine.getCanningMap().entrySet()).setRemover(r -> this.removeCanning(r.getKey()));
    }

    public SimpleObjectStream<ICannerRegistry.FuelInfo> streamFuelValues() {
        return new SimpleObjectStream<>(ClassicRecipes.canningMachine.getAllFuelTypes()).setRemover(r -> this.removeFuelValue(IngredientHelper.toIIngredient(r.getItem())));
    }

    public SimpleObjectStream<Map.Entry<Integer, IFoodCanEffect>> streamItemEffects() {
        return new SimpleObjectStream<>(ClassicRecipes.canningMachine.getEffectMap().entrySet()).setRemover(r -> this.removeItemEffect(r.getKey()));
    }

    public SimpleObjectStream<Map.Entry<ICustomDamageItem, List<Tuple<Integer, Tuple<IRecipeInput, Integer>>>>> streamRepairRecipes() {
        return new SimpleObjectStream<>(ClassicRecipes.canningMachine.getRepairMap().entrySet()).setRemover(entry -> {
            for (Tuple<Integer, Tuple<IRecipeInput, Integer>> tuple : entry.getValue()) {
                CanningRecipe recipe = new CanningRecipe(RecipeType.REPAIR)
                        .setDamageItem(entry.getKey())
                        .setMeta(tuple.getFirst())
                        .setInput(new ItemsIngredient(tuple.getSecond().getFirst().getInputs()))
                        .setInt(tuple.getSecond().getSecond());
                remove(recipe);
                addBackup(recipe);
            }
            return true;
        });
    }

    public boolean removeFuelValue(IIngredient fuel) {
        if (IngredientHelper.isEmpty(fuel)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canner recipe")
                    .add("fuel must not be empty")
                    .error()
                    .post();
            return false;
        }
        ICannerRegistry.FuelInfo info = ClassicRecipes.canningMachine.getFuelInfo(fuel.getMatchingStacks()[0]);
        if (info == null) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canner recipe")
                    .add("no recipes found for {}", fuel)
                    .error()
                    .post();
            return false;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInput(fuel).setInt(info.getAmount()).setFloat(info.getMultiplier());
        remove(recipe);
        addBackup(recipe);
        return true;
    }

    public void removeAllFuelValue() {
        for (ICannerRegistry.FuelInfo info : ClassicRecipes.canningMachine.getAllFuelTypes()) {
            CanningRecipe recipe = new CanningRecipe(RecipeType.FUEL_VALUE).setInput((IIngredient) (Object) info.getItem()).setInt(info.getAmount()).setFloat(info.getMultiplier());
            remove(recipe);
            addBackup(recipe);
        }
    }

    public boolean removeItemEffect(int id) {
        Map<Integer, List<ItemWithMeta>> idToItems = ((ClassicCanningMachineRegistryAccessor) ClassicRecipes.canningMachine).getIdToItems();
        if (id < 0 || id >= idToItems.size()) {
            GroovyLog.msg("Error removing Industrialcraft 2 Canner Item Effect recipe")
                    .add("id must be between 0-%d", idToItems.size())
                    .error()
                    .post();
            return false;
        }
        CanningRecipe recipe = new CanningRecipe(RecipeType.ITEM_EFFECT).setInput(new ItemsIngredient(asItemStackList(idToItems.get(id)))).setInt(id);
        ClassicRecipes.canningMachine.deleteEffectID(id, true);
        addBackup(recipe);
        return true;
    }

    public void removeAllItemEffect() {
        Map<Integer, List<ItemWithMeta>> idToItems = ((ClassicCanningMachineRegistryAccessor) ClassicRecipes.canningMachine).getIdToItems();
        for (int i = 0; i < ClassicRecipes.canningMachine.getEffectMap().size(); i++) {
            CanningRecipe recipe = new CanningRecipe(RecipeType.ITEM_EFFECT).setInput(new ItemsIngredient(asItemStackList(idToItems.get(i)))).setInt(i);
            remove(recipe);
            addBackup(recipe);
        }
    }

    public boolean removeAllRepair() {
        for (Map.Entry<ICustomDamageItem, List<Tuple<Integer, Tuple<IRecipeInput, Integer>>>> entry : ClassicRecipes.canningMachine.getRepairMap().entrySet()) {
            for (Tuple<Integer, Tuple<IRecipeInput, Integer>> tuple : entry.getValue()) {
                CanningRecipe recipe = new CanningRecipe(RecipeType.REPAIR).setDamageItem(entry.getKey()).setMeta(tuple.getFirst()).setInput(new ItemsIngredient(tuple.getSecond().getFirst().getInputs())).setInt(tuple.getSecond().getSecond());
                remove(recipe);
                addBackup(recipe);
            }
        }
        return true;
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
                .add("no recipes found for {}", container)
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
                break;
            case ITEM_EFFECT:
                ClassicRecipes.canningMachine.registerItemsForEffect(recipe.intValue, recipe.input.getMatchingStacks());
                break;
            case FUEL_VALUE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.canningMachine.registerFuelValue(stack, recipe.intValue);
                    if (recipe.floatValue > 0)
                        ClassicRecipes.canningMachine.registerFuelMultiplier(stack, recipe.floatValue);
                }
                break;
            case REPAIR:
                ClassicRecipes.canningMachine.addRepairRecipe(recipe.damageItem, recipe.meta, new RecipeInput(recipe.input), recipe.intValue);
                break;
        }
    }

    private void remove(CanningRecipe recipe) {
        switch (recipe.type) {
            case CANNING_RECIPE:
                ClassicRecipes.canningMachine.removeCanningRecipe(recipe.container, recipe.input.getMatchingStacks()[0]);
                break;
            case ITEM_EFFECT:
                ClassicRecipes.canningMachine.deleteEffectID(recipe.intValue, true);
                break;
            case FUEL_VALUE:
                for (ItemStack stack : recipe.input.getMatchingStacks()) {
                    ClassicRecipes.canningMachine.deleteItemFuel(stack);
                }
                break;
            case REPAIR:
                ClassicRecipes.canningMachine.removeRepairItem(recipe.damageItem, recipe.meta);
                break;
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

        public int meta;
        public ICustomDamageItem damageItem;

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

        public CanningRecipe setMeta(int meta) {
            this.meta = meta;
            return this;
        }

        public CanningRecipe setDamageItem(ICustomDamageItem item) {
            this.damageItem = item;
            return this;
        }
    }

    private enum RecipeType {
        ITEM_EFFECT,
        FUEL_VALUE,
        CANNING_RECIPE,
        REPAIR
    }
}
