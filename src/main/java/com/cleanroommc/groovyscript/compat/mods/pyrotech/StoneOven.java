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
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickOvenRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickOvenRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneOvenRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = {
                @Admonition(
                        value = "groovyscript.wiki.pyrotech.stone_oven.note0",
                        format = Admonition.Format.STANDARD,
                        hasTitle = true
                ),
                @Admonition(
                        value = "groovyscript.wiki.pyrotech.oven.note0",
                        type = Admonition.Type.WARNING,
                        format = Admonition.Format.STANDARD,
                        hasTitle = true)
        })
public class StoneOven extends ForgeRegistryWrapper<StoneOvenRecipe> {

    public StoneOven() {
        super(ModuleTechMachine.Registries.STONE_OVEN_RECIPES);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).duration(400).inherit(true).name('diamond_campfire_to_emerald_stone')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public StoneOvenRecipe add(String name, IIngredient input, ItemStack output, int duration) {
        return add(name, input, output, duration, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.pyrotech.stone_oven.add.inherit", example = @Example("'sand_to_dirt', item('minecraft:sand'), item('minecraft:dirt'), 1000, true"))
    public StoneOvenRecipe add(String name, IIngredient input, ItemStack output, int duration, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .duration(duration)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:porkchop')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing stone oven recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneOvenRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:cooked_porkchop')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing stone oven recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (StoneOvenRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneOvenRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int duration;
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_stone_oven_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Stone Oven Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            msg.add(duration <= 0, "duration must be a non negative integer that is larger than 0, yet it was {}", duration);
            msg.add(ModuleTechMachine.Registries.STONE_OVEN_RECIPES.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable StoneOvenRecipe register() {
            if (!validate()) return null;
            StoneOvenRecipe recipe = new StoneOvenRecipe(output.get(0), input.get(0).toMcIngredient(), duration).setRegistryName(super.name);
            ModSupport.PYROTECH.get().stoneOven.add(recipe);
            if (inherit) {
                ResourceLocation location = new ResourceLocation(super.name.getNamespace(), "stone_oven/" + super.name.getPath());
                BrickOvenRecipe brickOvenRecipe = BrickOvenRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(location);
                ModSupport.PYROTECH.get().brickOven.add(brickOvenRecipe);
            }
            return recipe;
        }
    }
}
