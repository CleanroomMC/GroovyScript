package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.recipe.AnimalHarvestRecipe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription
public class AnimalHarvest extends VirtualizedRegistry<AnimalHarvestRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('wither_skeleton_harvest').entity(entity('minecraft:wither_skeleton'))"),
            @Example(".entity(entity('minecraft:enderman'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipes.removeAnimalHarvestRecipe(recipe.getRegistryName()));
        restoreFromBackup().forEach(recipe -> ModRecipes.getAnimalHarvestRecipes().put(recipe.getRegistryName(), recipe));
    }

    public void add(AnimalHarvestRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, AnimalHarvestRecipe recipe) {
        ModRecipes.getAnimalHarvestRecipes().put(name, recipe);
        addScripted(recipe);
    }

    public ResourceLocation findRecipe(AnimalHarvestRecipe recipe) {
        for (Map.Entry<ResourceLocation, AnimalHarvestRecipe> entry : ModRecipes.getAnimalHarvestRecipes().entrySet()) {
            if (entry.getValue().matches(recipe.getHarvestClass())) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:chicken')"))
    public boolean removeByName(ResourceLocation name) {
        AnimalHarvestRecipe recipe = ModRecipes.getAnimalHarvestRecipes().get(name);
        if (recipe == null) return false;
        ModRecipes.removeAnimalHarvestRecipe(name);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("entity('minecraft:pig')"))
    public boolean removeByEntity(EntityEntry entity) {
        for (Map.Entry<ResourceLocation, AnimalHarvestRecipe> x : ModRecipes.getAnimalHarvestRecipes().entrySet()) {
            if (x.getValue().matches(entity.getEntityClass())) {
                ModRecipes.getAnimalHarvestRecipes().remove(x.getKey());
                addBackup(x.getValue());
                return true;
            }
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.getAnimalHarvestRecipes().values().forEach(this::addBackup);
        ModRecipes.getAnimalHarvestRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, AnimalHarvestRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.getAnimalHarvestRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<AnimalHarvestRecipe> {

        @Property(comp = @Comp(not = "null"))
        private Class<? extends EntityLivingBase> entity;

        @SuppressWarnings("unchecked")
        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = (Class<? extends EntityLivingBase>) entity.getEntityClass();
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Animal Harvest recipe";
        }

        @Override
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
        @RecipeBuilderRegistrationMethod
        public @Nullable AnimalHarvestRecipe register() {
            if (!validate()) return null;
            AnimalHarvestRecipe recipe = new AnimalHarvestRecipe(super.name, entity);
            ModSupport.ROOTS.get().animalHarvest.add(super.name, recipe);
            return recipe;
        }
    }
}
