package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.PyreCraftingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static epicsquid.roots.init.ModRecipes.*;

@RegistryDescription
public class Pyre extends VirtualizedRegistry<Pair<ResourceLocation, PyreCraftingRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_from_fire').input(item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone'),item('minecraft:stone')).output(item('minecraft:clay')).xp(5).time(1)"),
            @Example(".input(item('minecraft:gold_ingot'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:stone'),item('minecraft:stone')).output(item('minecraft:diamond') * 32).levels(5).burnTime(1000)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> removePyreCraftingRecipe(pair.getKey()));
        restoreFromBackup().forEach(pair -> addPyreCraftingRecipe(pair.getKey(), pair.getValue()));
    }

    public void add(PyreCraftingRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, PyreCraftingRecipe recipe) {
        addPyreCraftingRecipe(name, recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(PyreCraftingRecipe recipe) {
        for (Map.Entry<ResourceLocation, PyreCraftingRecipe> entry : getPyreCraftingRecipes().entrySet()) {
            if (entry.getValue().matches(recipe.getRecipe())) return entry.getKey();
        }
        return null;
    }

    public ResourceLocation findRecipeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, PyreCraftingRecipe> entry : getPyreCraftingRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(entry.getValue().getResult(), output)) return entry.getKey();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:infernal_bulb')"))
    public boolean removeByName(ResourceLocation name) {
        PyreCraftingRecipe recipe = getCraftingRecipe(name);
        if (recipe == null) return false;
        removePyreCraftingRecipe(name);
        addBackup(Pair.of(name, recipe));
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:gravel')"))
    public boolean removeByOutput(ItemStack output) {
        for (Map.Entry<ResourceLocation, PyreCraftingRecipe> x : getPyreCraftingRecipes().entrySet()) {
            if (ItemStack.areItemsEqual(x.getValue().getResult(), output)) {
                getPyreCraftingRecipes().remove(x.getKey());
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
        }
        return false;
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getPyreCraftingRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        getPyreCraftingRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<ResourceLocation, PyreCraftingRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(getPyreCraftingRecipes().entrySet())
                .setRemover(r -> this.removeByName(r.getKey()));
    }

    @Property(property = "name")
    @Property(property = "input", valid = @Comp("5"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PyreCraftingRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int xp = 0;
        @Property(defaultValue = "200")
        private int burnTime = 200;

        @RecipeBuilderMethodDescription
        public RecipeBuilder xp(int xp) {
            this.xp = xp;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "xp")
        public RecipeBuilder levels(int levels) {
            this.xp = levels;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder burnTime(int burnTime) {
            this.burnTime = burnTime;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "burnTime")
        public RecipeBuilder time(int time) {
            return this.burnTime(time);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Pyre recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_pyre_recipe_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 5, 5, 1, 1);
            validateFluids(msg);
            msg.add(xp < 0, "xp must be a nonnegative integer, yet it was {}", xp);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PyreCraftingRecipe register() {
            if (!validate()) return null;
            PyreCraftingRecipe recipe = new PyreCraftingRecipe(output.get(0), xp);
            input.forEach(i -> recipe.addIngredient(i.toMcIngredient()));
            recipe.setBurnTime(this.burnTime);
            recipe.setName(name.toString());
            ModSupport.ROOTS.get().pyre.add(name, recipe);
            return recipe;
        }
    }
}
