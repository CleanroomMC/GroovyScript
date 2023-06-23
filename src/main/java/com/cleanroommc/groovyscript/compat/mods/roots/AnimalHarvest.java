package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.AnimalHarvestRecipe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.getAnimalHarvestRecipes;
import static epicsquid.roots.init.ModRecipes.removeAnimalHarvestRecipe;

public class AnimalHarvest extends VirtualizedRegistry<Pair<ResourceLocation, AnimalHarvestRecipe>> {

    public AnimalHarvest() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> removeAnimalHarvestRecipe(pair.getKey()));
        restoreFromBackup().forEach(pair -> getAnimalHarvestRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(AnimalHarvestRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, AnimalHarvestRecipe recipe) {
        getAnimalHarvestRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(AnimalHarvestRecipe recipe) {
        for (Map.Entry<ResourceLocation, AnimalHarvestRecipe> entry : getAnimalHarvestRecipes().entrySet()) {
            if (entry.getValue().matches(recipe.getHarvestClass())) return entry.getKey();
        }
        return null;
    }

    public boolean removeByName(ResourceLocation name) {
        AnimalHarvestRecipe recipe = getAnimalHarvestRecipes().get(name);
        if (recipe == null) return false;
        removeAnimalHarvestRecipe(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByEntity(EntityEntry entity) {
        for (Map.Entry<ResourceLocation, AnimalHarvestRecipe> x : getAnimalHarvestRecipes().entrySet()) {
            if (x.getValue().matches(entity.getEntityClass())) {
                getAnimalHarvestRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public void removeAll() {
        getAnimalHarvestRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getAnimalHarvestRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, AnimalHarvestRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getAnimalHarvestRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AnimalHarvestRecipe> {

        private Class<? extends EntityLivingBase> entity;

        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = (Class<? extends EntityLivingBase>) entity.getEntityClass();
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Animal Harvest recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_animal_harvest_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg);
            validateFluids(msg);
            msg.add(entity == null, "entity must be defined and extended EntityLivingBase, instead it was {}", entity);
        }

        @Override
        public @Nullable AnimalHarvestRecipe register() {
            if (!validate()) return null;
            AnimalHarvestRecipe recipe = new AnimalHarvestRecipe(name, entity);
            ModSupport.ROOTS.get().animalHarvest.add(name, recipe);
            return recipe;
        }
    }
}
