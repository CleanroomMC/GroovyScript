package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.FlowerRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static epicsquid.roots.init.ModRecipes.getFlowerRecipes;


public class FlowerGeneration extends VirtualizedRegistry<Pair<ResourceLocation, FlowerRecipe>> {

    public FlowerGeneration() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> getFlowerRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> getFlowerRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(FlowerRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, FlowerRecipe recipe) {
        getFlowerRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(FlowerRecipe recipe) {
        for (Map.Entry<ResourceLocation, FlowerRecipe> entry : getFlowerRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    public boolean removeByName(ResourceLocation name) {
        FlowerRecipe recipe = getFlowerRecipes().get(name);
        if (recipe == null) return false;
        getFlowerRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByFlower(IBlockState flower) {
        for (Map.Entry<ResourceLocation, FlowerRecipe> x : getFlowerRecipes().entrySet()) {
            if (x.getValue().getFlower() == flower) {
                getFlowerRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public boolean removeByFlower(Block flower, int meta) {
        return removeByFlower(flower.getStateFromMeta(meta));
    }

    public boolean removeByFlower(Block flower) {
        boolean found = false;
        for (IBlockState state : flower.getBlockState().getValidStates()) {
            if (removeByFlower(state)) found = true;
        }
        return found;
    }

    public boolean removeByFlower(ItemStack output) {
        return removeByFlower(((ItemBlock) output.getItem()).getBlock().getStateFromMeta(output.getMetadata()));
    }

    public void removeAll() {
        getFlowerRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getFlowerRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, FlowerRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getFlowerRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<FlowerRecipe> {

        private IBlockState flower;
        private final List<Ingredient> allowedSoils = new ArrayList<>();

        public RecipeBuilder flower(IBlockState flower) {
            this.flower = flower;
            return this;
        }

        public RecipeBuilder flower(Block flower, int meta) {
            this.flower = flower.getStateFromMeta(meta);
            return this;
        }

        /*
        public RecipeBuilder allowedSoils(IIngredient allowedSoils) {
            this.allowedSoils.add(allowedSoils.toMcIngredient());
            return this;
        }

        public RecipeBuilder allowedSoils(IIngredient... allowedSoilss) {
            for (IIngredient allowedSoils : allowedSoilss) {
                allowedSoils(allowedSoils);
            }
            return this;
        }

        public RecipeBuilder allowedSoils(Collection<IIngredient> allowedSoilss) {
            for (IIngredient allowedSoils : allowedSoilss) {
                allowedSoils(allowedSoils);
            }
            return this;
        }
        */

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Flower Generation recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_flower_generation_recipe_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg);
            validateFluids(msg);
            msg.add(flower == null, "flower must be defined");
        }

        @Override
        public @Nullable FlowerRecipe register() {
            if (!validate()) return null;
            FlowerRecipe recipe = new FlowerRecipe(name, flower, allowedSoils);
            ModSupport.ROOTS.get().flowerGeneration.add(name, recipe);
            return recipe;
        }
    }
}
