package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyArray;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.bloodmagic.BloodMagicRecipeRegistrarAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class AlchemyArray extends VirtualizedRegistry<RecipeAlchemyArray> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).catalyst(item('bloodmagic:slate:1')).output(item('minecraft:gold_ingot')).texture('bloodmagic:textures/models/AlchemyArrays/LightSigil.png')"),
            @Example(".input(item('minecraft:clay')).catalyst(item('minecraft:gold_ingot')).output(item('minecraft:diamond'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes()::remove);
        restoreFromBackup().forEach(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes()::add);
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.alchemy_array.add0", type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyArray add(Ingredient input, Ingredient catalyst, ItemStack output) {
        RecipeAlchemyArray recipe = new RecipeAlchemyArray(input, catalyst, output, null);
        add(recipe);
        return recipe;
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.alchemy_array.add1", type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyArray add(Ingredient input, Ingredient catalyst, ItemStack output, String circleTexture) {
        return add(input, catalyst, output, new ResourceLocation(circleTexture));
    }

    @MethodDescription(description = "groovyscript.wiki.bloodmagic.alchemy_array.add1", type = MethodDescription.Type.ADDITION)
    public RecipeAlchemyArray add(Ingredient input, Ingredient catalyst, ItemStack output, ResourceLocation circleTexture) {
        RecipeAlchemyArray recipe = new RecipeAlchemyArray(input, catalyst, output, circleTexture);
        add(recipe);
        return recipe;
    }

    public void add(RecipeAlchemyArray recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().add(recipe);
    }

    public boolean remove(RecipeAlchemyArray recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('bloodmagic:component:13')"))
    public boolean removeByInput(IIngredient input) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().removeIf(recipe -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeByCatalyst", example = @Example("item('bloodmagic:slate:2')"))
    public boolean removeByCatalyst(IIngredient catalyst) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().removeIf(recipe -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeByInputAndCatalyst", example = @Example("item('bloodmagic:component:7'), item('bloodmagic:slate:1')"))
    public boolean removeByInputAndCatalyst(IIngredient input, IIngredient catalyst) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().removeIf(recipe -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('bloodmagic:sigil_void')"))
    public boolean removeByOutput(ItemStack output) {
        if (((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().removeIf(recipe -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().forEach(this::addBackup);
        ((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<RecipeAlchemyArray> streamRecipes() {
        return new SimpleObjectStream<>(((BloodMagicRecipeRegistrarAccessor) BloodMagicAPI.INSTANCE.getRecipeRegistrar()).getAlchemyArrayRecipes())
                .setRemover(this::remove);
    }


    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeAlchemyArray> {

        @Property
        private IIngredient catalyst;
        @Property
        private ResourceLocation texture = null;

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
        public String getErrorMsg() {
            return "Error adding Blood Magic Alchemy Array recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            msg.add(catalyst == null, "Must have a catalyst ItemStack but didn't find any!");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RecipeAlchemyArray register() {
            if (!validate()) return null;
            RecipeAlchemyArray recipe = ModSupport.BLOOD_MAGIC.get().alchemyArray.add(input.get(0).toMcIngredient(), catalyst.toMcIngredient(), output.get(0), texture);
            return recipe;
        }
    }
}
