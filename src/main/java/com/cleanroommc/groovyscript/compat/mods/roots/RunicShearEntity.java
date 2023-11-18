package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.RunicShearConditionalEntityRecipe;
import epicsquid.roots.recipe.RunicShearEntityRecipe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

import static epicsquid.roots.init.ModRecipes.getRunicShearEntityRecipes;

public class RunicShearEntity extends VirtualizedRegistry<Pair<ResourceLocation, RunicShearEntityRecipe>> {

    public RunicShearEntity() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        // FIXME Not currently reloadable due to being controlled by the Capability RunicShearsCapabilityProvider
        removeScripted().forEach(pair -> getRunicShearEntityRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> getRunicShearEntityRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(RunicShearEntityRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, RunicShearEntityRecipe recipe) {
        getRunicShearEntityRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(RunicShearEntityRecipe recipe) {
        for (Map.Entry<ResourceLocation, RunicShearEntityRecipe> entry : getRunicShearEntityRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    public boolean removeByName(ResourceLocation name) {
        RunicShearEntityRecipe recipe = getRunicShearEntityRecipes().get(name);
        if (recipe == null) return false;
        getRunicShearEntityRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByOutput(ItemStack output) {
        return getRunicShearEntityRecipes().entrySet().removeIf(x -> {
            if (ItemStack.areItemsEqual(x.getValue().getDrop(), output)) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    public boolean removeByEntity(EntityEntry entity) {
        return removeByEntity((Class<? extends EntityLivingBase>) entity.getEntityClass());
    }

    public boolean removeByEntity(Class<? extends EntityLivingBase> clazz) {
        for (Map.Entry<ResourceLocation, RunicShearEntityRecipe> x : getRunicShearEntityRecipes().entrySet()) {
            if (x.getValue().getClazz() == clazz) {
                getRunicShearEntityRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    public void removeAll() {
        getRunicShearEntityRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getRunicShearEntityRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, RunicShearEntityRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getRunicShearEntityRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<RunicShearEntityRecipe> {

        private Class<? extends EntityLivingBase> entity;
        private int cooldown;
        private Function<EntityLivingBase, ItemStack> functionMap;

        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = (Class<? extends EntityLivingBase>) entity.getEntityClass();
            return this;
        }

        public RecipeBuilder cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public RecipeBuilder functionMap(Function<EntityLivingBase, ItemStack> functionMap) {
            this.functionMap = functionMap;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Runic Shear Entity recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_runic_shear_entity_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            msg.add(!input.isEmpty(), () -> "No item inputs allowed, but found " + input.size());
            validateFluids(msg);
            msg.add(output.size() > 1 && functionMap == null, "if output is greater than 1, functionMap must be defined");
            msg.add(entity == null, "entity must be defined and extended EntityLivingBase, instead it was {}", entity);
        }

        @Override
        public @Nullable RunicShearEntityRecipe register() {
            if (!validate()) return null;
            RunicShearEntityRecipe recipe;
            if (functionMap == null) recipe = new RunicShearEntityRecipe(name, output.get(0), entity, cooldown);
            else recipe = new RunicShearConditionalEntityRecipe(name, functionMap, new HashSet<>(output), entity, cooldown);
            ModSupport.ROOTS.get().runicShearEntity.add(name, recipe);
            return recipe;
        }
    }
}
