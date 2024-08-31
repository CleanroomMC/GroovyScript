package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import techreborn.api.Reference;
import techreborn.api.recipe.machines.ImplosionCompressorRecipe;

@RegistryDescription
public class ImplosionCompressor extends AbstractGenericTechRebornRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot')).time(10).perTick(100)"),
            @Example(".input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2).output(item('minecraft:clay') * 2).time(5).perTick(32)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public String reference() {
        return Reference.IMPLOSION_COMPRESSOR_RECIPE;
    }

    @Override
    @MethodDescription(example = @Example("item('techreborn:ingot:22')"))
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
    }

    @Override
    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public void removeByOutput(IIngredient output) {
        super.removeByOutput(output);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<ImplosionCompressorRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int perTick;

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

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Implosion Compressor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 2);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ImplosionCompressorRecipe register() {
            if (!validate()) return null;
            ItemStack output2 = output.size() >= 2 ? output.get(1) : null;
            ImplosionCompressorRecipe recipe = new ImplosionCompressorRecipe(Helper.getStackFromIIngredient(input.get(0)), input.size() >= 2 ? Helper.getStackFromIIngredient(input.get(1)) : null, output.get(0), output2, time, perTick);
            ModSupport.TECH_REBORN.get().implosionCompressor.add(recipe);
            return recipe;
        }
    }

}
