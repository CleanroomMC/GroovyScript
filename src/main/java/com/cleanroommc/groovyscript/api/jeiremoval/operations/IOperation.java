package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import mezz.jei.api.gui.IRecipeLayout;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

/**
 * Base operations requirement, merely requires that the implementing classes contain a {@link #parse(IRecipeLayout, List, List)} method.
 */
@ParametersAreNonnullByDefault
public interface IOperation {

    /**
     * @param layout     the recipe layout to be parsed
     * @param removing   a list of strings which should be modified, and any removal methods should be added to
     * @param exactInput if the implementing class gathers input ingredients, an addition list to manage them for a separate method
     */
    void parse(IRecipeLayout layout, List<String> removing, List<String> exactInput);

}
