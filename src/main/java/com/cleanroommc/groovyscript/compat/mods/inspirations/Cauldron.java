package com.cleanroommc.groovyscript.compat.mods.inspirations;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.inspirations.InspirationsRegistryAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
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
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

@RegistryDescription(
        admonition = @Admonition("groovyscript.wiki.inspirations.cauldron.note")
)
public class Cauldron extends StandardListRegistry<ICauldronRecipe> {

    private static boolean checkRecipeMatches(ISimpleCauldronRecipe recipe, IIngredient input, ItemStack output, Object inputState, Object outputState) {
        // Check all relevant parts to determine if we need to match them and if they do match.
        return (inputState == null || compareFluids(inputState, recipe.getInputState())) && (outputState == null || compareFluids(outputState, recipe.getState())) && (output == null || output.isItemEqual(recipe.getResult())) && (input == null || recipe.getInput().stream().anyMatch(input));
    }

    private static boolean compareFluids(Object first, Object second) {
        if (first == second) return true;
        if (first instanceof Fluid fluid1 && second instanceof Fluid fluid2) return fluid1.getName().equals(fluid2.getName());
        return false;
    }

    @RecipeBuilderDescription(example = @Example(".standard().input(item('minecraft:gold_ingot')).fluidInput(fluid('lava')).output(item('minecraft:clay')).boiling().sound(sound('minecraft:block.anvil.destroy')).levels(3)"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "type"),
            @Property(property = "input"),
            @Property(property = "output"),
            @Property(property = "fluidInput"),
            @Property(property = "fluidOutput"),
            @Property(property = "inputPotion"),
            @Property(property = "outputPotion"),
            @Property(property = "dye"),
            @Property(property = "boiling"),
            @Property(property = "levels"),
            @Property(property = "sound")
    }))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).fluidInput(fluid('lava')).levels(3).sound(sound('minecraft:block.anvil.destroy'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "input"),
            @Property(property = "output"),
            @Property(property = "fluidInput"),
            @Property(property = "boiling"),
            @Property(property = "levels"),
            @Property(property = "sound")
    }))
    public RecipeBuilder recipeBuilderStandard() {
        return new RecipeBuilder().standard();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone:3')).fluidInput(fluid('water')).fluidOutput(fluid('milk')).levels(2)"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "input"),
            @Property(property = "fluidInput"),
            @Property(property = "fluidOutput"),
            @Property(property = "boiling"),
            @Property(property = "levels"),
            @Property(property = "sound")
    }))
    public RecipeBuilder recipeBuilderTransform() {
        return new RecipeBuilder().transform();
    }

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:clay')).fluidInput(fluid('milk'), fluid('lava'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "fluidInput", comp = @Comp(eq = 2)),
            @Property(property = "output")
    }))
    public RecipeBuilder recipeBuilderMix() {
        return new RecipeBuilder().mix();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).fluidInput(fluid('milk')).sound(sound('minecraft:block.anvil.destroy'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "input"),
            @Property(property = "output"),
            @Property(property = "fluidInput"),
            @Property(property = "boiling"),
            @Property(property = "sound")
    }))
    public RecipeBuilder recipeBuilderFill() {
        return new RecipeBuilder().fill();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond_block')).inputPotion(potionType('minecraft:fire_resistance')).outputPotion(potionType('minecraft:strength'))"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "input"),
            @Property(property = "inputPotion"),
            @Property(property = "outputPotion")
    }))
    public RecipeBuilder recipeBuilderBrewing() {
        return new RecipeBuilder().brewing();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_block')).output(item('minecraft:diamond_block')).inputPotion(potionType('minecraft:fire_resistance')).levels(2)"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "input"),
            @Property(property = "output"),
            @Property(property = "inputPotion", comp = @Comp(gte = 0, lte = 1)),
            @Property(property = "boiling"),
            @Property(property = "levels")
    }))
    public RecipeBuilder recipeBuilderPotion() {
        return new RecipeBuilder().potion();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:gold_block')).output(item('minecraft:diamond_block')).dye('blue').levels(2)"), override = @RecipeBuilderOverride(requirement = {
            @Property(property = "input"),
            @Property(property = "output"),
            @Property(property = "dye"),
            @Property(property = "levels")
    }))
    public RecipeBuilder recipeBuilderDye() {
        return new RecipeBuilder().dye();
    }

    @Override
    public Collection<ICauldronRecipe> getRecipes() {
        return InspirationsRegistryAccessor.getCauldronRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:ghast_tear')"))
    public void removeByInput(IIngredient input) {
        for (ICauldronRecipe recipe : getRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, input, null, null, null))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            getRecipes().remove(recipe);
        }
    }

    @MethodDescription(example = @Example("item('minecraft:piston')"))
    public void removeByOutput(ItemStack output) {
        for (ICauldronRecipe recipe : getRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, null, output, null, null))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            getRecipes().remove(recipe);
        }
    }

    @MethodDescription
    public void removeByFluidInput(Fluid input) {
        for (ICauldronRecipe recipe : getRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, null, null, input, null))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            getRecipes().remove(recipe);
        }
    }

    @MethodDescription(example = @Example("fluid('mushroom_stew')"))
    public void removeByFluidInput(FluidStack input) {
        removeByFluidInput(input.getFluid());
    }

    @MethodDescription
    public void removeByFluidOutput(Fluid output) {
        for (ICauldronRecipe recipe : getRecipes().stream()
                .filter(r -> r instanceof ISimpleCauldronRecipe && checkRecipeMatches((ISimpleCauldronRecipe) r, null, null, null, output))
                .collect(Collectors.toList())) {
            addBackup(recipe);
            getRecipes().remove(recipe);
        }
    }

    @MethodDescription(example = @Example("fluid('beetroot_soup')"))
    public void removeByFluidOutput(FluidStack output) {
        removeByFluidOutput(output.getFluid());
    }

    @Property(property = "input", comp = @Comp(eq = 1), needsOverride = true)
    @Property(property = "output", comp = @Comp(eq = 1), needsOverride = true)
    @Property(property = "fluidInput", comp = @Comp(eq = 1), needsOverride = true)
    @Property(property = "fluidOutput", comp = @Comp(eq = 1), needsOverride = true)
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICauldronRecipe> {

        @Property(needsOverride = true)
        private RecipeType type;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private PotionType inputPotion;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private PotionType outputPotion;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private EnumDyeColor dye;
        @Property(needsOverride = true)
        private Boolean boiling;
        @Property(comp = @Comp(gte = 0, unique = "groovyscript.wiki.inspirations.cauldron.levels.required"), needsOverride = true)
        private int levels;
        @Property(comp = @Comp(not = "null"), needsOverride = true)
        private SoundEvent sound;

        public static RecipeMatch recipeMatchFromIngredient(IIngredient ingredient, int amount) {
            return RecipeMatch.of(Arrays.asList(ingredient.getMatchingStacks()), amount, 1);
        }

        public static RecipeMatch recipeMatchFromIngredient(IIngredient ingredient) {
            return recipeMatchFromIngredient(ingredient, 1);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(RecipeType type) {
            this.type = type;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder type(String type) {
            return type(RecipeType.valueOf(type.toUpperCase(Locale.ROOT)));
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder standard() {
            return type(RecipeType.STANDARD);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder transform() {
            return type(RecipeType.TRANSFORM);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder mix() {
            return type(RecipeType.MIX);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder fill() {
            return type(RecipeType.FILL);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder brewing() {
            return type(RecipeType.BREWING);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder potion() {
            return type(RecipeType.POTION);
        }

        @RecipeBuilderMethodDescription(field = "type")
        public RecipeBuilder dye() {
            return type(RecipeType.DYE);
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder inputPotion(PotionType inputPotion) {
            this.inputPotion = inputPotion;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputPotion(PotionType outputPotion) {
            this.outputPotion = outputPotion;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder dye(EnumDyeColor dye) {
            this.dye = dye;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder dye(String dye) {
            return dye(EnumDyeColor.valueOf(dye.toUpperCase(Locale.ROOT)));
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder boiling(Boolean boiling) {
            this.boiling = boiling;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder boiling() {
            this.boiling = boiling == null || !boiling;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder levels(int levels) {
            this.levels = levels;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder sound(SoundEvent sound) {
            this.sound = sound;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder sound(String sound) {
            return sound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sound)));
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Inspirations Cauldron recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            if (type == null) {
                msg.add("type must be defined");
                return;
            }

            switch (type) {
                case STANDARD -> {
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg, 1, 1, 0, 0);
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                    msg.add(sound == null, "sound must be defined");
                }
                case TRANSFORM -> {
                    validateItems(msg, 1, 1, 0, 0);
                    validateFluids(msg, 1, 1, 1, 1);
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                }
                case MIX -> {
                    validateItems(msg, 0, 0, 1, 1);
                    validateFluids(msg, 2, 2, 0, 0);
                }
                case FILL -> {
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg, 1, 1, 0, 0);
                    msg.add(sound == null, "sound must be defined");
                }
                case BREWING -> {
                    validateItems(msg, 1, 1, 0, 0);
                    validateFluids(msg);
                    msg.add(inputPotion == null, "inputPotion must be defined");
                    msg.add(outputPotion == null, "outputPotion must be defined");
                }
                case POTION -> {
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg);
                    msg.add(inputPotion == null, "inputPotion must be defined");
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                }
                case DYE -> {
                    validateItems(msg, 1, 1, 1, 1);
                    validateFluids(msg);
                    msg.add(dye == null, "dye must be defined");
                    msg.add(levels <= 0 || levels > InspirationsRegistry.getCauldronMax(), "levels must be greater than 0 and less than {}, yet it was {}", InspirationsRegistry.getCauldronMax(), levels);
                }
            }
            msg.add(msg.hasSubMessages(), "Note that validation changes based on the type requested");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICauldronRecipe register() {
            if (!validate()) return null;

            ICauldronRecipe recipe = switch (type) {
                case STANDARD -> new CauldronFluidRecipe(recipeMatchFromIngredient(input.get(0)), fluidInput.get(0).getFluid(), output.get(0), boiling, levels, sound);
                case TRANSFORM -> new CauldronFluidTransformRecipe(recipeMatchFromIngredient(input.get(0)), fluidInput.get(0).getFluid(), fluidOutput.get(0).getFluid(), boiling, levels);
                case MIX -> new CauldronMixRecipe(fluidInput.get(0).getFluid(), fluidInput.get(1).getFluid(), output.get(0));
                case FILL -> new FillCauldronRecipe(recipeMatchFromIngredient(input.get(0)), fluidInput.get(0).getFluid(), fluidInput.get(0).amount, output.get(0), boiling, sound);
                case BREWING -> new CauldronBrewingRecipe(inputPotion, input.get(0).toMcIngredient(), outputPotion);
                case POTION -> new CauldronPotionRecipe(recipeMatchFromIngredient(input.get(0)), inputPotion, output.get(0), levels, boiling);
                case DYE -> new CauldronDyeRecipe(recipeMatchFromIngredient(input.get(0)), dye, output.get(0), levels);
            };

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
