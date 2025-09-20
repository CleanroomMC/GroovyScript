package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickSawmillRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

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

    @Property(property = "input", comp = @Comp(gte = 1, lt = 3))
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
        public String getErrorMsg() {
            return "Error adding Pyrotech Refractory Sawmill Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 2, 1, 1);
            msg.add(duration <= 0, "duration must be a non negative integer that is larger than 0, yet it was {}", duration);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechMachine.Registries.BRICK_SAWMILL_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable BrickSawmillRecipe register() {
            if (!validate()) return null;
            if (input.size() > 1) {
                return this.addRecipe(1, 1, input.get(1).toMcIngredient(), 1, "");
            }
            else {
                BrickSawmillRecipe recipe = this.addRecipe(1, 1, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.STONE_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 1, "");
                this.addRecipe(2, 3.0 / 2.0, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.FLINT_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 2, "_flint_blade");
                this.addRecipe(2, 0.5, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.IRON_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 4, "_iron_blade");
                this.addRecipe(3, 3.0 / 2.0, Ingredient.fromStacks(new ItemStack(ModuleTechMachine.Items.DIAMOND_MILL_BLADE, 1, OreDictionary.WILDCARD_VALUE)), 4, "_diamond_blade");
                return recipe;
            }
        }

        private BrickSawmillRecipe addRecipe(int outputMultiplier, double durationMultiplier, Ingredient blade, int woodChipsDivisor, String name) {
            ItemStack out = output.get(0).copy();
            out.setCount(Math.min(output.get(0).getCount() * outputMultiplier, 64));
            BrickSawmillRecipe recipe = new BrickSawmillRecipe(out, input.get(0).toMcIngredient(), (int) ((double) duration * durationMultiplier), blade, woodChips / woodChipsDivisor).setRegistryName(new ResourceLocation(super.name.getNamespace(), super.name.getPath() + name));
            ModSupport.PYROTECH.get().brickSawmill.add(recipe);
            return recipe;
        }
    }
}
