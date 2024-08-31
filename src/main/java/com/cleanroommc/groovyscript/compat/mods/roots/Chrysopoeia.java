package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.ModRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.ChrysopoeiaRecipe;
import epicsquid.roots.util.IngredientWithStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription
public class Chrysopoeia extends VirtualizedRegistry<ChrysopoeiaRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_transmute').input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:diamond') * 3).output(item('minecraft:gold_ingot') * 3)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> ModRecipesAccessor.getChrysopoeiaRecipes().remove(recipe.getRegistryName()));
        restoreFromBackup().forEach(recipe -> ModRecipesAccessor.getChrysopoeiaRecipes().put(recipe.getRegistryName(), recipe));
    }

    public void add(ChrysopoeiaRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, ChrysopoeiaRecipe recipe) {
        ModRecipesAccessor.getChrysopoeiaRecipes().put(name, recipe);
        addScripted(recipe);
    }

    public ResourceLocation findRecipe(ChrysopoeiaRecipe recipe) {
        for (Map.Entry<ResourceLocation, ChrysopoeiaRecipe> entry : ModRecipesAccessor.getChrysopoeiaRecipes().entrySet()) {
            if (entry.getValue().matches(recipe.getOutput())) return entry.getKey();
        }
        return null;
    }

    public ResourceLocation findRecipeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, ChrysopoeiaRecipe> entry : ModRecipesAccessor.getChrysopoeiaRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(entry.getValue().getOutput(), output)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:gold_from_silver')"))
    public boolean removeByName(ResourceLocation name) {
        ChrysopoeiaRecipe recipe = ModRecipesAccessor.getChrysopoeiaRecipes().get(name);
        if (recipe == null) return false;
        ModRecipesAccessor.getChrysopoeiaRecipes().remove(name);
        addBackup(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:iron_nugget')"))
    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, ChrysopoeiaRecipe> entry : ModRecipesAccessor.getChrysopoeiaRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(entry.getValue().getOutput(), output)) {
                ModRecipesAccessor.getChrysopoeiaRecipes().remove(entry.getKey());
                addBackup(entry.getValue());
                return true;
            }
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:rotten_flesh')"))
    public boolean removeByInput(ItemStack output) {
        for (Map.Entry<ResourceLocation, ChrysopoeiaRecipe> entry : ModRecipesAccessor.getChrysopoeiaRecipes().entrySet()) {
            if (entry.getValue().getIngredient().getIngredient().test(output)) {
                ModRecipesAccessor.getChrysopoeiaRecipes().remove(entry.getKey());
                addBackup(entry.getValue());
                return true;
            }
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipesAccessor.getChrysopoeiaRecipes().values().forEach(this::addBackup);
        ModRecipesAccessor.getChrysopoeiaRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, ChrysopoeiaRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipesAccessor.getChrysopoeiaRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ChrysopoeiaRecipe> {

//        overload, byproductChance, and byproduct are all unused
//        private float overload = 0.0F;
//        private float byproductChance = 0.0F;
//        private ItemStack byproduct = ItemStack.EMPTY;

//        public RecipeBuilder overload(float overload) {
//            this.overload = overload;
//            return this;
//        }

//        public RecipeBuilder byproductChance(float byproductChance) {
//            this.byproductChance = byproductChance;
//            return this;
//        }

//        public RecipeBuilder byproduct(IIngredient byproduct) {
//            this.byproduct = IngredientHelper.toItemStack(byproduct);
//            return this;
//        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Chrysopoeia conversion recipe";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_chrysopoeia_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
//            msg.add(overload < 0, "overload must be a nonnegative float, yet it was {}", overload);
//            msg.add(byproductChance < 0, "byproductChance must be a nonnegative float, yet it was {}", byproductChance);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ChrysopoeiaRecipe register() {
            if (!validate()) return null;
            ChrysopoeiaRecipe recipe = new ChrysopoeiaRecipe(new IngredientWithStack(IngredientHelper.toItemStack(input.get(0))), output.get(0)/*, byproduct, overload, byproductChance*/);
            recipe.setRegistryName(super.name);
            ModSupport.ROOTS.get().chrysopoeia.add(super.name, recipe);
            return recipe;
        }
    }
}
