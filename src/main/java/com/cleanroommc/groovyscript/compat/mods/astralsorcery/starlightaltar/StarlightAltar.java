package com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class StarlightAltar extends VirtualizedRegistry<AbstractAltarRecipe> {

    public StarlightAltar() {
        super();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> AltarRecipeRegistry.recipes.get(r.getNeededLevel()).removeIf(rec -> rec.equals(r)));
        restoreFromBackup().forEach(r -> AltarRecipeRegistry.recipes.get(r.getNeededLevel()).add(r));
    }

    public void afterScriptLoad() {
        AltarRecipeRegistry.compileRecipes();
    }

    public @Nullable AbstractAltarRecipe add(String name, ItemStack output, ItemHandle[] inputs, int starlightRequired, int craftingTickTime, TileAltar.AltarLevel altarLevel, IConstellation requiredConstellation, ItemHandle[] outerInputs) {
        if (name == null || name.equals(""))
            name = RecipeName.generate("starlight_altar_recipe");
        final int starlightConsumed = MathHelper.clamp(starlightRequired, 1, altarLevel.getStarlightMaxStorage());
        final List<Integer> fluidStacks = this.computeFluidConsumptionSlots(inputs);
        switch (altarLevel) {
            case DISCOVERY:
                DiscoveryRecipe dRec = new DiscoveryRecipe(this.registerNative(name, output, inputs)) {
                    public int getPassiveStarlightRequired() {
                        return starlightConsumed;
                    }

                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }
                };

                return add(dRec);
            case ATTUNEMENT:
                AttunementRecipe aRec = new AttunementRecipe(this.registerNative(name, output, inputs)) {
                    public int getPassiveStarlightRequired() {
                        return starlightConsumed;
                    }

                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }

                    public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }
                };

                for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) aRec.setAttItem(inputs[al.getSlotId()], al);
                }

                return add(aRec);
            case CONSTELLATION_CRAFT:
                ConstellationRecipe cRec = new ConstellationRecipe(this.registerNative(name, output, inputs)) {
                    public int getPassiveStarlightRequired() {
                        return starlightConsumed;
                    }

                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }

                    public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }

                    public boolean mayDecrement(TileAltar ta, ConstellationAtlarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }
                };

                for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) cRec.setAttItem(inputs[al.getSlotId()], al);
                }
                for (ConstellationRecipe.ConstellationAtlarSlot al : ConstellationRecipe.ConstellationAtlarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) cRec.setCstItem(inputs[al.getSlotId()], al);
                }

                return add(cRec);
            case TRAIT_CRAFT:
                TraitRecipe rRec = new TraitRecipe(this.registerNative(name, output, inputs)) {
                    public int getPassiveStarlightRequired() {
                        return starlightConsumed;
                    }

                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }

                    public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }

                    public boolean mayDecrement(TileAltar ta, ConstellationAtlarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }

                    public boolean mayDecrement(TileAltar ta, TraitRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }
                };

                for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) rRec.setAttItem(inputs[al.getSlotId()], al);
                }
                for (ConstellationRecipe.ConstellationAtlarSlot al : ConstellationRecipe.ConstellationAtlarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) rRec.setCstItem(inputs[al.getSlotId()], al);
                }
                for (TraitRecipe.TraitRecipeSlot al : TraitRecipe.TraitRecipeSlot.values()) {
                    if (inputs[al.getSlotId()] != null) rRec.setInnerTraitItem(inputs[al.getSlotId()], al);
                }
                for (int i = 25; i < inputs.length; i++) {
                    if (inputs[i] != null) {
                        rRec.addOuterTraitItem(inputs[i]);
                    }
                }

                if (requiredConstellation != null)
                    rRec.setRequiredConstellation(requiredConstellation);
                for (ItemHandle item : outerInputs)
                    rRec.addOuterTraitItem(item);

                return add(rRec);
            default:
                return null;
        }
    }

    private AccessibleRecipeAdapater registerNative(String name, ItemStack output, ItemHandle[] inputs) {
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

    private AbstractAltarRecipe add(AbstractAltarRecipe recipe) {
        addScripted(recipe);
        AltarRecipeRegistry.recipes.get(recipe.getNeededLevel()).add(recipe);
        return recipe;
    }

    private List<Integer> computeFluidConsumptionSlots(ItemHandle[] inputs) {
        List<Integer> fluidInputs = Lists.newLinkedList();

        for (int i = 0; i < inputs.length; ++i) {
            ItemHandle handle = inputs[i];
            if (handle != null && handle.handleType == ItemHandle.Type.FLUID) {
                fluidInputs.add(i);
            }
        }

        return fluidInputs;
    }

    public void removeByOutput(ItemStack output) {
        removeByOutput(output, TileAltar.AltarLevel.DISCOVERY);
        removeByOutput(output, TileAltar.AltarLevel.ATTUNEMENT);
        removeByOutput(output, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
        removeByOutput(output, TileAltar.AltarLevel.TRAIT_CRAFT);
    }

    public void removeByOutput(ItemStack output, TileAltar.AltarLevel altarLevel) {
        AltarRecipeRegistry.recipes.get(altarLevel).removeIf(recipe -> {
            if (recipe.getOutputForMatching().isItemEqual(output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    private boolean remove(AbstractAltarRecipe recipe) {
        return AltarRecipeRegistry.recipes.get(recipe.getNeededLevel()).removeIf(rec -> rec.equals(recipe));
    }

    public SimpleObjectStream<AbstractAltarRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AltarRecipeRegistry.recipes.entrySet().stream().flatMap(r -> r.getValue().stream()).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    public void removeAll() {
        AltarRecipeRegistry.recipes.forEach((level, recipes) -> {
            recipes.forEach(this::addBackup);
            recipes.clear();
        });
    }

    public static AltarRecipeBuilder discoveryRecipeBuilder() {
        return new AltarRecipeBuilder(3, 3, TileAltar.AltarLevel.DISCOVERY);
    }

    public static AltarRecipeBuilder attunementRecipeBuilder() {
        return new AltarRecipeBuilder(5, 5, TileAltar.AltarLevel.ATTUNEMENT);
    }

    public static AltarRecipeBuilder constellationRecipeBuilder() {
        return new AltarRecipeBuilder(5, 5, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
    }

    public static AltarRecipeBuilder traitRecipeBuilder() {
        return new AltarRecipeBuilder(5, 5, TileAltar.AltarLevel.TRAIT_CRAFT);
    }
}
