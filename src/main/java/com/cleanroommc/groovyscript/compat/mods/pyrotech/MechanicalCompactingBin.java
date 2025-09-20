package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasicConfig;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.MechanicalCompactingBinRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class MechanicalCompactingBin extends ForgeRegistryWrapper<MechanicalCompactingBinRecipe> {

    public MechanicalCompactingBin() {
        super(ModuleTechMachine.Registries.MECHANICAL_COMPACTING_BIN_RECIPES);
    }

    @RecipeBuilderDescription(example = {
            @Example(".hits(2, 2, 1, 1).input(item('minecraft:melon') * 8).output(item('minecraft:melon_block')).name('melon_compacting')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("'gold_to_wheat', ore('ingotGold') * 4, item('minecraft:wheat') * 64, 4, 4, 3, 2"),
            @Example("'wheat_to_hay_block', ore('cropWheat') * 9, item('minecraft:hay_block')")
    })
    public MechanicalCompactingBinRecipe add(String name, IIngredient input, ItemStack output, int... hits) {
        return recipeBuilder()
                .hits(hits)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:snowball')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (MechanicalCompactingBinRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:bone_block')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing compacting bin recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (MechanicalCompactingBinRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<MechanicalCompactingBinRecipe> {

        @Property
        private final IntList hits = new IntArrayList();

        @RecipeBuilderMethodDescription
        public RecipeBuilder hits(int... hits) {
            for (int hit : hits) {
                this.hits.add(hit);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Compacting Bin Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(hits.stream().anyMatch(i -> i <= 0), "hits must be a non negative integer that is larger than 0");
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable MechanicalCompactingBinRecipe register() {
            if (!validate()) return null;
            MechanicalCompactingBinRecipe recipe = new MechanicalCompactingBinRecipe(output.get(0), input.get(0).toMcIngredient(), input.get(0).getAmount(), hits.isEmpty() ? ModuleTechBasicConfig.COMPACTING_BIN.TOOL_USES_REQUIRED_PER_HARVEST_LEVEL : hits.toIntArray()).setRegistryName(super.name);
            ModSupport.PYROTECH.get().mechanicalCompactingBin.add(recipe);
            return recipe;
        }
    }
}
