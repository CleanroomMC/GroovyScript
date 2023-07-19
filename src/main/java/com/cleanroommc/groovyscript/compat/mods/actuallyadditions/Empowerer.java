package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class Empowerer extends VirtualizedRegistry<EmpowererRecipe> {

    public Empowerer() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.EMPOWERER_RECIPES::remove);
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.addAll(restoreFromBackup());
    }

    public EmpowererRecipe add(Ingredient input, ItemStack output, Ingredient modifier1, Ingredient modifier2, Ingredient modifier3, Ingredient modifier4, int energyPerStand, int time, float[] particleColor) {
        EmpowererRecipe recipe = new EmpowererRecipe(input, output, modifier1, modifier2, modifier3, modifier4, energyPerStand, time, particleColor);
        add(recipe);
        return recipe;
    }

    public void add(EmpowererRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.add(recipe);
    }

    public boolean remove(EmpowererRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        return ActuallyAdditionsAPI.EMPOWERER_RECIPES.removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.EMPOWERER_RECIPES.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    public void removeAll() {
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.EMPOWERER_RECIPES.clear();
    }

    public SimpleObjectStream<EmpowererRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.EMPOWERER_RECIPES)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<EmpowererRecipe> {

        private IIngredient mainInput;
        private int energyPerStand;
        private int time;
        private float red = 0;
        private float green = 0;
        private float blue = 0;

        public RecipeBuilder mainInput(IIngredient mainInput) {
            this.mainInput = mainInput;
            return this;
        }

        public RecipeBuilder energyPerStand(int energyPerStand) {
            this.energyPerStand = energyPerStand;
            return this;
        }

        public RecipeBuilder energy(int energy) {
            this.energyPerStand = energy;
            return this;
        }

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

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

        public RecipeBuilder color(float... color) {
            return this.particleColor(color);
        }

        public RecipeBuilder particleColor(int hex) {
            Color color = new Color(hex);
            this.red = color.getRed() / 255f;
            this.green = color.getGreen() / 255f;
            this.blue = color.getBlue() / 255f;
            return this;
        }

        public RecipeBuilder color(int hex) {
            return this.particleColor(hex);
        }

        public RecipeBuilder red(float red) {
            this.red = red;
            return this;
        }

        public RecipeBuilder green(float green) {
            this.green = green;
            return this;
        }

        public RecipeBuilder blue(float blue) {
            this.blue = blue;
            return this;
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
            msg.add(energyPerStand < 0, "energyPerStand must be a non negative integer, yet it was {}", energyPerStand);
            msg.add(time <= 0, "time must be an integer greater than 0, yet it was {}", time);
            msg.add(red < 0 || red > 1, "red must be a float between 0 and 1, yet it was {}", red);
            msg.add(green < 0 || green > 1, "green must be a float between 0 and 1, yet it was {}", green);
            msg.add(blue < 0 || blue > 1, "blue must be a float between 0 and 1, yet it was {}", blue);
        }

        @Override
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
                    new float[]{red, green, blue}
            );
            ModSupport.ACTUALLY_ADDITIONS.get().empowerer.add(recipe);
            return recipe;
        }
    }
}
