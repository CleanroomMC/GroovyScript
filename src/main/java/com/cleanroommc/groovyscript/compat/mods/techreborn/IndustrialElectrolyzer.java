package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import techreborn.api.Reference;
import techreborn.api.recipe.machines.IndustrialElectrolyzerRecipe;

@RegistryDescription
public class IndustrialElectrolyzer extends AbstractGenericTechRebornRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot'), item('minecraft:clay') * 5, item('minecraft:clay') * 2, item('minecraft:clay')).time(10).perTick(100)"),
            @Example(".input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2).output(item('minecraft:clay') * 2).time(5).perTick(32)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public String reference() {
        return Reference.INDUSTRIAL_ELECTROLYZER_RECIPE;
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:dye:15')"))
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
    }

    @Override
    @MethodDescription(example = @Example("item('techreborn:dust:1')"))
    public void removeByOutput(IIngredient output) {
        super.removeByOutput(output);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "4")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<IndustrialElectrolyzerRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
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
            return "Error adding Tech Reborn Industrial Electrolyzer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 4);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IndustrialElectrolyzerRecipe register() {
            if (!validate()) return null;
            ItemStack output2 = output.size() >= 2 ? output.get(1) : null;
            ItemStack output3 = output.size() >= 3 ? output.get(2) : null;
            ItemStack output4 = output.size() >= 4 ? output.get(3) : null;
            IndustrialElectrolyzerRecipe recipe = new IndustrialElectrolyzerRecipe(Helper.getStackFromIIngredient(input.get(0)), Helper.getStackFromIIngredient(input.get(1)), output.get(0), output2, output3, output4, time, perTick, oreDict);
            ModSupport.TECH_REBORN.get().industrialElectrolyzer.add(recipe);
            return recipe;
        }
    }

}
