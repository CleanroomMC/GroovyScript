package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.FeyCraftingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.*;

public class FeyCrafter extends VirtualizedRegistry<Pair<ResourceLocation, FeyCraftingRecipe>> {

    public FeyCrafter() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> removeFeyCraftingRecipe(pair.getKey()));
        restoreFromBackup().forEach(pair -> addFeyCraftingRecipe(pair.getKey(), pair.getValue()));
    }

    public void add(String name, FeyCraftingRecipe recipe) {
        add(new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name), recipe);
    }

    public void add(ResourceLocation name, FeyCraftingRecipe recipe) {
        addFeyCraftingRecipe(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(FeyCraftingRecipe recipe) {
        for (Map.Entry<ResourceLocation, FeyCraftingRecipe> entry : getFeyCraftingRecipes().entrySet()) {
            if (entry.getValue().matches(recipe.getRecipe())) return entry.getKey();
        }
        return null;
    }

    public ResourceLocation findRecipeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, FeyCraftingRecipe> entry : getFeyCraftingRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(entry.getValue().getResult(), output)) return entry.getKey();
        }
        return null;
    }

    public boolean removeByName(ResourceLocation name) {
        FeyCraftingRecipe recipe = getFeyCraftingRecipe(name);
        if (recipe == null) return false;
        removeFeyCraftingRecipe(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, FeyCraftingRecipe> x : getFeyCraftingRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getResult(), output)) {
                getFeyCraftingRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public void removeAll() {
        getFeyCraftingRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getFeyCraftingRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, FeyCraftingRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getFeyCraftingRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<FeyCraftingRecipe> {

        private int xp = 0;
        private ResourceLocation name;

        public RecipeBuilder xp(int xp) {
            this.xp = xp;
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
            return "Error adding Roots Fey Crafter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 5, 5, 1, 1);
            validateFluids(msg);
            msg.add(xp < 0, "xp must be a nonnegative integer, yet it was {}", xp);
            if (name == null) {
                name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), RecipeName.generate("groovyscript_fey_crafter_"));
            }
        }

        @Override
        public @Nullable FeyCraftingRecipe register() {
            if (!validate()) return null;
            FeyCraftingRecipe recipe = new FeyCraftingRecipe(output.get(0), xp);
            input.forEach(i -> recipe.addIngredient(i.toMcIngredient()));
            ModSupport.ROOTS.get().feyCrafter.add(name, recipe);
            return recipe;
        }
    }
}
