package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.PacifistEntryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.PacifistEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.getPacifistEntities;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Pacifist extends VirtualizedRegistry<Pair<ResourceLocation, PacifistEntry>> {

    @RecipeBuilderDescription(example = @Example(".name('wither_skeleton_pacifist').entity(entity('minecraft:wither_skeleton'))"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> getPacifistEntities().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> getPacifistEntities().put(pair.getKey(), pair.getValue()));
    }

    public void add(PacifistEntry recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, PacifistEntry recipe) {
        getPacifistEntities().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(PacifistEntry recipe) {
        for (Map.Entry<ResourceLocation, PacifistEntry> entry : getPacifistEntities().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('minecraft:chicken')"))
    public boolean removeByName(ResourceLocation name) {
        PacifistEntry recipe = getPacifistEntities().get(name);
        if (recipe == null) return false;
        getPacifistEntities().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription(example = @Example("entity('minecraft:cow')"))
    public boolean removeByEntity(EntityEntry entity) {
        return removeByClass(entity.getEntityClass());
    }

    @MethodDescription
    public boolean removeByClass(Class<? extends Entity> clazz) {
        return getPacifistEntities().entrySet().removeIf(x -> {
            if (x.getValue().getEntityClass().equals(clazz)) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getPacifistEntities().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getPacifistEntities().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, PacifistEntry>> streamRecipes() {
        return new SimpleObjectStream<>(getPacifistEntities().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<PacifistEntry> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private Class<? extends Entity> entity;

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = entity.getEntityClass();
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Runic Shear Entity recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_pacifist_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg);
            validateFluids(msg);
            msg.add(entity == null, "entity must be defined");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PacifistEntry register() {
            if (!validate()) return null;
            PacifistEntry recipe = new PacifistEntry(entity, name.toString());
            ((PacifistEntryAccessor) recipe).setName(name);
            ModSupport.ROOTS.get().pacifist.add(recipe.getRegistryName(), recipe);
            return recipe;
        }
    }
}
