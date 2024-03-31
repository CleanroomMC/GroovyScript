package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CrudeDryingRackRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class CrudeDryingRack extends ForgeRegistryWrapper<CrudeDryingRackRecipe> {


    public CrudeDryingRack() {
        super(ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).dryTime(260).name('diamond_to_emerald_crude_drying_rack')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200"))
    public CrudeDryingRackRecipe add(String name, IIngredient input, ItemStack output, int dryTime) {
        return recipeBuilder()
                .dryTime(dryTime)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:wheat')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing crude drying rack recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CrudeDryingRackRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput")
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing crude drying rack recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CrudeDryingRackRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<CrudeDryingRackRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int dryTime;

        @RecipeBuilderMethodDescription
        public RecipeBuilder dryTime(int time) {
            this.dryTime = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Crude Drying Rack Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(dryTime < 0, "dryTime must be a non negative integer, yet it was {}", dryTime);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable CrudeDryingRackRecipe register() {
            if (!validate()) return null;
            CrudeDryingRackRecipe recipe = new CrudeDryingRackRecipe(output.get(0), input.get(0).toMcIngredient(), dryTime).setRegistryName(name);
            PyroTech.crudeDryingRack.add(recipe);
            return recipe;
        }
    }
}
