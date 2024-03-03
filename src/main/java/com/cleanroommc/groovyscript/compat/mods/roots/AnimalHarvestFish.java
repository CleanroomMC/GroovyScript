package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.AnimalHarvestFishRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.getAnimalHarvestFishRecipes;
import static epicsquid.roots.init.ModRecipes.removeAnimalHarvestFishRecipe;

@RegistryDescription
public class AnimalHarvestFish extends VirtualizedRegistry<Pair<ResourceLocation, AnimalHarvestFishRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_fish').weight(50).output(item('minecraft:clay'))"),
            @Example(".weight(13).fish(item('minecraft:gold_ingot'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> removeAnimalHarvestFishRecipe(pair.getKey()));
        restoreFromBackup().forEach(pair -> getAnimalHarvestFishRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(AnimalHarvestFishRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, AnimalHarvestFishRecipe recipe) {
        getAnimalHarvestFishRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, AnimalHarvestFishRecipe> entry : getAnimalHarvestFishRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(entry.getValue().getItemStack(), output)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:cod')"))
    public boolean removeByName(ResourceLocation name) {
        AnimalHarvestFishRecipe recipe = getAnimalHarvestFishRecipes().get(name);
        if (recipe == null) return false;
        removeAnimalHarvestFishRecipe(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:fish:1')"))
    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, AnimalHarvestFishRecipe> x : getAnimalHarvestFishRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getItemStack(), output)) {
                getAnimalHarvestFishRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:fish:2')"))
    public boolean removeByFish(ItemStack fish) {
        return removeByOutput(fish);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getAnimalHarvestFishRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getAnimalHarvestFishRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, AnimalHarvestFishRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getAnimalHarvestFishRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AnimalHarvestFishRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int weight;

        @RecipeBuilderMethodDescription
        public RecipeBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "output")
        public RecipeBuilder fish(ItemStack fish) {
            this.output.add(fish);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Animal Harvest Fish recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_animal_harvest_fish_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(weight <= 0, "weight must be a nonnegative integer greater than 0, instead it was {}", weight);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AnimalHarvestFishRecipe register() {
            if (!validate()) return null;
            AnimalHarvestFishRecipe recipe = new AnimalHarvestFishRecipe(name, output.get(0), weight);
            ModSupport.ROOTS.get().animalHarvestFish.add(name, recipe);
            return recipe;
        }
    }
}
