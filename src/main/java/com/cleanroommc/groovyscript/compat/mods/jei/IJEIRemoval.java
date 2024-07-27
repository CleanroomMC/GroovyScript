package com.cleanroommc.groovyscript.compat.mods.jei;

import mezz.jei.api.gui.IRecipeLayout;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IJEIRemoval {

    /**
     * A list containing any number of strings which
     * are the UIDs of a JEI recipe category {@link mezz.jei.api.recipe.IRecipeCategory#getUid()}.
     * <br>
     * This list is typically immutable.
     *
     * @return a list of all UIDs associated with the target recipe category
     */
    @NotNull
    Collection<String> getCategories();

    /**
     * Generates the removal method for the targeted recipe,
     * using the information from JEI to do so.
     * <br>
     * Should only remove a single recipe, although this may not be guaranteed.
     * <br>
     * The path to the recipe will automatically be generated, only the method name and its parameters should be returned.
     *
     * @param layout a map of type to group of slots displayed in JEI
     * @return the method to remove the specific recipe
     */
    @NotNull
    String getRemoval(IRecipeLayout layout);

    @Deprecated
    interface Direct extends IJEIRemoval {
        // TODO removeme
    }

    /**
     * Has a default removal method, {@link #getRemoval(IRecipeLayout)}, which returns a method to remove by all input ingredients.
     */
    interface IJEIRemoveByAllInput extends IJEIRemoval {

        /**
         * @see JeiRemovalHelper#getFromInput
         */
        @Override
        default @NotNull String getRemoval(IRecipeLayout layout) {
            return JeiRemovalHelper.getFromInput(layout);
        }

    }

    /**
     * Has a default removal method, {@link #getRemoval(IRecipeLayout)}, which returns a method to remove by the first {@link net.minecraft.item.ItemStack} input.
     * Should only be used if there is only a single input, and there is only a single input -> recipe entry.
     */
    interface IJEIRemoveByItemInput extends IJEIRemoval {

        /**
         * @see JeiRemovalHelper#getFromSingleUniqueItemInput
         */
        @Override
        default @NotNull String getRemoval(IRecipeLayout layout) {
            return JeiRemovalHelper.getFromSingleUniqueItemInput(layout);
        }

    }

    /**
     * Has a default removal method, {@link #getRemoval(IRecipeLayout)}, which returns a method to remove by the first {@link net.minecraftforge.fluids.FluidStack} input.
     * Should only be used if there is only a single input, and there is only a single input -> recipe entry.
     */
    interface IJEIRemoveByFluidInput extends IJEIRemoval.Direct {

        /**
         * @see JeiRemovalHelper#getFromSingleUniqueFluidInput
         */
        @Override
        default @NotNull String getRemoval(IRecipeLayout layout) {
            return JeiRemovalHelper.getFromSingleUniqueFluidInput(layout);
        }

    }
}
