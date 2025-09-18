package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompostBinRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class CompostBin extends ForgeRegistryWrapper<CompostBinRecipe> {


    public CompostBin() {
        super(ModuleTechBasic.Registries.COMPOST_BIN_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald') * 4).compostValue(25).name('diamond_to_emerald_compost_bin')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'iron_to_clay2', ore('ingotIron') * 5, item('minecraft:clay_ball') * 20, 2"))
    public CompostBinRecipe add(String name, IIngredient input, ItemStack output, int compostValue) {
        return recipeBuilder()
                .compostValue(compostValue)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:golden_carrot')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compost bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompostBinRecipe recipe : getRegistry()) {
            if (recipe.getInput().isItemEqual(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing compost bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompostBinRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<CompostBinRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int compostValue;

        @RecipeBuilderMethodDescription
        public RecipeBuilder compostValue(int compostValue) {
            this.compostValue = compostValue;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Compost Bin Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // The composting bin won't accept any item that has a stack size of more than 1
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(compostValue < 0, "compostValue must be a non negative integer, yet it was {}", compostValue);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable CompostBinRecipe register() {
            if (!validate()) return null;
            ItemStack[] in = input.get(0).getMatchingStacks();
            if (in.length > 1) {
                int j = 1;
                for (ItemStack i : in) {
                    ResourceLocation rl = new ResourceLocation(super.name.getNamespace(), super.name.getPath() + "_" + (j++));
                    CompostBinRecipe recipe = new CompostBinRecipe(output.get(0), i, compostValue).setRegistryName(rl);
                    ModSupport.PYROTECH.get().compostBin.add(recipe);
                }
                return null;
            }
            CompostBinRecipe recipe = new CompostBinRecipe(output.get(0), in[0], compostValue).setRegistryName(super.name);
            ModSupport.PYROTECH.get().compostBin.add(recipe);
            return recipe;
        }
    }
}
