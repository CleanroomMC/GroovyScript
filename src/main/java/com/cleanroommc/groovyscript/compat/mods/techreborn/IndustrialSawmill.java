package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import reborncore.api.recipe.RecipeHandler;
import techreborn.api.Reference;
import techreborn.api.recipe.machines.IndustrialSawmillRecipe;

@RegistryDescription(override = @MethodOverride(method = @MethodDescription(method = "removeByOutput", example = @Example("item('minecraft:planks:4')"))))
public class IndustrialSawmill extends AbstractGenericTechRebornRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('lava') * 100).output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay')).time(10).perTick(100)"),
            @Example(".input(item('minecraft:diamond') * 3).fluidInput(fluid('water') * 500).output(item('minecraft:clay') * 2).time(5).perTick(32)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public String reference() {
        return Reference.INDUSTRIAL_SAWMILL_RECIPE;
    }

    @Override
    @MethodDescription(example = {
            @Example("fluid('water')"),
            @Example("item('minecraft:log')")
    })
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
        RecipeHandler.recipeList.removeIf(recipe -> {
            if (recipe.getRecipeName().equals(reference()) && recipe instanceof IndustrialSawmillRecipe sawmillRecipe) {
                if (input.test(sawmillRecipe.fluidStack)) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 3))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IndustrialSawmillRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int time;
        @Property(comp = @Comp(gte = 0))
        private int perTick;
        @Property
        private boolean oreDict;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder oreDict(boolean oreDict) {
            this.oreDict = oreDict;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Industrial Sawmill recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 3);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IndustrialSawmillRecipe register() {
            if (!validate()) return null;
            ItemStack output2 = output.size() >= 2 ? output.get(1) : null;
            ItemStack output3 = output.size() >= 3 ? output.get(2) : null;
            IndustrialSawmillRecipe recipe = new IndustrialSawmillRecipe(Helper.getStackFromIIngredient(input.get(0)), fluidInput.get(0), output.get(0), output2, output3, time, perTick, oreDict);
            ModSupport.TECH_REBORN.get().industrialSawmill.add(recipe);
            return recipe;
        }
    }
}
