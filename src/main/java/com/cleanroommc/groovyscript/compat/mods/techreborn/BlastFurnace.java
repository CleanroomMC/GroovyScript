package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import techreborn.api.Reference;
import techreborn.api.recipe.machines.BlastFurnaceRecipe;

@RegistryDescription(admonition = @Admonition(value = "groovyscript.wiki.techreborn.blast_furnace.note0", type = Admonition.Type.INFO))
public class BlastFurnace extends AbstractGenericTechRebornRegistry {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay'), item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot')).time(10).perTick(100).neededHeat(3800)"),
            @Example(".input(item('minecraft:diamond') * 3, item('minecraft:diamond') * 2).output(item('minecraft:clay') * 2).time(5).neededHeat(1500)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public String reference() {
        return Reference.BLAST_FURNACE_RECIPE;
    }

    @Override
    @MethodDescription(example = @Example("item('techreborn:dust:1')"))
    public void removeByInput(IIngredient input) {
        super.removeByInput(input);
    }

    @Override
    @MethodDescription(example = @Example("item('techreborn:ingot:12')"))
    public void removeByOutput(IIngredient output) {
        super.removeByOutput(output);
    }

    @Property(property = "input", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    @Property(property = "output", valid = {@Comp(type = Comp.Type.GTE, value = "1"), @Comp(type = Comp.Type.LTE, value = "2")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<BlastFurnaceRecipe> {

        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int time;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE), defaultValue = "128")
        private int perTick = 128;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int neededHeat;

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
        public RecipeBuilder neededHeat(int neededHeat) {
            this.neededHeat = neededHeat;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Tech Reborn Blast Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 2);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable BlastFurnaceRecipe register() {
            if (!validate()) return null;
            ItemStack output2 = output.size() >= 2 ? output.get(1) : null;
            BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(Helper.getStackFromIIngredient(input.get(0)), input.size() == 2 ? Helper.getStackFromIIngredient(input.get(1)) : null, output.get(0), output2, time, perTick, neededHeat);
            ModSupport.TECH_REBORN.get().blastFurnace.add(recipe);
            return recipe;
        }
    }

}
