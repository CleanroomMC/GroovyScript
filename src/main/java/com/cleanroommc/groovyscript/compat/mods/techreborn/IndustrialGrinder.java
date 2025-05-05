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
import techreborn.api.recipe.machines.IndustrialGrinderRecipe;

@RegistryDescription(override = @MethodOverride(method = @MethodDescription(method = "removeByOutput", example = @Example("item('techreborn:dust:53')"))))
public class IndustrialGrinder extends AbstractGenericTechRebornRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidInput(fluid('lava') * 50).output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay') * 2, item('minecraft:clay')).time(10).perTick(100)"),
            @Example(".input(item('minecraft:diamond') * 3).fluidInput(fluid('water') * 250).output(item('minecraft:clay') * 2).time(5).perTick(32)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public String reference() {
        return Reference.INDUSTRIAL_GRINDER_RECIPE;
    }

    @Override
    @MethodDescription(example = {
            @Example("fluid('water')"),
            @Example("item('techreborn:ore2')")
    })
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
        RecipeHandler.recipeList.removeIf(recipe -> {
            if (recipe.getRecipeName().equals(reference()) && recipe instanceof IndustrialGrinderRecipe grinderRecipe) {
                if (input.test(grinderRecipe.fluidStack)) {
                    addBackup(recipe);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(gte = 1, lte = 4))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IndustrialGrinderRecipe> {

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
            return "Error adding Tech Reborn Industrial Grinder recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 4);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IndustrialGrinderRecipe register() {
            if (!validate()) return null;
            ItemStack output2 = output.size() >= 2 ? output.get(1) : null;
            ItemStack output3 = output.size() >= 3 ? output.get(2) : null;
            ItemStack output4 = output.size() >= 4 ? output.get(3) : null;
            IndustrialGrinderRecipe recipe = new IndustrialGrinderRecipe(Helper.getStackFromIIngredient(input.get(0)), fluidInput.get(0), output.get(0), output2, output3, output4, time, perTick, oreDict);
            ModSupport.TECH_REBORN.get().industrialGrinder.add(recipe);
            return recipe;
        }
    }
}
