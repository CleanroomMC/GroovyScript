package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasicConfig;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CompactingBinRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.MechanicalCompactingBinRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.MechanicalCompactingBinRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class CompactingBin extends ForgeRegistryWrapper<CompactingBinRecipe> {


    public CompactingBin() {
        super(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).hits(5, 4, 3, 2).inherit(true).name('diamond_to_emerald')"),
            @Example(".input(item('minecraft:slime_ball') * 9).output(item('minecraft:slime')).name('slime_compacting')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public CompactingBinRecipe add(String name, IIngredient input, ItemStack output, int... hits) {
        return add(name, input, output, false, hits);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'iron_to_clay', ore('ingotIron') * 5, item('minecraft:clay_ball') * 20, false, 9, 7, 6, 6"))
    public CompactingBinRecipe add(String name, IIngredient input, ItemStack output, boolean inherit, int... hits) {
        return recipeBuilder()
                .inherit(inherit)
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
        for (CompactingBinRecipe recipe : getRegistry()) {
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
        for (CompactingBinRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<CompactingBinRecipe> {

        @Property
        private final IntList hits = new IntArrayList();
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder hits(int... hits) {
            for (int use : hits) {
                this.hits.add(use);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Compacting Bin Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(hits.stream().anyMatch(i -> i <= 0), "hits must be a non negative integer that's larger than 0");
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.COMPACTING_BIN_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable CompactingBinRecipe register() {
            if (!validate()) return null;
            CompactingBinRecipe recipe = new CompactingBinRecipe(output.get(0), input.get(0).toMcIngredient(), input.get(0).getAmount(), hits.isEmpty() ? ModuleTechBasicConfig.COMPACTING_BIN.TOOL_USES_REQUIRED_PER_HARVEST_LEVEL : hits.toIntArray()).setRegistryName(super.name);
            input.get(0).setAmount(1);
            ModSupport.PYROTECH.get().compactingBin.add(recipe);
            if (inherit && ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class)) {
                MechanicalCompactingBinRecipe mechanicalCompactingBinRecipe = MechanicalCompactingBinRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(super.name.getNamespace(), "compacting_bin/" + super.name.getPath());
                ModSupport.PYROTECH.get().mechanicalCompactingBin.add(mechanicalCompactingBinRecipe);
            }
            return recipe;
        }
    }
}
