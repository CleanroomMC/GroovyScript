package com.cleanroommc.groovyscript.compat.mods.atum;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.teammetallurgy.atum.api.recipe.RecipeHandlers;
import com.teammetallurgy.atum.api.recipe.kiln.IKilnRecipe;
import com.teammetallurgy.atum.api.recipe.kiln.KilnRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Kiln extends ForgeRegistryWrapper<IKilnRecipe> {

    public Kiln() {
        super(RecipeHandlers.kilnRecipes);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4).experience(0.5f)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public IKilnRecipe add(IIngredient input, ItemStack output) {
        return add(input, output, 0);
    }

    public IKilnRecipe add(IIngredient input, ItemStack output, float experience) {
        return recipeBuilder()
                .experience(experience)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('minecraft:netherrack')"))
    public void removeByInput(IIngredient input) {
        for (IKilnRecipe recipe : getRegistry()) {
            if (recipe.getInput().stream().anyMatch(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('minecraft:stone')"))
    public void removeByOutput(IIngredient output) {
        for (IKilnRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IKilnRecipe> {

        @Property(comp = @Comp(types = Comp.Type.GTE))
        private float experience;

        @RecipeBuilderMethodDescription
        public RecipeBuilder experience(float experience) {
            this.experience = experience;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Atum 2 Kiln recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(experience < 0, "experience must be a non negative float, yet it was {}", experience);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IKilnRecipe register() {
            if (!validate()) return null;
            IKilnRecipe recipe = null;
            if (input.get(0) instanceof OreDictIngredient oreDictIngredient) {
                recipe = new KilnRecipe(oreDictIngredient.getOreDict(), output.get(0), experience);
                recipe.setRegistryName(super.name);
                ModSupport.ATUM.get().kiln.add(recipe);
                return recipe;
            } else {
                ItemStack[] matchingStacks = input.get(0).getMatchingStacks();
                for (int i = 0; i < matchingStacks.length; i++) {
                    recipe = new KilnRecipe(matchingStacks[i], output.get(0), experience);
                    var location = new ResourceLocation(super.name.getNamespace(), super.name.getPath() + i);
                    recipe.setRegistryName(location);
                    ModSupport.ATUM.get().kiln.add(recipe);
                }
            }
            return recipe;
        }
    }
}
