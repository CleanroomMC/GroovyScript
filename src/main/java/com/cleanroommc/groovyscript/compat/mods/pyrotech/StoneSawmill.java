package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickSawmillRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneSawmillRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = @Admonition(
                value = "groovyscript.wiki.pyrotech.sawmill.note0",
                type = Admonition.Type.WARNING,
                format = Admonition.Format.STANDARD,
                hasTitle = true))
public class StoneSawmill extends ForgeRegistryWrapper<StoneSawmillRecipe> {

    public StoneSawmill() {
        super(ModuleTechMachine.Registries.STONE_SAWMILL_RECIPES);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:sign')).output(item('minecraft:planks:0') * 2).duration(200).woodChips(5).inherit(true).name('wood_from_sign')"),
            @Example(".input(item('minecraft:stone_pickaxe'), ore('blockIron')).output(item('minecraft:iron_pickaxe')).duration(5000).name('stone_pickaxe_upgrade')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public StoneSawmillRecipe add(String name, IIngredient input, ItemStack output, int duration, int woodChips) {
        return add(name, input, output, duration, woodChips, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'stone_to_cobblestone', ore('stone'), item('minecraft:cobblestone'), 500, 0, true"))
    public StoneSawmillRecipe add(String name, IIngredient input, ItemStack output, int duration, int woodChips, boolean inherit) {
        return recipeBuilder()
                .duration(duration)
                .woodChips(woodChips)
                .inherit(inherit)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    public StoneSawmillRecipe add(String name, IIngredient input, IIngredient blade, ItemStack output, int duration, int woodChips) {
        return add(name, input, blade, output, duration, woodChips, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_gapple_with_golden_blade', item('minecraft:apple'), item('pyrotech:sawmill_blade_bone'), item('minecraft:golden_apple'), 2000, 0, false"))
    public StoneSawmillRecipe add(String name, IIngredient input, IIngredient blade, ItemStack output, int duration, int woodChips, boolean inherit) {
        return recipeBuilder()
                .duration(duration)
                .woodChips(woodChips)
                .inherit(inherit)
                .name(name)
                .input(input, blade)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:planks:1')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing stone sawmill recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneSawmillRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('pyrotech:material:23')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing stone sawmill recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneSawmillRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(gte = 1, lt = 3))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneSawmillRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int duration;
        @Property(comp = @Comp(gte = 0))
        private int woodChips;
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder woodChips(int woodChips) {
            this.woodChips = woodChips;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Stone Sawmill Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 1);
            msg.add(duration <= 0, "duration must be a non negative integer that is larger than 0, yet it was {}", duration);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechMachine.Registries.STONE_SAWMILL_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable StoneSawmillRecipe register() {
            if (!validate()) return null;
            if (input.size() > 1) {
                return this.addRecipe(1, 1, input.get(1).toMcIngredient(), 1, "");
            }
            else {
                StoneSawmillRecipe recipe = this.addRecipe(1, 1, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.STONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 1, "");
                this.addRecipe(2, 3.0 / 2.0, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.FLINT_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ModuleTechMachine.Items.BONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 2, "_flint_blade");
                this.addRecipe(2, 0.5, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.IRON_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 4, "_iron_blade");
                this.addRecipe(3, 3.0 / 2.0, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.DIAMOND_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 4, "_diamond_blade");
                return recipe;
            }
        }

        private StoneSawmillRecipe addRecipe(int outputMultiplier, double durationMultiplier, Ingredient blade, int woodChipsDivisor, String name) {
            ItemStack out = output.get(0).copy();
            out.setCount(Math.min(output.get(0).getCount() * outputMultiplier, 64));
            ResourceLocation registryName = new ResourceLocation(super.name.getNamespace(), super.name.getPath() + name);
            StoneSawmillRecipe recipe = new StoneSawmillRecipe(out, input.get(0).toMcIngredient(), (int) ((double) duration * durationMultiplier), blade, woodChips / woodChipsDivisor).setRegistryName(registryName);
            ModSupport.PYROTECH.get().stoneSawmill.add(recipe);
            if (inherit) {
                ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(new ResourceLocation(registryName.getNamespace(), "stone_sawmill/" + registryName.getPath())));
            }
            return recipe;
        }
    }
}
