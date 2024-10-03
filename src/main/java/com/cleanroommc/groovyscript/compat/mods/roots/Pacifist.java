package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.PacifistEntryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.recipe.PacifistEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Pacifist extends VirtualizedRegistry<PacifistEntry> {

    @RecipeBuilderDescription(example = @Example(".name('wither_skeleton_pacifist').entity(entity('minecraft:wither_skeleton'))"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipes.getPacifistEntities().remove(recipe.getRegistryName()));
        restoreFromBackup().forEach(recipe -> ModRecipes.getPacifistEntities().put(recipe.getRegistryName(), recipe));
    }

    public void add(PacifistEntry recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, PacifistEntry recipe) {
        ModRecipes.getPacifistEntities().put(name, recipe);
        addScripted(recipe);
    }

    public ResourceLocation findRecipe(PacifistEntry recipe) {
        for (Map.Entry<ResourceLocation, PacifistEntry> entry : ModRecipes.getPacifistEntities().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('minecraft:chicken')"))
    public boolean removeByName(ResourceLocation name) {
        PacifistEntry recipe = ModRecipes.getPacifistEntities().get(name);
        if (recipe == null) return false;
        ModRecipes.getPacifistEntities().remove(name);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("entity('minecraft:cow')"))
    public boolean removeByEntity(EntityEntry entity) {
        return removeByClass(entity.getEntityClass());
    }

    @MethodDescription
    public boolean removeByClass(Class<? extends Entity> clazz) {
        return ModRecipes.getPacifistEntities().entrySet().removeIf(x -> {
            if (x.getValue().getEntityClass().equals(clazz)) {
                addBackup(x.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.getPacifistEntities().values().forEach(this::addBackup);
        ModRecipes.getPacifistEntities().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, PacifistEntry>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.getPacifistEntities().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<PacifistEntry> {

        @Property(comp = @Comp(not = "null"))
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

        @Override
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
            PacifistEntry recipe = new PacifistEntry(entity, super.name.toString());
            ((PacifistEntryAccessor) recipe).setName(super.name);
            ModSupport.ROOTS.get().pacifist.add(recipe.getRegistryName(), recipe);
            return recipe;
        }

    }

}
