package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.athenaeum.util.ArrayHelper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachineConfig;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickSawmillRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneSawmillRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@RegistryDescription(
        admonition = {
                @Admonition(
                        value = "groovyscript.wiki.pyrotech.stone_sawmill.note0",
                        type = Admonition.Type.WARNING,
                        format = Admonition.Format.STANDARD,
                        hasTitle = true
                ),
                @Admonition(
                        value = "groovyscript.wiki.pyrotech.sawmill.note0",
                        type = Admonition.Type.WARNING,
                        format = Admonition.Format.STANDARD,
                        hasTitle = true)
        })
public class StoneSawmill extends ForgeRegistryWrapper<StoneSawmillRecipe> {

    public StoneSawmill() {
        super(ModuleTechMachine.Registries.STONE_SAWMILL_RECIPES);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:sign')).output(item('minecraft:planks:0') * 2).duration(200).woodChips(5).inherit(true).name('wood_from_sign')"),
            @Example(".input(item('minecraft:stone_pickaxe'), item('pyrotech:sawmill_blade_bone')).output(item('minecraft:iron_pickaxe')).duration(5000).name('stone_pickaxe_upgrade')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public StoneSawmillRecipe add(String name, IIngredient input, ItemStack output, int duration, int woodChips) {
        return add(name, input, output, duration, woodChips, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.stone_sawmill.add.inherit", example = @Example("'stone_to_cobblestone', ore('stone'), item('minecraft:cobblestone'), 500, 0, true"))
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

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public StoneSawmillRecipe add(String name, IIngredient input, IIngredient blade, ItemStack output, int duration, int woodChips) {
        return add(name, input, blade, output, duration, woodChips, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.stone_sawmill.add.inherit", example = @Example("'apple_to_gapple_with_golden_blade', item('minecraft:apple'), item('pyrotech:sawmill_blade_bone'), item('minecraft:golden_apple'), 2000, 0, false"))
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

    @Property(property = "input", comp = @Comp(gte = 1, lte = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneSawmillRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int duration;
        @Property(value = "wood_chips", comp = @Comp(gte = 0))
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
        public String getRecipeNamePrefix() {
            return "groovyscript_stone_sawmill_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Stone Sawmill Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 2, 1, 1);
            if (input.size() > 1) {
                for (ItemStack stack : input.get(1).toMcIngredient().getMatchingStacks()) {
                    msg.add(!ArrayHelper.contains(ModuleTechMachineConfig.STONE_SAWMILL.SAWMILL_BLADES, Objects.requireNonNull(stack.getItem().getRegistryName()).toString()), "{} is not a stone sawmill blade", stack);
                }
            }
            msg.add(duration <= 0, "duration must be a non negative integer that is larger than 0, yet it was {}", duration);
            msg.add(ModuleTechMachine.Registries.STONE_SAWMILL_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable StoneSawmillRecipe register() {
            if (!validate()) return null;
            if (input.size() > 1) {
                StoneSawmillRecipe recipe = new StoneSawmillRecipe(output.get(0), input.get(0).toMcIngredient(), duration, input.get(1).toMcIngredient(), woodChips).setRegistryName(super.name);
                ModSupport.PYROTECH.get().stoneSawmill.add(recipe);
                if (inherit) {
                    ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(new ResourceLocation(super.name.getNamespace(), "stone_sawmill/" + super.name.getPath())));
                }
                return recipe;
            } else {
                StoneSawmillRecipe[] recipes = new StoneSawmillRecipe[4];
                ItemStack out = output.get(0);
                Ingredient in = input.get(0).toMcIngredient();
                StoneSawmillRecipe recipe = new StoneSawmillRecipe(out, in, duration, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.STONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips).setRegistryName(name + "_tier_0");
                recipes[0] = recipe;
                out = out.copy();
                out.setCount(Math.min(out.getMaxStackSize(), out.getCount() * 2));
                recipes[1] = new StoneSawmillRecipe(out, in, (int) (1.5 * (double) duration), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.FLINT_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ModuleTechMachine.Items.BONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips / 2).setRegistryName(name + "_tier_1");
                recipes[2] = new StoneSawmillRecipe(out, in, (int) (0.5 * (double) duration), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.IRON_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips / 4).setRegistryName(name + "_tier_2");
                out = out.copy();
                out.setCount(Math.min(out.getMaxStackSize(), out.getCount() * 3));
                recipes[3] = new StoneSawmillRecipe(out, in, (int) (1.5 * (double) duration), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.DIAMOND_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips / 4).setRegistryName(name + "_tier_3");
                for (StoneSawmillRecipe r : recipes) {
                    ModSupport.PYROTECH.get().stoneSawmill.add(r);
                    if (inherit) {
                        ModSupport.PYROTECH.get().brickSawmill.add(BrickSawmillRecipesAdd.INHERIT_TRANSFORMER.apply(r).setRegistryName(new ResourceLocation(super.name.getNamespace(), "stone_sawmill/" + super.name.getPath())));
                    }
                }
                return recipe;
            }
        }
    }
}
