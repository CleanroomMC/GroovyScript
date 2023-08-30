package com.cleanroommc.groovyscript.compat.inworldcrafting;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.inworldcrafting.jei.BurningRecipeCategory;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.Closure;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Burning extends VirtualizedRegistry<Burning.BurningRecipe> {

    private static final Map<EntityItem, BurningRecipe> runningRecipes = new Object2ObjectOpenHashMap<>();

    private final List<BurningRecipe> burningRecipes = new ArrayList<>();

    @Optional.Method(modid = "jei")
    @GroovyBlacklist
    public List<BurningRecipeCategory.RecipeWrapper> getRecipeWrappers() {
        return this.burningRecipes.stream().map(BurningRecipeCategory.RecipeWrapper::new).collect(Collectors.toList());
    }

    @Override
    public void onReload() {
        this.burningRecipes.addAll(getBackupRecipes());
        getScriptedRecipes().forEach(this.burningRecipes::remove);
    }

    @Override
    public void afterScriptLoad() {
        super.afterScriptLoad();
        this.burningRecipes.sort(Comparator.comparingInt(BurningRecipe::getTicks));
    }

    public void add(BurningRecipe burningRecipe) {
        this.burningRecipes.add(burningRecipe);
        addScripted(burningRecipe);
    }

    public boolean remove(BurningRecipe burningRecipe) {
        if (this.burningRecipes.remove(burningRecipe)) {
            addBackup(burningRecipe);
            return true;
        }
        return false;
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public static class BurningRecipe {

        private final IIngredient input;
        private final ItemStack output;
        private final int ticks;
        private final Closure<Boolean> startCondition;

        public BurningRecipe(IIngredient input, ItemStack output, int ticks, Closure<Boolean> startCondition) {
            this.input = input;
            this.output = output;
            this.ticks = ticks;
            this.startCondition = startCondition;
        }

        public IIngredient getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public int getTicks() {
            return ticks;
        }

        public boolean isValidInput(EntityItem entityItem, ItemStack itemStack) {
            return this.input.test(itemStack) && (this.startCondition == null || ClosureHelper.call(true, this.startCondition, entityItem));
        }
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BurningRecipe> {

        private int ticks = 40;
        private Closure<Boolean> startCondition;

        public RecipeBuilder ticks(int ticks) {
            this.ticks = ticks;
            return this;
        }

        public RecipeBuilder startCondition(Closure<Boolean> startCondition) {
            this.startCondition = startCondition;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding in world burning recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (this.ticks < 0) {
                GroovyLog.get().warn("Burning recipe chance should be greater than 0.");
                this.ticks = 40;
            }
        }

        @Override
        public @Nullable Burning.BurningRecipe register() {
            if (!validate()) return null;
            BurningRecipe burningRecipe = new BurningRecipe(this.input.get(0), this.output.get(0), this.ticks, this.startCondition);
            VanillaModule.inWorldCrafting.burning.add(burningRecipe);
            return burningRecipe;
        }
    }

    @GroovyBlacklist
    public BurningRecipe findRecipe(EntityItem entityItem) {
        BurningRecipe burningRecipe = runningRecipes.get(entityItem);
        if (burningRecipe != null) return burningRecipe;
        ItemStack itemStack = entityItem.getItem();
        for (BurningRecipe burningRecipe1 : this.burningRecipes) {
            if (burningRecipe1.isValidInput(entityItem, itemStack)) {
                runningRecipes.put(entityItem, burningRecipe1);
                return burningRecipe1;
            }
        }
        return null;
    }

    @GroovyBlacklist
    public void updateRecipeProgress(EntityItem entityItem) {
        BurningRecipe burningRecipe = findRecipe(entityItem);
        if (burningRecipe == null) return;
        int prog = entityItem.getEntityData().getInteger("burn_time") + 1;
        entityItem.getEntityData().setInteger("burn_time", prog);
        entityItem.setEntityInvulnerable(true);
        if (prog >= burningRecipe.ticks) {
            ItemStack newStack = burningRecipe.output.copy();
            newStack.setCount(entityItem.getItem().getCount());
            entityItem.setItem(newStack);
            removeBurningItem(entityItem);
        }
    }

    @GroovyBlacklist
    public static boolean removeBurningItem(EntityItem entityItem) {
        entityItem.getEntityData().removeTag("burn_item");
        return runningRecipes.remove(entityItem) != null;
    }

    public static boolean isRunningRecipe(EntityItem entityItem) {
        return runningRecipes.containsKey(entityItem) && entityItem.getEntityData().getInteger("burn_time") > 1;
    }
}
