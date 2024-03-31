package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.TanningRackRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class TanningRack extends ForgeRegistryWrapper<TanningRackRecipe> {


    public TanningRack() {
        super(ModuleTechBasic.Registries.TANNING_RACK_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:gold_ingot')).dryTime(260).name('iron_to_gold_drying_rack')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, item('minecraft:clay_ball')"))
    public TanningRackRecipe add(String name, IIngredient input, ItemStack output, int dryTime, ItemStack failureItem) {
        return recipeBuilder()
                .dryTime(dryTime)
                .failureItem(failureItem)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:wheat')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing tanning rack recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (TanningRackRecipe recipe : getRegistry()) {
            if (recipe.getInputItem().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput")
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing tanning rack recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (TanningRackRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<TanningRackRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int dryTime;
        @Property
        private ItemStack failureItem;

        @RecipeBuilderMethodDescription
        public RecipeBuilder failureItem(ItemStack stack) {
            this.failureItem = stack;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder dryTime(int time) {
            this.dryTime = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Tanning Rack Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(dryTime < 0, "dryTime must be a non negative integer, yet it was {}", dryTime);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.TANNING_RACK_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod

        @Override
        public @Nullable TanningRackRecipe register() {
            if (!validate()) return null;
            TanningRackRecipe recipe = new TanningRackRecipe(output.get(0), input.get(0).toMcIngredient(), failureItem, dryTime).setRegistryName(name);
            PyroTech.tanningRack.add(recipe);
            return recipe;
        }
    }
}
