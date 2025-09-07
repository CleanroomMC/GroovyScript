package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.helper.ingredient.ItemStackHashStrategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.item.ItemStack;

@GroovyBlacklist
public class FurnaceRecipeManager {

    /**
     * All input items for the furnace. Uses a mixin so adding a recipe adds to this set.
     * This does <i>not</i> control logic, it just reflects it.
     *
     * @see com.cleanroommc.groovyscript.core.mixin.FurnaceRecipeMixin FurnaceRecipeMixin
     */
    public static final ObjectOpenCustomHashSet<ItemStack> INPUT_SET = new ObjectOpenCustomHashSet<>(ItemStackHashStrategy.STRATEGY);

}
