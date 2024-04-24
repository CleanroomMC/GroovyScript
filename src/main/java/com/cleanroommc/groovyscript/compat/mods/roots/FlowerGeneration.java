package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.init.ModRecipes;
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

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class FlowerGeneration extends VirtualizedRegistry<Pair<ResourceLocation, FlowerRecipe>> {

    @RecipeBuilderDescription(example = @Example(".name('clay_flower').flower(blockstate('minecraft:clay'))"))
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> ModRecipes.getFlowerRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> ModRecipes.getFlowerRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(FlowerRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, FlowerRecipe recipe) {
        ModRecipes.getFlowerRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(FlowerRecipe recipe) {
        for (Map.Entry<ResourceLocation, FlowerRecipe> entry : ModRecipes.getFlowerRecipes().entrySet()) {
            if (entry.getValue().equals(recipe)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:dandelion')"))
    public boolean removeByName(ResourceLocation name) {
        FlowerRecipe recipe = ModRecipes.getFlowerRecipes().get(name);
        if (recipe == null) return false;
        ModRecipes.getFlowerRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.flower_generation.removeByFlower0", example = @Example("blockstate('minecraft:red_flower:2')"))
    public boolean removeByFlower(IBlockState flower) {
        for (Map.Entry<ResourceLocation, FlowerRecipe> x : ModRecipes.getFlowerRecipes().entrySet()) {
            if (x.getValue().getFlower() == flower) {
                ModRecipes.getFlowerRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.flower_generation.removeByFlower1", example = @Example("block('minecraft:red_flower'), 1"))
    public boolean removeByFlower(Block flower, int meta) {
        return removeByFlower(flower.getStateFromMeta(meta));
    }

    @MethodDescription(description = "groovyscript.wiki.roots.flower_generation.removeByFlower2", example = @Example("block('minecraft:red_flower')"))
    public boolean removeByFlower(Block flower) {
        boolean found = false;
        for (IBlockState state : flower.getBlockState().getValidStates()) {
            if (removeByFlower(state)) found = true;
        }
        return found;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.flower_generation.removeByFlower3", example = @Example("item('minecraft:red_flower:3')"))
    public boolean removeByFlower(ItemStack output) {
        return removeByFlower(((ItemBlock) output.getItem()).getBlock().getStateFromMeta(output.getMetadata()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipes.getFlowerRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ModRecipes.getFlowerRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, FlowerRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipes.getFlowerRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<FlowerRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private IBlockState flower;
        private final List<Ingredient> allowedSoils = new ArrayList<>();

        @RecipeBuilderMethodDescription
        public RecipeBuilder flower(IBlockState flower) {
            this.flower = flower;
            return this;
        }

        @RecipeBuilderMethodDescription
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

        @Override
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
        @RecipeBuilderRegistrationMethod
        public @Nullable FlowerRecipe register() {
            if (!validate()) return null;
            FlowerRecipe recipe = new FlowerRecipe(super.name, flower, allowedSoils);
            ModSupport.ROOTS.get().flowerGeneration.add(super.name, recipe);
            return recipe;
        }
    }
}
