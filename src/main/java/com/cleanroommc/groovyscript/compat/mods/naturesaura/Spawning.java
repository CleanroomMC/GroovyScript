package com.cleanroommc.groovyscript.compat.mods.naturesaura;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.recipes.AnimalSpawnerRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.naturesaura.spawning.note0", type = Admonition.Type.WARNING))
public class Spawning extends VirtualizedRegistry<AnimalSpawnerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('demo').input(item('minecraft:clay')).entity(entity('minecraft:bat')).aura(100).time(100)"),
            @Example(".name(resource('example:demo')).input(item('minecraft:mutton')).entity(entity('minecraft:wolf')).aura(30).time(5)"),
            @Example(".input(item('minecraft:bone'), item('minecraft:dye:15') * 4).entity(resource('minecraft:skeleton')).aura(10).time(10)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.remove(x.name));
        restoreFromBackup().forEach(x -> NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.put(x.name, x));
    }

    public void add(AnimalSpawnerRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.put(recipe.name, recipe);
    }

    public boolean remove(AnimalSpawnerRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.remove(recipe.name) != null;
    }

    @MethodDescription(example = @Example("resource('naturesaura:cow')"))
    public boolean removeByName(ResourceLocation name) {
        if (name == null) return false;
        var recipe = NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.remove(name);
        if (recipe == null) return false;
        addBackup(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:bone')"))
    public boolean removeByInput(IIngredient input) {
        return NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.entrySet().removeIf(r -> {
            for (var ingredient : r.getValue().ingredients) {
                for (var item : ingredient.getMatchingStacks()) {
                    if (input.test(item)) {
                        addBackup(r.getValue());
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("resource('minecraft:cave_spider')"))
    public boolean removeByEntity(ResourceLocation resourceLocation) {
        return NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.entrySet().removeIf(r -> {
            if (r.getValue().entity.equals(resourceLocation)) {
                addBackup(r.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("entity('minecraft:polar_bear')"))
    public boolean removeByEntity(EntityEntry entity) {
        return NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.entrySet().removeIf(r -> {
            if (r.getValue().entity.equals(entity.getRegistryName())) {
                addBackup(r.getValue());
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.values().forEach(this::addBackup);
        NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.entrySet().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, AnimalSpawnerRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(NaturesAuraAPI.ANIMAL_SPAWNER_RECIPES.entrySet()).setRemover(x -> remove(x.getValue()));
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "Integer.MAX_VALUE")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AnimalSpawnerRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private ResourceLocation entity;
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int aura;
        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(EntityEntry entity) {
            this.entity = entity.getRegistryName();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder entity(ResourceLocation entity) {
            this.entity = entity;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder aura(int aura) {
            this.aura = aura;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Nature's Aura Spawning Recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_spawning_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, Integer.MAX_VALUE, 0, 0);
            validateFluids(msg);
            msg.add(entity == null, "entity must not be null");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AnimalSpawnerRecipe register() {
            if (!validate()) return null;
            AnimalSpawnerRecipe recipe = new AnimalSpawnerRecipe(name, entity, aura, time, input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new));
            ModSupport.NATURES_AURA.get().spawning.add(recipe);
            return recipe;
        }
    }
}
