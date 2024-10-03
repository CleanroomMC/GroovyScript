package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import essentialcraft.api.MithrilineFurnaceRecipe;
import essentialcraft.api.MithrilineFurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RegistryDescription
public class MithrilineFurnace extends VirtualizedRegistry<MithrilineFurnaceRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:coal_block') * 3).output(item('minecraft:diamond_block')).espe(500)"))
    public MithrilineFurnace.RecipeBuilder recipeBuilder() {
        return new MithrilineFurnace.RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(MithrilineFurnaceRecipes::removeRecipe);
        restoreFromBackup().forEach(MithrilineFurnaceRecipes::addRecipe);
    }

    @MethodDescription(example = @Example("ore('dustGlowstone')"))
    public boolean removeByInput(IIngredient x) {
        return MithrilineFurnaceRecipes.RECIPES.removeIf(r -> {
            if (Arrays.stream(x.getMatchingStacks()).anyMatch(r.input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:emerald')"))
    public boolean removeByOutput(IIngredient x) {
        return MithrilineFurnaceRecipes.RECIPES.removeIf(r -> {
            if (x.test(r.result)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        MithrilineFurnaceRecipes.RECIPES.forEach(this::addBackup);
        MithrilineFurnaceRecipes.RECIPES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<MithrilineFurnaceRecipe> streamRecipes() {
        return new SimpleObjectStream<>(MithrilineFurnaceRecipes.RECIPES).setRemover(r -> {
            addBackup(r);
            return MithrilineFurnaceRecipes.RECIPES.remove(r);
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MithrilineFurnaceRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int espe;

        @RecipeBuilderMethodDescription
        public RecipeBuilder espe(int cost) {
            espe = cost;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mithriline Furnace Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(espe < 1, "espe cost must be 1 or greater, got {}", espe);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable MithrilineFurnaceRecipe register() {
            if (!validate()) return null;
            int stackSize = input.get(0).getAmount();
            Ingredient inputItem = input.get(0).withAmount(1).toMcIngredient();
            MithrilineFurnaceRecipe recipe = new MithrilineFurnaceRecipe(inputItem, output.get(0), (float) espe, stackSize);
            ModSupport.ESSENTIALCRAFT.get().mithrilineFurnace.addScripted(recipe);
            MithrilineFurnaceRecipes.addRecipe(recipe);
            return recipe;
        }

    }

}
