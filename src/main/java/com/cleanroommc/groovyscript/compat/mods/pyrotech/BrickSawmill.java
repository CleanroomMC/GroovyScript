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
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickSawmillRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@RegistryDescription(
        admonition = {
                @Admonition(
                        value = "groovyscript.wiki.pyrotech.brick_sawmill.note0",
                        format = Admonition.Format.STANDARD,
                        hasTitle = true
                ),
                @Admonition(
                        value = "groovyscript.wiki.pyrotech.sawmill.note0",
                        type = Admonition.Type.WARNING,
                        format = Admonition.Format.STANDARD,
                        hasTitle = true)
        })
public class BrickSawmill extends ForgeRegistryWrapper<BrickSawmillRecipe> {

    public BrickSawmill() {
        super(ModuleTechMachine.Registries.BRICK_SAWMILL_RECIPES);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:golden_helmet')).output(item('minecraft:gold_ingot') * 2).duration(1500).woodChips(5).name('golden_helmet_recycling')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'bed_to_wool', item('minecraft:bed'), item('minecraft:wool') * 3, 500, 3"))
    public BrickSawmillRecipe add(String name, IIngredient input, ItemStack output, int duration, int woodChips) {
        return recipeBuilder()
                .duration(duration)
                .woodChips(woodChips)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'glowstone_to_dust', item('minecraft:glowstone'), item('pyrotech:sawmill_blade_stone'), item('minecraft:glowstone_dust'), 200, 0"))
    public BrickSawmillRecipe add(String name, IIngredient input, IIngredient blade, ItemStack output, int duration, int woodChips) {
        return recipeBuilder()
                .duration(duration)
                .woodChips(woodChips)
                .name(name)
                .input(input, blade)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:planks:1')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing refractory sawmill recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickSawmillRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('pyrotech:material:23')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing refractory sawmill recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (BrickSawmillRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<BrickSawmillRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int duration;
        @Property(value = "wood_chips", comp = @Comp(gte = 0))
        private int woodChips;

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

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_brick_sawmill_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Refractory Sawmill Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 2, 1, 1);
            if (input.size() > 1) {
                for (ItemStack stack : input.get(1).toMcIngredient().getMatchingStacks()) {
                    msg.add(!ArrayHelper.contains(ModuleTechMachineConfig.BRICK_SAWMILL.SAWMILL_BLADES, Objects.requireNonNull(stack.getItem().getRegistryName()).toString()), "{} is not a brick sawmill blade", stack);
                }
            }
            msg.add(duration <= 0, "duration must be a non negative integer that is larger than 0, yet it was {}", duration);
            msg.add(ModuleTechMachine.Registries.BRICK_SAWMILL_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BrickSawmillRecipe register() {
            if (!validate()) return null;
            if (input.size() > 1) {
                BrickSawmillRecipe recipe = new BrickSawmillRecipe(output.get(0), input.get(0).toMcIngredient(), duration, input.get(1).toMcIngredient(), woodChips).setRegistryName(super.name);
                ModSupport.PYROTECH.get().brickSawmill.add(recipe);
                return recipe;
            } else {
                return addMultiRecipe(output.get(0), input.get(0).toMcIngredient(), woodChips, duration, super.name);
            }
        }
    }

    protected static BrickSawmillRecipe addMultiRecipe(ItemStack output, Ingredient input, int woodChips, int duration, ResourceLocation name) {
        BrickSawmillRecipe recipe = new BrickSawmillRecipe(output, input, duration, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.STONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips).setRegistryName(name + "_tier_0");
        ModSupport.PYROTECH.get().brickSawmill.add(recipe);
        ItemStack out = output.copy();
        out.setCount(Math.min(output.getMaxStackSize(), output.getCount() * 2));
        ModSupport.PYROTECH.get().brickSawmill.add(new BrickSawmillRecipe(out, input, (int) (1.5 * (double) duration), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.FLINT_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(ModuleTechMachine.Items.BONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips / 2).setRegistryName(name + "_tier_1"));
        ModSupport.PYROTECH.get().brickSawmill.add(new BrickSawmillRecipe(out, input, (int) (0.5 * (double) duration), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.IRON_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips / 4).setRegistryName(name + "_tier_2"));
        out = output.copy();
        out.setCount(Math.min(output.getMaxStackSize(), output.getCount() * 3));
        ModSupport.PYROTECH.get().brickSawmill.add(new BrickSawmillRecipe(out, input, (int) (1.5 * (double) duration), Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.DIAMOND_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), woodChips / 4).setRegistryName(name + "_tier_3"));
        return recipe;
    }
}
