package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.RunicShearRecipe;
import epicsquid.roots.recipe.transmutation.BlockStatePredicate;
import epicsquid.roots.recipe.transmutation.StatePredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.getRunicShearRecipes;

public class RunicShearBlock extends VirtualizedRegistry<Pair<ResourceLocation, RunicShearRecipe>> {

    public RunicShearBlock() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> getRunicShearRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> getRunicShearRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(String name, RunicShearRecipe recipe) {
        add(new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name), recipe);
    }

    public void add(ResourceLocation name, RunicShearRecipe recipe) {
        getRunicShearRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(RunicShearRecipe recipe) {
        for (Map.Entry<ResourceLocation, RunicShearRecipe> entry : getRunicShearRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    public boolean removeByName(ResourceLocation name) {
        RunicShearRecipe recipe = getRunicShearRecipes().get(name);
        if (recipe == null) return false;
        getRunicShearRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByState(IBlockState state) {
        for (Map.Entry<ResourceLocation, RunicShearRecipe> x : getRunicShearRecipes().entrySet()) {
            if (x.getValue().matches(state)) {
                getRunicShearRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, RunicShearRecipe> x : getRunicShearRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getDrop(), output)) {
                getRunicShearRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public void removeAll() {
        getRunicShearRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getRunicShearRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, RunicShearRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getRunicShearRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<RunicShearRecipe> {

        private ItemStack displayItem;
        private BlockStatePredicate state;
        private IBlockState replacementState;
        private ResourceLocation name;

        public RecipeBuilder displayItem(ItemStack displayItem) {
            this.displayItem = displayItem;
            return this;
        }

        public RecipeBuilder state(IBlockState state) {
            this.state = new StatePredicate(state);
            return this;
        }

        public RecipeBuilder state(BlockStatePredicate state) {
            this.state = state;
            return this;
        }

        public RecipeBuilder replacementState(IBlockState replacementState) {
            this.replacementState = replacementState;
            return this;
        }

        public RecipeBuilder name(String name) {
            this.name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name);
            return this;
        }

        public RecipeBuilder name(ResourceLocation name) {
            this.name = name;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Runic Shear Block recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(state == null, "state must be defined");
            msg.add(replacementState == null, "replacementState must be defined");
            if (displayItem == null) {
                displayItem = state.matchingItems().get(0);
            }
            if (name == null) {
                name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), RecipeName.generate("groovyscript_runic_shear_block_"));
            }
        }

        @Override
        public @Nullable RunicShearRecipe register() {
            if (!validate()) return null;
            RunicShearRecipe recipe = new RunicShearRecipe(name, state, replacementState, output.get(0), displayItem);
            ModSupport.ROOTS.get().runicShearBlock.add(name, recipe);
            return recipe;
        }
    }
}
