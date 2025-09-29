package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.helper.ingredient.itemstack.ItemStackSet;

@GroovyBlacklist
public class FurnaceRecipeManager {

    /**
     * All input items for the furnace. Uses a mixin so adding a recipe adds to this set.
     * This does <i>not</i> control logic, it just reflects it.
     * Uses a custom datastructure {@link ItemStackSet} to handle the special case of wildcard metadata
     * without violating Set hash <=> equals logic
     *
     * @see com.cleanroommc.groovyscript.core.mixin.FurnaceRecipeMixin FurnaceRecipeMixin
     */
    public static final ItemStackSet FURNACE_INPUTS = new ItemStackSet();

}
