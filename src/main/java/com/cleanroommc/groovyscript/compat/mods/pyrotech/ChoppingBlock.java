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
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.ChoppingBlockRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachineConfig;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickSawmillRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneSawmillRecipe;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class ChoppingBlock extends ForgeRegistryWrapper<ChoppingBlockRecipe> {

    public ChoppingBlock() {
        super(ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechBasic.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).chops(25, 1).chops(20, 1).chops(15, 1).chops(10, 2).name('diamond_to_emerald_chopping_block')"),
            @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:gold_ingot')).inherit(true).name('iron_to_gold_chopping_block')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:log2')"))
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

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:planks', 4)"))
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

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<ChoppingBlockRecipe> {

        @Property
        private final IntList chops = new IntArrayList();
        @Property
        private final IntList quantities = new IntArrayList();
        @Property
        private boolean inherit = false;

        @RecipeBuilderMethodDescription(field = {
                "chops", "quantities"
        })
        public RecipeBuilder chops(int chops, int quantities) {
            this.chops.add(chops);
            this.quantities.add(quantities);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_chopping_block_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Chopping Block Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            msg.add(chops.stream().anyMatch(i -> i <= 0) || quantities.stream().anyMatch(i -> i <= 0), "chops and quantities must not have a negative integer value or zero");
            msg.add(ModuleTechBasic.Registries.CHOPPING_BLOCK_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
            msg.add(chops.size() != quantities.size(), "Size of chops and quantities should be equal");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable ChoppingBlockRecipe register() {
            if (!validate()) return null;
            ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(output.get(0), input.get(0).toMcIngredient(), chops.toIntArray(), quantities.toIntArray()).setRegistryName(super.name);
            ModSupport.PYROTECH.get().choppingBlock.add(recipe);
            if (inherit && ModSupport.PYROTECH.get().stoneSawmill.isEnabled()) {
                addSawmillRecipes(ModuleTechMachineConfig.STONE_SAWMILL.INHERITED_CHOPPING_BLOCK_RECIPE_DURATION_MODIFIER);
            }
            return recipe;
        }

        private void addSawmillRecipes(double durationModifier) {
            ItemStack out = output.get(0).copy();
            Ingredient in = input.get(0).toMcIngredient();
            out.setCount(!quantities.isEmpty() ? quantities.get(0) : 1);
            ResourceLocation registryName = new ResourceLocation(super.name.getNamespace(), "chopping_block/" + super.name.getPath() + "_tier_0");
            StoneSawmillRecipe recipe = (new StoneSawmillRecipe(out, in, (int) ((!chops.isEmpty() ? chops.getInt(0) * 40.0 : 240.0) * durationModifier), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.STONE_MILL_BLADE, 1, 32767)), 4)).setRegistryName(registryName);
            ModSupport.PYROTECH.get().stoneSawmill.add(recipe);
            ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(registryName));
            out = output.get(0).copy();
            out.setCount(quantities.size() > 1 ? quantities.get(1) : 2);
            registryName = new ResourceLocation(super.name.getNamespace(), "chopping_block/" + super.name.getPath() + "_tier_1");
            recipe = (new StoneSawmillRecipe(out, in, (int) ((chops.size() > 1 ? chops.getInt(1) * 40.0 : 160.0) * durationModifier), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.FLINT_MILL_BLADE, 1, 32767), new ItemStack(ModuleTechMachine.Items.BONE_MILL_BLADE, 1, 32767)), 2)).setRegistryName(registryName);
            ModSupport.PYROTECH.get().stoneSawmill.add(recipe);
            ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(registryName));
            out = output.get(0).copy();
            registryName = new ResourceLocation(super.name.getNamespace(), "chopping_block/" + super.name.getPath() + "_tier_2");
            out.setCount(quantities.size() > 2 ? quantities.get(2) : quantities.size() > 1 ? quantities.get(1) : 2);
            recipe = (new StoneSawmillRecipe(out, in, (int) ((chops.size() > 2 ? chops.getInt(2) * 60.0 : 120.0) * durationModifier), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.IRON_MILL_BLADE, 1, 32767), new ItemStack(ModuleTechMachine.Items.OBSIDIAN_MILL_BLADE, 1, 32767)), 1)).setRegistryName(registryName);
            ModSupport.PYROTECH.get().stoneSawmill.add(recipe);
            ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(registryName));
            out = output.get(0).copy();
            out.setCount(quantities.size() > 3 ? quantities.get(3) : 3);
            new ResourceLocation(super.name.getNamespace(), "chopping_block/" + super.name.getPath() + "_tier_3");
            recipe = (new StoneSawmillRecipe(out, in, (int) ((chops.size() > 3 ? chops.getInt(3) * 80.0 : 160.0) * durationModifier), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.DIAMOND_MILL_BLADE, 1, 32767)), 1)).setRegistryName(registryName);
            ModSupport.PYROTECH.get().stoneSawmill.add(recipe);
            ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(registryName));
        }
    }
}
