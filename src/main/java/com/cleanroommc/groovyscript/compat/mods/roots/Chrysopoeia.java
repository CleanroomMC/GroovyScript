package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.ModRecipesAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.ChrysopoeiaRecipe;
import epicsquid.roots.util.IngredientWithStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Chrysopoeia extends VirtualizedRegistry<Pair<ResourceLocation, ChrysopoeiaRecipe>> {

    public Chrysopoeia() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> ModRecipesAccessor.getChrysopoeiaRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> ModRecipesAccessor.getChrysopoeiaRecipes().put(pair.getKey(), pair.getValue()));
    }

    public void add(String name, ChrysopoeiaRecipe recipe) {
        add(new ResourceLocation(GroovyScript.getRunConfig().getPackId(), name), recipe);
    }

    public void add(ResourceLocation name, ChrysopoeiaRecipe recipe) {
        ModRecipesAccessor.getChrysopoeiaRecipes().put(name, recipe);
        addScripted(Pair.of(name, recipe));
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

    public boolean removeByName(ResourceLocation name) {
        ChrysopoeiaRecipe recipe = ModRecipesAccessor.getChrysopoeiaRecipes().get(name);
        if (recipe == null) return false;
        ModRecipesAccessor.getChrysopoeiaRecipes().remove(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, ChrysopoeiaRecipe> entry : ModRecipesAccessor.getChrysopoeiaRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(entry.getValue().getOutput(), output)) {
                ModRecipesAccessor.getChrysopoeiaRecipes().remove(entry.getKey());
                addBackup(Pair.of(entry.getKey(), entry.getValue()));
                return true;
            }
        }
        return false;
    }

    public boolean removeByInput(ItemStack output) {
        for (Map.Entry<ResourceLocation, ChrysopoeiaRecipe> entry : ModRecipesAccessor.getChrysopoeiaRecipes().entrySet()) {
            if (entry.getValue().getIngredient().getIngredient().test(output)) {
                ModRecipesAccessor.getChrysopoeiaRecipes().remove(entry.getKey());
                addBackup(Pair.of(entry.getKey(), entry.getValue()));
                return true;
            }
        }
        return false;
    }

    public void removeAll() {
        ModRecipesAccessor.getChrysopoeiaRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ModRecipesAccessor.getChrysopoeiaRecipes().clear();
    }

    public SimpleObjectStream<Map.Entry<ResourceLocation, ChrysopoeiaRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(ModRecipesAccessor.getChrysopoeiaRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<ChrysopoeiaRecipe> {

        // overload, byproductChance, and byproduct are all unused
//        private float overload = 0.0F;
//        private float byproductChance = 0.0F;
//        private ItemStack byproduct = ItemStack.EMPTY;
        private ResourceLocation name;

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
            return "Error adding Roots Chrysopoeia conversion recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
//            msg.add(overload < 0, "overload must be a nonnegative float, yet it was {}", overload);
//            msg.add(byproductChance < 0, "byproductChance must be a nonnegative float, yet it was {}", byproductChance);
            if (name == null) {
                name = new ResourceLocation(GroovyScript.getRunConfig().getPackId(), RecipeName.generate("groovyscript_chrysopoeia_"));
            }
        }

        @Override
        public @Nullable ChrysopoeiaRecipe register() {
            if (!validate()) return null;
            ChrysopoeiaRecipe recipe = new ChrysopoeiaRecipe(new IngredientWithStack(IngredientHelper.toItemStack(input.get(0))), output.get(0)/*, byproduct, overload, byproductChance*/);
            recipe.setRegistryName(name);
            ModSupport.ROOTS.get().chrysopoeia.add(name, recipe);
            return recipe;
        }
    }
}
