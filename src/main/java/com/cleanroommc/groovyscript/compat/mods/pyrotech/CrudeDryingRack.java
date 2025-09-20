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
import com.codetaylor.mc.pyrotech.modules.tech.basic.init.recipe.DryingRackRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CrudeDryingRackRecipe;
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
public class CrudeDryingRack extends ForgeRegistryWrapper<CrudeDryingRackRecipe> {

    public CrudeDryingRack() {
        super(ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE);
    }

    @Override
    public boolean isEnabled() {
        return ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechBasic.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).dryTime(260).name('diamond_to_emerald_crude_drying_rack')"),
            @Example(".input(item('minecraft:glowstone_dust')).output(item('minecraft:redstone')).dryTime(1000).inherit(true).name('glowstone_to_redstone')")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public CrudeDryingRackRecipe add(String name, IIngredient input, ItemStack output, int dryTime) {
        return add(name, input, output, dryTime, false);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1200, true"))
    public CrudeDryingRackRecipe add(String name, IIngredient input, ItemStack output, int dryTime, boolean inherit) {
        return recipeBuilder()
                .inherit(inherit)
                .dryTime(dryTime)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:wheat')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing crude drying rack recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CrudeDryingRackRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("item('minecraft:paper')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing crude drying rack recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CrudeDryingRackRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<CrudeDryingRackRecipe> {

        @Property(comp = @Comp(gt = 0))
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
            return "Error adding Pyrotech Crude Drying Rack Recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(dryTime <= 0, "dryTime must be a non negative integer that is larger than 0, yet it was {}", dryTime);
            msg.add(super.name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.CRUDE_DRYING_RACK_RECIPE.getValue(super.name) != null, "tried to register {}, but it already exists.", super.name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable CrudeDryingRackRecipe register() {
            if (!validate()) return null;
            CrudeDryingRackRecipe recipe = new CrudeDryingRackRecipe(output.get(0), input.get(0).toMcIngredient(), dryTime).setRegistryName(super.name);
            ModSupport.PYROTECH.get().crudeDryingRack.add(recipe);
            if (inherit) {
                ResourceLocation location = new ResourceLocation(super.name.getNamespace(), "crude_drying_rack/" + super.name.getPath());
                DryingRackRecipe dryingRackRecipe = DryingRackRecipesAdd.INHERIT_TRANSFORMER.apply(recipe).setRegistryName(location);
                ModSupport.PYROTECH.get().dryingRack.add(dryingRackRecipe);
                if (ModPyrotech.INSTANCE.isModuleEnabled(ModuleTechMachine.class)) {
                    StoneOvenRecipe stoneOvenRecipe = StoneOvenRecipesAdd.INHERIT_TRANSFORMER.apply(dryingRackRecipe).setRegistryName(location);
                    ModSupport.PYROTECH.get().stoneOven.add(stoneOvenRecipe);
                    BrickOvenRecipe brickOvenRecipe = BrickOvenRecipesAdd.INHERIT_TRANSFORMER.apply(stoneOvenRecipe).setRegistryName(location);
                    ModSupport.PYROTECH.get().brickOven.add(brickOvenRecipe);
                }
            }
            return recipe;
        }
    }
}
