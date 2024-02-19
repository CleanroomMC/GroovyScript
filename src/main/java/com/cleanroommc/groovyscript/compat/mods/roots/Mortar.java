package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.roots.ModRecipesAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.recipe.MortarRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static epicsquid.roots.init.ModRecipes.addMortarRecipe;
import static epicsquid.roots.init.ModRecipes.getMortarRecipes;

@RegistryDescription
public class Mortar extends VirtualizedRegistry<Pair<ResourceLocation, MortarRecipe>> {

    public Mortar() {
        super(Alias.generateOfClassAnd(Mortar.class, "MortarAndPestle"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".name('clay_mortar').input(item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:stone'),item('minecraft:gold_ingot'),item('minecraft:stone')).generate(false).output(item('minecraft:clay')).color(1, 0, 0.1, 1, 0, 0.1)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).color(0, 0, 0.1)"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 16).red(0).green1(0.5).green2(1)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> ModRecipesAccessor.getMortarRecipes().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> addMortarRecipe(pair.getValue()));
    }

    public void add(MortarRecipe recipe) {
        add(recipe.getRegistryName(), recipe);
    }

    public void add(ResourceLocation name, MortarRecipe recipe) {
        addMortarRecipe(recipe);
        addScripted(Pair.of(name, recipe));
    }

    public ResourceLocation findRecipe(MortarRecipe recipe) {
        for (MortarRecipe entry : getMortarRecipes()) {
            if (entry.matches(recipe.getRecipe())) return entry.getRegistryName();
        }
        return null;
    }

    public ResourceLocation findRecipeByOutput(ItemStack output) {
        for (MortarRecipe entry : getMortarRecipes()) {
            if (ItemStack.areItemsEqual(entry.getResult(), output)) return entry.getRegistryName();
        }
        return null;
    }

    @MethodDescription(example = @Example("resource('roots:wheat_flour')"))
    public boolean removeByName(ResourceLocation name) {
        return ModRecipesAccessor.getMortarRecipes().entrySet().removeIf(x -> {
            // Some Mortar recipe names are generated via [base]_x. If we are removing eg "wheat_flour" we should detect and remove all 5 variants
            if (x.getKey().equals(name) || x.getKey().toString().startsWith(name.toString())) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:string')"))
    public boolean removeByOutput(ItemStack output) {
        return ModRecipesAccessor.getMortarRecipes().entrySet().removeIf(x -> {
            if (ItemStack.areItemsEqual(x.getValue().getResult(), output)) {
                addBackup(Pair.of(x.getKey(), x.getValue()));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ModRecipesAccessor.getMortarRecipes().forEach((key, value) -> addBackup(Pair.of(key, value)));
        ModRecipesAccessor.getMortarRecipes().clear();
    }


    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<MortarRecipe> streamRecipes() {
        return new SimpleObjectStream<>(getMortarRecipes())
                .setRemover(r -> this.removeByName(r.getRegistryName()));
    }

    @Property(property = "name")
    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "5")})
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MortarRecipe> {

        @Property(defaultValue = "1.0f", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float red1 = 1.0F;
        @Property(defaultValue = "1.0f", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float green1 = 1.0F;
        @Property(defaultValue = "1.0f", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float blue1 = 1.0F;
        @Property(defaultValue = "1.0f", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float red2 = 1.0F;
        @Property(defaultValue = "1.0f", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float green2 = 1.0F;
        @Property(defaultValue = "1.0f", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
        private float blue2 = 1.0F;
        @Property(defaultValue = "true")
        private boolean generate = true;

        @RecipeBuilderMethodDescription
        public RecipeBuilder red1(float red1) {
            this.red1 = red1;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder green1(float green1) {
            this.green1 = green1;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder blue1(float blue1) {
            this.blue1 = blue1;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder red2(float red2) {
            this.red2 = red2;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder green2(float green2) {
            this.green2 = green2;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder blue2(float blue2) {
            this.blue2 = blue2;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"red1", "red2"})
        public RecipeBuilder red(float red1, float red2) {
            this.red1 = red1;
            this.red2 = red2;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"red1", "red2"})
        public RecipeBuilder red(float red) {
            this.red1 = red;
            this.red2 = red;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"green1", "green2"})
        public RecipeBuilder green(float green1, float green2) {
            this.green1 = green1;
            this.green2 = green2;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"green1", "green2"})
        public RecipeBuilder green(float green) {
            this.green1 = green;
            this.green2 = green;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"blue1", "blue2"})
        public RecipeBuilder blue(float blue1, float blue2) {
            this.blue1 = blue1;
            this.blue2 = blue2;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"blue1", "blue2"})
        public RecipeBuilder blue(float blue) {
            this.blue1 = blue;
            this.blue2 = blue;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"red1", "green1", "blue1", "red2", "green2", "blue2"})
        public RecipeBuilder color(float red1, float green1, float blue1, float red2, float green2, float blue2) {
            this.red1 = red1;
            this.red2 = red2;
            this.green1 = green1;
            this.green2 = green2;
            this.blue1 = blue1;
            this.blue2 = blue2;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"red1", "green1", "blue1", "red2", "green2", "blue2"})
        public RecipeBuilder color(float red, float green, float blue) {
            this.red1 = red;
            this.red2 = red;
            this.green1 = green;
            this.green2 = green;
            this.blue1 = blue;
            this.blue2 = blue;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder generate(boolean generate) {
            this.generate = generate;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder generate() {
            this.generate = !this.generate;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Roots Mortar recipe";
        }

        public String getRecipeNamePrefix() {
            return "groovyscript_mortar_recipe_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 5, 1, 1);
            validateFluids(msg);
            msg.add(red1 < 0 || red1 > 1, "red1 must be a float between 0 and 1, yet it was {}", red1);
            msg.add(green1 < 0 || green1 > 1, "green1 must be a float between 0 and 1, yet it was {}", green1);
            msg.add(blue1 < 0 || blue1 > 1, "blue1 must be a float between 0 and 1, yet it was {}", blue1);
            msg.add(red2 < 0 || red2 > 1, "red2 must be a float between 0 and 1, yet it was {}", red2);
            msg.add(green2 < 0 || green2 > 1, "green2 must be a float between 0 and 1, yet it was {}", green2);
            msg.add(blue2 < 0 || blue2 > 1, "blue2 must be a float between 0 and 1, yet it was {}", blue2);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MortarRecipe register() {
            if (!validate()) return null;

            if (input.size() == 1 && generate) {
                List<Ingredient> ingredients = new ArrayList<>();
                int count = output.get(0).getCount();

                for (int i = 1; i <= 5; i++) {
                    ingredients.add(input.get(0).toMcIngredient());
                    ItemStack copy = output.get(0).copy();
                    copy.setCount(i * count);
                    MortarRecipe recipe = new MortarRecipe(copy, ingredients.toArray(new Ingredient[0]), red1, green1, blue1, red2, green2, blue2);
                    recipe.setRegistryName(new ResourceLocation(name.toString() + "_" + i));
                    ModSupport.ROOTS.get().mortar.add(recipe.getRegistryName(), recipe);
                }
                return null;
            }

            MortarRecipe recipe = new MortarRecipe(output.get(0), input.stream().map(IIngredient::toMcIngredient).toArray(Ingredient[]::new), red1, red2, green1, green2, blue1, blue2);
            recipe.setRegistryName(name);
            ModSupport.ROOTS.get().mortar.add(name, recipe);
            return recipe;

        }
    }
}
