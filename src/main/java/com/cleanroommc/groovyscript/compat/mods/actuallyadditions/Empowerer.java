package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;

@RegistryDescription
public class Empowerer extends StandardListRegistry<EmpowererRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".mainInput(item('minecraft:clay')).input(item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay')).output(item('minecraft:diamond')).time(50).energy(1000).red(0.5).green(0.3).blue(0.2)"),
            @Example(".mainInput(item('minecraft:clay')).input(item('minecraft:diamond'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay')).output(item('minecraft:diamond') * 2).time(50).color(0.5, 0.3, 0.2)"),
            @Example(".mainInput(item('minecraft:diamond')).input(item('minecraft:diamond'),item('minecraft:gold_ingot'),item('minecraft:diamond'),item('minecraft:gold_ingot')).output(item('minecraft:dirt') * 8).time(50).particleColor(0x00FF88)"),
            @Example(".input(item('minecraft:gold_ingot'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay'),item('minecraft:clay')).output(item('minecraft:diamond')).time(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<EmpowererRecipe> getRecipes() {
        return ActuallyAdditionsAPI.EMPOWERER_RECIPES;
    }

    public EmpowererRecipe add(Ingredient input, ItemStack output, Ingredient modifier1, Ingredient modifier2, Ingredient modifier3, Ingredient modifier4, int energyPerStand, int time, float[] particleColor) {
        EmpowererRecipe recipe = new EmpowererRecipe(input, output, modifier1, modifier2, modifier3, modifier4, energyPerStand, time, particleColor);
        add(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("item('actuallyadditions:item_crystal')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(example = @Example("item('actuallyadditions:item_misc:24')"))
    public boolean removeByOutput(ItemStack output) {
        return getRecipes().removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 4, lte = 5))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<EmpowererRecipe> {

        @Property
        private IIngredient mainInput;
        @Property(comp = @Comp(gte = 0))
        private int energyPerStand;
        @Property(comp = @Comp(gt = 0))
        private int time;
        @Property(comp = @Comp(gte = 0, lte = 1))
        private float red;
        @Property(comp = @Comp(gte = 0, lte = 1))
        private float green;
        @Property(comp = @Comp(gte = 0, lte = 1))
        private float blue;

        @RecipeBuilderMethodDescription
        public RecipeBuilder mainInput(IIngredient mainInput) {
            this.mainInput = mainInput;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energyPerStand(int energyPerStand) {
            this.energyPerStand = energyPerStand;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "energyPerStand")
        public RecipeBuilder energy(int energy) {
            this.energyPerStand = energy;
            return this;
        }


        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "red", "green", "blue"
        })
        public RecipeBuilder particleColor(float... color) {
            if (color.length != 3) {
                GroovyLog.get().warn("Error setting color in Actually Additions Empowerer recipe. color must contain 3 floats, yet it contained {}", color.length);
                return this;
            }
            this.red = color[0];
            this.green = color[1];
            this.blue = color[2];
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "red", "green", "blue"
        })
        public RecipeBuilder color(float... color) {
            return this.particleColor(color);
        }


        @RecipeBuilderMethodDescription(field = {
                "red", "green", "blue"
        })
        public RecipeBuilder particleColor(int hex) {
            Color color = new Color(hex);
            this.red = color.getRed() / 255.0f;
            this.green = color.getGreen() / 255.0f;
            this.blue = color.getBlue() / 255.0f;
            return this;
        }


        @RecipeBuilderMethodDescription(field = {
                "red", "green", "blue"
        })
        public RecipeBuilder color(int hex) {
            return this.particleColor(hex);
        }

        @RecipeBuilderMethodDescription(priority = 100)
        public RecipeBuilder red(float red) {
            this.red = red;
            return this;
        }

        @RecipeBuilderMethodDescription(priority = 100)
        public RecipeBuilder green(float green) {
            this.green = green;
            return this;
        }

        @RecipeBuilderMethodDescription(priority = 100)
        public RecipeBuilder blue(float blue) {
            this.blue = blue;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed on the Empowerer or Display Stands
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Empowerer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            if (input.size() == 5 && mainInput == null) {
                mainInput = input.remove(0);
            }
            validateItems(msg, 4, 4, 1, 1);
            validateFluids(msg);
            msg.add(mainInput == null, "mainInput must be defined");
            validateStackSize(msg, 1, "mainInput", mainInput);
            msg.add(energyPerStand < 0, "energyPerStand must be a non negative integer, yet it was {}", energyPerStand);
            msg.add(time <= 0, "time must be an integer greater than 0, yet it was {}", time);
            msg.add(red < 0 || red > 1, "red must be a float between 0 and 1, yet it was {}", red);
            msg.add(green < 0 || green > 1, "green must be a float between 0 and 1, yet it was {}", green);
            msg.add(blue < 0 || blue > 1, "blue must be a float between 0 and 1, yet it was {}", blue);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable EmpowererRecipe register() {
            if (!validate()) return null;
            EmpowererRecipe recipe = new EmpowererRecipe(
                    mainInput.toMcIngredient(),
                    output.get(0),
                    input.get(0).toMcIngredient(),
                    input.get(1).toMcIngredient(),
                    input.get(2).toMcIngredient(),
                    input.get(3).toMcIngredient(),
                    energyPerStand,
                    time,
                    new float[]{
                            red, green, blue
                    }
            );
            ModSupport.ACTUALLY_ADDITIONS.get().empowerer.add(recipe);
            return recipe;
        }
    }
}
