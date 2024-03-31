package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.ChoppingBlockRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class ChoppingBlock extends ForgeRegistryWrapper<ChoppingBlockRecipe> {

    public ChoppingBlock() {
        super(ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE);
    }


    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).chops(25, 1).chops(20, 1).chops(15, 1).chops(10, 2).name('diamond_to_emerald_chopping_block')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:log2')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing chopping block recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (ChoppingBlockRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:planks', 4)"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing chopping block recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (ChoppingBlockRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<ChoppingBlockRecipe> {

        @Property
        private final IntList chops = new IntArrayList();
        private final IntList quantities = new IntArrayList();

        @RecipeBuilderMethodDescription(field = "chops, quantities")
        public RecipeBuilder chops(int chops, int quantities) {
            this.chops.add(chops);
            this.quantities.add(quantities);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Chopping Block Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(quantities.isEmpty(), "cops and quantities must be a non negative integer, yet it was {}", quantities.size());
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable ChoppingBlockRecipe register() {
            if (!validate()) return null;
            ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(output.get(0), input.get(0).toMcIngredient(), chops.toIntArray(), quantities.toIntArray()).setRegistryName(name);
            PyroTech.choppingBlock.add(recipe);
            return recipe;
        }
    }
}
