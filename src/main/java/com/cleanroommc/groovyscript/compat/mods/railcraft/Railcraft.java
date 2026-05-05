package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class Railcraft extends GroovyPropertyContainer {

    public final BlastFurnace blastFurnace = new BlastFurnace();
    public final CokeOven cokeOven = new CokeOven();
    public final RockCrusher rockCrusher = new RockCrusher();
    public final RollingMachine rollingMachine = new RollingMachine();
    public final FluidFuels fluidFuels = new FluidFuels();

    /**
     * Converts a GroovyScript IIngredient to a Minecraft Ingredient for Railcraft recipes
     */
    public static Ingredient toIngredient(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) {
            return new net.minecraftforge.oredict.OreIngredient(oreDictIngredient.getOreDict());
        }
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        if (matchingStacks.length == 0) {
            return Ingredient.EMPTY;
        }
        return Ingredient.fromStacks(matchingStacks);
    }
}
