package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.woot.SpawnRecipeRepositoryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ipsis.Woot;
import ipsis.woot.farming.ISpawnRecipe;
import ipsis.woot.farming.SpawnRecipe;
import ipsis.woot.util.WootMobName;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Spawning extends VirtualizedRegistry<Pair<WootMobName, SpawnRecipe>> {

    public Spawning() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        restoreFromBackup().forEach(pair -> ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().put(pair.getKey(), pair.getValue()));
        removeScripted().forEach(pair -> ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().remove(pair.getKey()));
        //((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe().setEfficiency(true);
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe().getItems().clear();
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe().getFluids().clear();
    }

    public void addDefaultItem(ItemStack item) {
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe().addIngredient(item);
    }

    public void addDefaultFluid(FluidStack fluid) {
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe().addIngredient(fluid);
    }

    //public void setDefaultEfficiency(boolean efficiency) {
    //    ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe().setEfficiency(efficiency);
    //}

    public void add(WootMobName name, SpawnRecipe recipe) {
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public boolean remove(WootMobName name) {
        SpawnRecipe recipe = ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().get(name);
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByEntity(WootMobName name) {
        return remove(name);
    }

    public boolean removeByEntity(EntityEntry entity) {
        return remove(new WootMobName(entity.getName()));
    }

    public boolean removeByEntity(String name) {
        return remove(new WootMobName(name));
    }

    public boolean removeByEntity(String name, String tag) {
        return remove(new WootMobName(name, tag));
    }

    public void removeAll() {
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().forEach((l, r) -> addBackup(Pair.of(l, r)));
        ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<WootMobName, SpawnRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getRecipes().entrySet())
                .setRemover(x -> remove(x.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<ISpawnRecipe> {

        private boolean defaultSpawnRecipe = false;
        private WootMobName name;
        //private boolean efficiency;

        public RecipeBuilder defaultSpawnRecipe(boolean defaultSpawnRecipe) {
            this.defaultSpawnRecipe = defaultSpawnRecipe;
            return this;
        }

        public RecipeBuilder defaultSpawnRecipe() {
            this.defaultSpawnRecipe = !defaultSpawnRecipe;
            return this;
        }

        public RecipeBuilder name(WootMobName name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder name(EntityEntry entity) {
            this.name = new WootMobName(entity.getName());
            return this;
        }

        public RecipeBuilder name(String name) {
            this.name = new WootMobName(name);
            return this;
        }

        //public RecipeBuilder efficiency(boolean efficiency) {
        //    this.efficiency = efficiency;
        //    return this;
        //}

        @Override
        public String getErrorMsg() {
            return "Error adding Woot custom spawning costs";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 6, 0, 0);
            validateFluids(msg, 0, 6, 0, 0);
            msg.add(!defaultSpawnRecipe && (name == null || !name.isValid()), "if the recipe is not the defaultSpawnRecipe, the name must be defined and a valid name, yet it was {}", name);
        }

        @Override
        public @Nullable ISpawnRecipe register() {
            if (!validate()) return null;

            SpawnRecipe recipe = defaultSpawnRecipe
                                 ? ((SpawnRecipeRepositoryAccessor) Woot.spawnRecipeRepository).getDefaultSpawnRecipe()
                                 : new SpawnRecipe();
            for (IIngredient item : input) {
                recipe.addIngredient(item.getMatchingStacks()[0]);
            }
            for (FluidStack fluid : fluidInput) {
                recipe.addIngredient(fluid);
            }
            //recipe.setEfficiency(efficiency);

            if (!defaultSpawnRecipe) ModSupport.WOOT.get().spawning.add(name, recipe);
            return recipe;
        }
    }

}
