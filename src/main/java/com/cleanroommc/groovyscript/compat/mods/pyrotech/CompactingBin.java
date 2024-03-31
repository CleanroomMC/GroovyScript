package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompactingBinRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class CompactingBin extends ForgeRegistryWrapper<CompactingBinRecipe> {


    public CompactingBin() {
        super(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).toolUses(5).name('diamond_to_emerald_compacting_bin')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'iron_to_clay', ore('ingotIron') * 5, item('minecraft:clay_ball') * 20, 9"))
    public CompactingBinRecipe add(String name, IIngredient input, ItemStack output, int hits) {
        return recipeBuilder()
                .toolUses(hits)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:snowball')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompactingBinRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:bone_block')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CompactingBinRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<CompactingBinRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int toolUses;

        @RecipeBuilderMethodDescription
        public RecipeBuilder toolUses(int toolUses) {
            this.toolUses = toolUses;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Compacting Bin Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(toolUses < 0, "toolUses must be a non negative integer, yet it was {}", toolUses);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable CompactingBinRecipe register() {
            if (!validate()) return null;
            CompactingBinRecipe recipe = new CompactingBinRecipe(output.get(0), input.get(0).toMcIngredient(), toolUses).setRegistryName(name);
            PyroTech.compactingBin.add(recipe);
            return recipe;
        }
    }
}
