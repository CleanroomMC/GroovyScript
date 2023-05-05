package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.ModRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.SummonCreatureRecipe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static epicsquid.roots.init.ModRecipes.*;

public class SummonCreature extends VirtualizedRegistry<Pair<ResourceLocation, SummonCreatureRecipe>> {

    public SummonCreature() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> removeSummonCreatureEntry(pair.getKey()));
        restoreFromBackup().forEach(pair -> addSummonCreatureEntry(pair.getValue()));
    }

    public void add(String name, SummonCreatureRecipe recipe) {
        add(new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name), recipe);
    }

    public void add(ResourceLocation name, SummonCreatureRecipe recipe) {
        addSummonCreatureEntry(recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(SummonCreatureRecipe recipe) {
        for (Map.Entry<ResourceLocation, SummonCreatureRecipe> entry : ModRecipesAccessor.getSummonCreatureEntries().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    public boolean removeByName(ResourceLocation name) {
        SummonCreatureRecipe recipe = getSummonCreatureEntry(name);
        if (recipe == null) return false;
        removeSummonCreatureEntry(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByEntity(EntityEntry entity) {
        return removeByEntity((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    public boolean removeByEntity(Class<? extends EntityLivingBase> clazz) {
        for (Map.Entry<ResourceLocation, SummonCreatureRecipe> x : ModRecipesAccessor.getSummonCreatureEntries().entrySet()) {
            if (x.getValue().getClazz() == clazz) {
                removeSummonCreatureEntry(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public void removeAll() {
        ModRecipesAccessor.getSummonCreatureEntries().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ModRecipesAccessor.getSummonCreatureEntries().clear();
        ModRecipesAccessor.getSummonCreatureClasses().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, SummonCreatureRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipesAccessor.getSummonCreatureEntries().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<SummonCreatureRecipe> {

        private Class<? extends EntityLivingBase> entity;
        private ResourceLocation name;

        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = (Class<? extends EntityLivingBase>) entity.getEntityClass();
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
            return "Error adding Roots Summon Creature recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 10, 0, 0);
            validateFluids(msg);
            msg.add(entity == null, "entity must be defined");
            if (name == null) {
                name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), RecipeName.generate("groovyscript_summon_creature_"));
            }
        }

        @Override
        public @Nullable SummonCreatureRecipe register() {
            if (!validate()) return null;
            List<Ingredient> ingredients = input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList());
            SummonCreatureRecipe recipe = new SummonCreatureRecipe(name, entity, ingredients);
            ModSupport.ROOTS.get().summonCreature.add(name, recipe);
            return recipe;
        }
    }
}
