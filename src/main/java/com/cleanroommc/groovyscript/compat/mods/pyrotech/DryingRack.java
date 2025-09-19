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
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.DryingRackRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickOvenRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.StoneOvenRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.BrickOvenRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneOvenRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class DryingRack extends ForgeRegistryWrapper<DryingRackRecipe> {


    public DryingRack() {
        super(ModuleTechBasic.Registries.DRYING_RACK_RECIPE);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:iron_ingot')).output(item('minecraft:gold_ingot')).dryTime(260).name('iron_to_gold_drying_rack')"),
            @Example(".input(item('minecraft:ender_eye')).output(item('minecraft:ender_pearl')).dryTime(500).inherit(true).name('ender_eye_to_ender_pearl')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public DryingRackRecipe add(String name, IIngredient input, ItemStack output, int dryTime) {
        return add(name, input, output, dryTime, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, true"))
    public DryingRackRecipe add(String name, IIngredient input, ItemStack output, int dryTime, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .dryTime(dryTime)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:wheat')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing drying rack recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (DryingRackRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:sponge')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing drying rack recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (DryingRackRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<DryingRackRecipe> {

        @Property(comp = @Comp(gte = 1))
        private int dryTime;
        @Property
        private boolean inherit;

        @RecipeBuilderMethodDescription
        public RecipeBuilder dryTime(int time) {
            this.dryTime = time;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inherit(boolean inherit) {
            this.inherit = inherit;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Drying Rack Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed in each slot
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(dryTime < 0, "dryTime must be a non negative integer, yet it was {}", dryTime);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.DRYING_RACK_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable DryingRackRecipe register() {
            if (!validate()) return null;
            DryingRackRecipe recipe = new DryingRackRecipe(output.get(0), input.get(0).toMcIngredient(), dryTime).setRegistryName(super.name);
            ModSupport.PYROTECH.get().dryingRack.add(recipe);
            if (inherit && ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class)) {
                ResourceLocation location = new ResourceLocation(super.name.getNamespace(), "drying_rack/" + super.name.getPath());
                StoneOvenRecipe stoneOvenRecipe = StoneOvenRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(location);
                ModSupport.PYROTECH.get().stoneOven.add(stoneOvenRecipe);
                BrickOvenRecipe brickOvenRecipe = BrickOvenRecipesAdd.INHERIT_TRANSFORMER.apply(stoneOvenRecipe).setRegistryName(location);
                ModSupport.PYROTECH.get().brickOven.add(brickOvenRecipe);
            }
            return recipe;
        }
    }
}
