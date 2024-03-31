package com.cleanroommc.groovyscript.compat.mods.pyrotech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.CampfireRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Campfire extends ForgeRegistryWrapper<CampfireRecipe> {

    public Campfire() {
        super(ModuleTechBasic.Registries.CAMPFIRE_RECIPE);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:emerald')).duration(400).name('diamond_campfire_to_emerald')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'apple_to_dirt', item('minecraft:apple'), item('minecraft:dirt'), 1000"))
    public CampfireRecipe add(String name, IIngredient input, ItemStack output, int duration) {
        return recipeBuilder()
                .duration(duration)
                .name(name)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:porkchop')"))
    public void removeByInput(ItemStack input) {
        if (GroovyLog.msg("Error removing campfire recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CampfireRecipe recipe : getRegistry()) {
            if (recipe.getInput().test(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:cooked_porkchop')"))
    public void removeByOutput(IIngredient output) {
        if (GroovyLog.msg("Error removing campfire recipe")
                .add(IngredientHelper.isEmpty(output), () -> "Output 1 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        for (CampfireRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<CampfireRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private int duration;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int time) {
            this.duration = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Pyrotech Campfire Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(name == null, "name cannot be null.");
            msg.add(ModuleTechBasic.Registries.CAMPFIRE_RECIPE.getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable CampfireRecipe register() {
            if (!validate()) return null;
            CampfireRecipe recipe = new CampfireRecipe(output.get(0), input.get(0).toMcIngredient(), duration).setRegistryName(name);
            PyroTech.campfire.add(recipe);
            return recipe;
        }
    }
}
