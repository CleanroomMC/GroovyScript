package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyArray;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicRecipeRegistrarAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class AlchemyArray extends StandardListRegistry<RecipeAlchemyArray> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).catalyst(item('bloodmagic:slate:1')).output(item('minecraft:gold_ingot')).texture('bloodmagic:textures/models/AlchemyArrays/LightSigil.png')"),
            @Example(".input(item('minecraft:clay')).catalyst(item('minecraft:gold_ingot')).output(item('minecraft:diamond'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RecipeAlchemyArray> getRecipes() {
        return ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes();
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.alchemy_array.add0", type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyArray add(IIngredient input, IIngredient catalyst, ItemStack output) {
        return recipeBuilder()
                .catalyst(catalyst)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.alchemy_array.add1", type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyArray add(IIngredient input, IIngredient catalyst, ItemStack output, String circleTexture) {
        return add(input, catalyst, output, new ResourceLocation(circleTexture));
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.alchemy_array.add1", type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyArray add(IIngredient input, IIngredient catalyst, ItemStack output, ResourceLocation circleTexture) {
        return recipeBuilder()
                .catalyst(catalyst)
                .texture(circleTexture)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('bloodmagic:component:13')"))
    public boolean removeByInput(IIngredient input) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Alchemy Array recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("item('bloodmagic:slate:2')"))
    public boolean removeByCatalyst(IIngredient catalyst) {
        if (getRecipes().removeIf(recipe -> {
            boolean found = recipe.getCatalyst().test(IngredientHelper.toItemStack(catalyst));
            if (found) {
                addBackup(recipe);
            }
            return found;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Alchemy Array recipe")
                .add("could not find recipe with catalyst {}", catalyst)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("item('bloodmagic:component:7'), item('bloodmagic:slate:1')"))
    public boolean removeByInputAndCatalyst(IIngredient input, IIngredient catalyst) {
        if (getRecipes().removeIf(recipe -> {
            boolean removeRecipe = recipe.getInput().test(IngredientHelper.toItemStack(input)) && recipe.getCatalyst().test(IngredientHelper.toItemStack(catalyst));
            if (removeRecipe) {
                addBackup(recipe);
            }
            return removeRecipe;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Alchemy Array recipe")
                .add("could not find recipe with input {} and catalyst {}", input, catalyst)
                .error()
                .post();
        return false;
    }

    @MethodDescription(example = @Example("item('bloodmagic:sigil_void')"))
    public boolean removeByOutput(ItemStack output) {
        if (getRecipes().removeIf(recipe -> {
            boolean matches = recipe.getOutput().isItemEqual(output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        })) {
            return true;
        }

        GroovyLog.msg("Error removing Blood Magic Alchemy Array recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeAlchemyArray> {

        @Property
        private IIngredient catalyst;
        @Property
        private ResourceLocation texture;

        @RecipeBuilderMethodDescription
        public RecipeBuilder catalyst(IIngredient catalyst) {
            this.catalyst = catalyst;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder texture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder texture(String texture) {
            this.texture = new ResourceLocation(texture);
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed in the Ashes's slots
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Blood Magic Alchemy Array recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(catalyst == null, "Must have a catalyst ItemStack but didn't find any!");
            validateStackSize(msg, 1, "catalyst", catalyst);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeAlchemyArray register() {
            if (!validate()) return null;
            RecipeAlchemyArray recipe = new RecipeAlchemyArray(input.get(0).toMcIngredient(), catalyst.toMcIngredient(), output.get(0), texture);
            ModSupport.BLOOD_MAGIC.get().alchemyArray.add(recipe);
            return recipe;
        }
    }
}
