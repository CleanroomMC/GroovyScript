package com.cleanroommc.groovyscript.compat.mods.inspirations;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.inspirations.InspirationsRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.*;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.RecipeMatch;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class Cauldron extends VirtualizedRegistry<ICauldronRecipe> {

    private static boolean checkRecipeMatches(ISimpleCauldronRecipe recipe, IIngredient input, ItemStack output, Object inputState, Object outputState) {
        // Check all relevant parts to determine if we need to match them and if they do match.
        return (inputState == null || compareFluids(inputState, recipe.getInputState()))
               && (outputState == null || compareFluids(outputState, recipe.getState()))
               && (output == null || output.isItemEqual(recipe.getResult()))
               && (input == null || recipe.getInput().stream().anyMatch(input));
    }

    private static boolean compareFluids(Object first, Object second) {
        if (first == second) return true;
        if (first instanceof Fluid && second instanceof Fluid) return ((Fluid) first).getName().equals(((Fluid) second).getName());
        return false;
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public RecipeBuilder recipeBuilderStandard() {
        return new RecipeBuilder().standard();
    }

    public RecipeBuilder recipeBuilderTransform() {
        return new RecipeBuilder().transform();
    }

    public RecipeBuilder recipeBuilderMix() {
        return new RecipeBuilder().mix();
    }

    public RecipeBuilder recipeBuilderFill() {
        return new RecipeBuilder().fill();
    }

    public RecipeBuilder recipeBuilderBrewing() {
        return new RecipeBuilder().brewing();
    }

    public RecipeBuilder recipeBuilderPotion() {
        return new RecipeBuilder().potion();
    }

    public RecipeBuilder recipeBuilderDye() {
        return new RecipeBuilder().dye();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> InspirationsRegistryAccessor.getCauldronRecipes().remove(recipe));
        InspirationsRegistryAccessor.getCauldronRecipes().addAll(restoreFromBackup());
    }

    public void add(ICauldronRecipe recipe) {
        addScripted(recipe);
        InspirationsRegistry.addCauldronRecipe(recipe);
    }

    public boolean remove(ICauldronRecipe recipe) {
        if (InspirationsRegistryAccessor.getCauldronRecipes().contains(recipe)) return false;
        addBackup(recipe);
        InspirationsRegistryAccessor.getCauldronRecipes().remove(recipe);
        return true;
    }

    public void removeByInput(IIngredient input) {
        for (ICauldronRecipe recipe : InspirationsRegistryAccessor.getCauldronRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, input, null, null, null))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            InspirationsRegistryAccessor.getCauldronRecipes().remove(recipe);
        }
    }

    public void removeByOutput(ItemStack output) {
        for (ICauldronRecipe recipe : InspirationsRegistryAccessor.getCauldronRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, null, output, null, null))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            InspirationsRegistryAccessor.getCauldronRecipes().remove(recipe);
        }
    }

    public void removeByFluidInput(Fluid input) {
        for (ICauldronRecipe recipe : InspirationsRegistryAccessor.getCauldronRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, null, null, input, null))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            InspirationsRegistryAccessor.getCauldronRecipes().remove(recipe);
        }
    }

    public void removeByFluidInput(FluidStack input) {
        removeByFluidInput(input.getFluid());
    }

    public void removeByFluidOutput(Fluid output) {
        for (ICauldronRecipe recipe : InspirationsRegistryAccessor.getCauldronRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, null, null, null, output))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            InspirationsRegistryAccessor.getCauldronRecipes().remove(recipe);
        }
    }

    public void removeByFluidOutput(FluidStack output) {
        removeByFluidOutput(output.getFluid());
    }

    public void removeAll() {
        InspirationsRegistryAccessor.getCauldronRecipes().forEach(this::addBackup);
        InspirationsRegistryAccessor.getCauldronRecipes().clear();
    }

    public SimpleObjectStream<ICauldronRecipe> streamRecipes() {
        return new SimpleObjectStream<>(InspirationsRegistryAccessor.getCauldronRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<ICauldronRecipe> {

        private RecipeType type;
        private PotionType inputPotion;
        private PotionType outputPotion;
        private EnumDyeColor dye;
        private Boolean boiling = null;
        private int levels;
        private SoundEvent sound;

        public static RecipeMatch recipeMatchFromIngredient(IIngredient ingredient, int amount) {
            return RecipeMatch.of(Arrays.stream(ingredient.getMatchingStacks()).collect(Collectors.toList()), amount, 1);
        }

        public static RecipeMatch recipeMatchFromIngredient(IIngredient ingredient) {
            return recipeMatchFromIngredient(ingredient, 1);
        }

        public RecipeBuilder type(RecipeType type) {
            this.type = type;
            return this;
        }

        public RecipeBuilder type(String type) {
            return type(RecipeType.valueOf(type.toUpperCase(Locale.ROOT)));
        }

        public RecipeBuilder standard() {
            return type(RecipeType.STANDARD);
        }

        public RecipeBuilder transform() {
            return type(RecipeType.TRANSFORM);
        }

        public RecipeBuilder mix() {
            return type(RecipeType.MIX);
        }

        public RecipeBuilder fill() {
            return type(RecipeType.FILL);
        }

        public RecipeBuilder brewing() {
            return type(RecipeType.BREWING);
        }

        public RecipeBuilder potion() {
            return type(RecipeType.POTION);
        }

        public RecipeBuilder dye() {
            return type(RecipeType.DYE);
        }

        public RecipeBuilder inputPotion(PotionType inputPotion) {
            this.inputPotion = inputPotion;
            return this;
        }

        public RecipeBuilder outputPotion(PotionType outputPotion) {
            this.outputPotion = outputPotion;
            return this;
        }

        public RecipeBuilder dye(EnumDyeColor dye) {
            this.dye = dye;
            return this;
        }

        public RecipeBuilder dye(String dye) {
            return dye(EnumDyeColor.valueOf(dye.toUpperCase(Locale.ROOT)));
        }

        public RecipeBuilder boiling(Boolean boiling) {
            this.boiling = boiling;
            return this;
        }

        public RecipeBuilder boiling() {
            this.boiling = boiling == null || !boiling;
            return this;
        }

        public RecipeBuilder levels(int levels) {
            this.levels = levels;
            return this;
        }

        public RecipeBuilder sound(SoundEvent sound) {
            this.sound = sound;
            return this;
        }

        public RecipeBuilder sound(String sound) {
            return sound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sound)));
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Inspirations Cauldron recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            if (type == null) {
                msg.add("type must be defined");
                return;
            }

            switch (type) {
                case STANDARD:
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg, 1, 1, 0, 0);
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                    msg.add(sound == null, "sound must be defined");
                    break;
                case TRANSFORM:
                    validateItems(msg, 1, 1, 0, 0);
                    validateFluids(msg, 1, 1, 1, 1);
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                    break;
                case MIX:
                    validateItems(msg, 0, 0, 1, 1);
                    validateFluids(msg, 2, 2, 0, 0);
                    break;
                case FILL:
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg, 1, 1, 0, 0);
                    msg.add(sound == null, "sound must be defined");
                    break;
                case BREWING:
                    validateItems(msg, 1, 1, 0, 0);
                    validateFluids(msg);
                    msg.add(inputPotion == null, "inputPotion must be defined");
                    msg.add(outputPotion == null, "outputPotion must be defined");
                    break;
                case POTION:
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg);
                    msg.add(inputPotion == null, "inputPotion must be defined");
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                    break;
                case DYE:
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg);
                    msg.add(dye == null, "dye must be defined");
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                    break;
            }
            msg.add(msg.hasSubMessages(), "Note that validation changes based on the type requested");
        }

        @Override
        public @Nullable ICauldronRecipe register() {
            if (!validate()) return null;

            ICauldronRecipe recipe;

            switch (type) {
                case STANDARD:
                    recipe = new CauldronFluidRecipe(recipeMatchFromIngredient(input.get(0)), fluidInput.get(0).getFluid(), output.get(0), boiling, levels, sound);
                    break;
                case TRANSFORM:
                    recipe = new CauldronFluidTransformRecipe(recipeMatchFromIngredient(input.get(0)), fluidInput.get(0).getFluid(), fluidOutput.get(0).getFluid(), boiling, levels);
                    break;
                case MIX:
                    recipe = new CauldronMixRecipe(fluidInput.get(0).getFluid(), fluidInput.get(1).getFluid(), output.get(0));
                    break;
                case FILL:
                    recipe = new FillCauldronRecipe(recipeMatchFromIngredient(input.get(0)), fluidInput.get(0).getFluid(), fluidInput.get(0).amount, output.get(0), boiling, sound);
                    break;
                case BREWING:
                    recipe = new CauldronBrewingRecipe(inputPotion, input.get(0).toMcIngredient(), outputPotion);
                    break;
                case POTION:
                    recipe = new CauldronPotionRecipe(recipeMatchFromIngredient(input.get(0)), inputPotion, output.get(0), levels, boiling);
                    break;
                case DYE:
                    recipe = new CauldronDyeRecipe(recipeMatchFromIngredient(input.get(0)), dye, output.get(0), levels);
                    break;
                default:
                    // Since we validate that type must be defined and return early if it isn't, this is impossible, but Intellij likes that it exists
                    return null;
            }

            ModSupport.INSPIRATIONS.get().cauldron.add(recipe);
            return recipe;
        }


        private enum RecipeType {
            STANDARD,
            TRANSFORM,
            MIX,
            FILL,
            BREWING,
            POTION,
            DYE
        }
    }
}
