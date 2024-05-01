package com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.AstralSorcery;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.AbstractCraftingRecipeBuilder;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AltarRecipeBuilder {

    private static AccessibleRecipeAdapater registerNative(String name, ItemStack output, ItemHandle[] inputs) {
        ShapedRecipe.Builder builder = ShapedRecipe.Builder.newShapedRecipe(name, output);

        for (int i = 0; i < 9; ++i) {
            ItemHandle itemHandle = inputs[i];
            if (itemHandle != null) {
                ShapedRecipeSlot srs = ShapedRecipeSlot.values()[i];
                builder.addPart(inputs[i], srs);
            }
        }

        return builder.unregisteredAccessibleShapedRecipe();
    }

    private static List<Integer> computeFluidConsumptionSlots(ItemHandle[] inputs) {
        List<Integer> fluidInputs = Lists.newLinkedList();

        for (int i = 0; i < inputs.length; ++i) {
            ItemHandle handle = inputs[i];
            if (handle != null && handle.handleType == ItemHandle.Type.FLUID) {
                fluidInputs.add(i);
            }
        }

        return fluidInputs;
    }

    private static DiscoveryRecipe discoveryAltar(String name, ItemStack output, ItemHandle[] inputs, int starlightRequired, int craftingTickTime, List<Integer> fluidStacks) {
        return new DiscoveryRecipe(registerNative(name, output, inputs)) {
            @Override
            public int getPassiveStarlightRequired() {
                return starlightRequired;
            }

            @Override
            public int craftingTickTime() {
                return craftingTickTime;
            }

            @Override
            public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                return !fluidStacks.contains(slot.getSlotID());
            }
        };
    }

    private static AttunementRecipe attunementAltar(String name, ItemStack output, ItemHandle[] inputs, int starlightRequired, int craftingTickTime, List<Integer> fluidStacks) {
        AttunementRecipe recipe = new AttunementRecipe(registerNative(name, output, inputs)) {
            @Override
            public int getPassiveStarlightRequired() {
                return starlightRequired;
            }

            @Override
            public int craftingTickTime() {
                return craftingTickTime;
            }

            @Override
            public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                return !fluidStacks.contains(slot.getSlotID());
            }

            @Override
            public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                return !fluidStacks.contains(slot.getSlotId());
            }
        };

        for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
            if (inputs[al.getSlotId()] != null) {
                recipe.setAttItem(inputs[al.getSlotId()], al);
            }
        }

        return recipe;
    }

    private static ConstellationRecipe constellationAltar(String name, ItemStack output, ItemHandle[] inputs, int starlightRequired, int craftingTickTime, List<Integer> fluidStacks) {
        ConstellationRecipe recipe = new ConstellationRecipe(registerNative(name, output, inputs)) {
            @Override
            public int getPassiveStarlightRequired() {
                return starlightRequired;
            }

            @Override
            public int craftingTickTime() {
                return craftingTickTime;
            }

            @Override
            public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                return !fluidStacks.contains(slot.getSlotID());
            }

            @Override
            public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                return !fluidStacks.contains(slot.getSlotId());
            }

            @Override
            public boolean mayDecrement(TileAltar ta, ConstellationAtlarSlot slot) {
                return !fluidStacks.contains(slot.getSlotId());
            }
        };

        for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
            if (inputs[al.getSlotId()] != null) {
                recipe.setAttItem(inputs[al.getSlotId()], al);
            }
        }
        for (ConstellationRecipe.ConstellationAtlarSlot al : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            if (inputs[al.getSlotId()] != null) {
                recipe.setCstItem(inputs[al.getSlotId()], al);
            }
        }

        return recipe;
    }

    private static TraitRecipe traitAltar(String name, ItemStack output, ItemHandle[] inputs, int starlightRequired, int craftingTickTime, List<Integer> fluidStacks, IConstellation requiredConstellation, ItemHandle[] outerInputs) {
        TraitRecipe recipe = new TraitRecipe(registerNative(name, output, inputs)) {
            @Override
            public int getPassiveStarlightRequired() {
                return starlightRequired;
            }

            @Override
            public int craftingTickTime() {
                return craftingTickTime;
            }

            @Override
            public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                return !fluidStacks.contains(slot.getSlotID());
            }

            @Override
            public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                return !fluidStacks.contains(slot.getSlotId());
            }

            @Override
            public boolean mayDecrement(TileAltar ta, ConstellationAtlarSlot slot) {
                return !fluidStacks.contains(slot.getSlotId());
            }

            @Override
            public boolean mayDecrement(TileAltar ta, TraitRecipeSlot slot) {
                return !fluidStacks.contains(slot.getSlotId());
            }
        };

        for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
            if (inputs[al.getSlotId()] != null) {
                recipe.setAttItem(inputs[al.getSlotId()], al);
            }
        }
        for (ConstellationRecipe.ConstellationAtlarSlot al : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            if (inputs[al.getSlotId()] != null) {
                recipe.setCstItem(inputs[al.getSlotId()], al);
            }
        }
        for (TraitRecipe.TraitRecipeSlot al : TraitRecipe.TraitRecipeSlot.values()) {
            if (inputs[al.getSlotId()] != null) {
                recipe.setInnerTraitItem(inputs[al.getSlotId()], al);
            }
        }
        for (int i = 25; i < inputs.length; i++) {
            if (inputs[i] != null) {
                recipe.addOuterTraitItem(inputs[i]);
            }
        }
        if (requiredConstellation != null) {
            recipe.setRequiredConstellation(requiredConstellation);
        }
        for (ItemHandle item : outerInputs) {
            recipe.addOuterTraitItem(item);
        }

        return recipe;
    }

    @Property(property = "mirrored", needsOverride = true)
    @Property(property = "recipeFunction", needsOverride = true)
    @Property(property = "recipeAction", needsOverride = true)
    @Property(property = "name", ignoresInheritedMethods = true, value = "groovyscript.wiki.astralsorcery.starlight_altar.name.value")
    public static class Shaped extends AbstractCraftingRecipeBuilder.AbstractShaped<AbstractAltarRecipe> {

        private final TileAltar.AltarLevel altarLevel;
        @Property(needsOverride = true)
        private final ArrayList<IIngredient> outerIngredients = new ArrayList<>();
        @Property(ignoresInheritedMethods = true)
        protected String name;
        @Property(defaultValue = "1")
        protected int starlightRequired = 1;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT), defaultValue = "1")
        protected int craftingTickTime = 1;
        @Property(needsOverride = true)
        protected IConstellation requiredConstellation;
        private ItemHandle[] inputs;
        private ItemHandle[] outerInputs;

        public Shaped(int width, int height, TileAltar.AltarLevel level) {
            super(width, height);
            this.altarLevel = level;
            this.keyMap.put(' ', IIngredient.EMPTY);
        }

        @Override
        @RecipeBuilderMethodDescription
        public Shaped name(String name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "inputs")
        public Shaped input(ItemHandle[] inputs) {
            this.inputs = inputs;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "starlightRequired")
        public Shaped starlight(int starlight) {
            this.starlightRequired = starlight;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "craftingTickTime")
        public Shaped craftTime(int ticks) {
            this.craftingTickTime = ticks;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "requiredConstellation")
        public Shaped constellation(IConstellation constellation) {
            this.requiredConstellation = constellation;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "outerIngredients")
        public Shaped outerInput(IIngredient ing) {
            this.outerIngredients.add(ing);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "outerIngredients")
        public Shaped outerInput(IIngredient... ings) {
            this.outerIngredients.addAll(Arrays.asList(ings));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "outerIngredients")
        public Shaped outerInput(Collection<IIngredient> ings) {
            this.outerIngredients.addAll(ings);
            return this;
        }

        private boolean flattenMatrix() {
            ItemHandle[] in = AltarInputOrder.initInputList(this.altarLevel);
            int[][] map = AltarInputOrder.getMap(this.altarLevel);
            if (map == null || in == null) {
                return false;
            }

            for (int i = 0; i < this.keyBasedMatrix.length; i++) {
                String row = this.keyBasedMatrix[i];
                for (int j = 0; j < row.length(); j++) {
                    if (map[i][j] >= 0 && row.charAt(j) != ' ') {
                        in[map[i][j]] = AstralSorcery.toItemHandle(keyMap.get(row.charAt(j)));
                    }
                }
            }

            this.input(in);

            ItemHandle[] outerIn = new ItemHandle[this.outerIngredients.size()];
            for (int j = 0; j < this.outerIngredients.size(); j++) {
                outerIn[j] = AstralSorcery.toItemHandle(this.outerIngredients.get(j));
            }
            this.outerInputs = outerIn;

            return true;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_starlight_altar_recipe_";
        }

        @Override
        @GroovyBlacklist
        public void validateName() {
            if (name == null) {
                name = RecipeName.generate(getRecipeNamePrefix());
            }
        }

        public boolean validate() {
            GroovyLog.Msg out = GroovyLog.msg("Error adding Astral Sorcery Starlight Altar recipe").warn();

            validateName();

            if (this.output == null || this.output.isItemEqual(ItemStack.EMPTY)) {
                out.add("Recipe output cannot be empty.").error();
            }
            if (this.starlightRequired <= 0) {
                out.add("Starlight amount cannot be negative or 0, setting starlight amount to 1.");
                this.starlightRequired = 1;
            }
            if (this.craftingTickTime <= 0) {
                out.add("Crafting time cannot be negative or 0, setting crafting time to 1.");
                this.craftingTickTime = 1;
            }

            int maximumValue = altarLevel.getStarlightMaxStorage();
            if (this.starlightRequired > maximumValue) {
                out.add("Altar recipe cannot exceed {} starlight, clamping starlight to {} value.", maximumValue, maximumValue);
                this.starlightRequired = Math.min(starlightRequired, maximumValue);
            }

            if (keyBasedMatrix != null) {
                validateShape(out, errors, keyBasedMatrix, keyMap, (width, height, ingredients) -> null);
            } else if (ingredientMatrix != null) {
                validateShape(out, ingredientMatrix, (width, height, ingredients) -> null);
            }

            out.postIfNotEmpty();
            return (out.getLevel() != Level.ERROR && this.flattenMatrix());
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public AbstractAltarRecipe register() {
            if (!validate()) return null;

            List<Integer> fluidStacks = computeFluidConsumptionSlots(inputs);
            AbstractAltarRecipe recipe = switch (altarLevel) {
                case DISCOVERY -> discoveryAltar(name, output, inputs, starlightRequired, craftingTickTime, fluidStacks);
                case ATTUNEMENT -> attunementAltar(name, output, inputs, starlightRequired, craftingTickTime, fluidStacks);
                case CONSTELLATION_CRAFT -> constellationAltar(name, output, inputs, starlightRequired, craftingTickTime, fluidStacks);
                case TRAIT_CRAFT -> traitAltar(name, output, inputs, starlightRequired, craftingTickTime, fluidStacks, requiredConstellation, outerInputs);
                default -> null;
            };
            ModSupport.ASTRAL_SORCERY.get().altar.add(recipe);
            return recipe;
        }
    }
}