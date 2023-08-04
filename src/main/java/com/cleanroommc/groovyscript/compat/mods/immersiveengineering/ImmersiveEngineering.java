package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;

public class ImmersiveEngineering extends ModPropertyContainer {

    public final AlloyKiln alloyKiln = new AlloyKiln();
    public final ArcFurnace arcFurnace = new ArcFurnace();
    public final BlastFurnace blastFurnace = new BlastFurnace();
    public final BlastFurnaceFuel blastFurnaceFuel = new BlastFurnaceFuel();
    public final BlueprintCrafting blueprint = new BlueprintCrafting();
    public final BottlingMachine bottlingMachine = new BottlingMachine();
    public final CokeOven cokeOven = new CokeOven();
    public final Crusher crusher = new Crusher();
    public final Excavator excavator = new Excavator();
    public final Fermenter fermenter = new Fermenter();
    public final MetalPress metalPress = new MetalPress();
    public final Mixer mixer = new Mixer();
    public final Refinery refinery = new Refinery();
    public final Squeezer squeezer = new Squeezer();

    public ImmersiveEngineering() {
        addRegistry(alloyKiln);
        addRegistry(arcFurnace);
        addRegistry(blastFurnace);
        addRegistry(blastFurnaceFuel);
        addRegistry(blueprint);
        addRegistry(bottlingMachine);
        addRegistry(cokeOven);
        addRegistry(crusher);
        addRegistry(excavator);
        addRegistry(fermenter);
        addRegistry(metalPress);
        addRegistry(mixer);
        addRegistry(refinery);
        addRegistry(squeezer);
    }

    public static IngredientStack toIngredientStack(IIngredient ingredient) {
        if (IngredientHelper.isItem(ingredient)) {
            return new IngredientStack(IngredientHelper.toItemStack(ingredient).copy());
        }
        if (ingredient instanceof OreDictIngredient) {
            return new IngredientStack(((OreDictIngredient) ingredient).getOreDict());
        }
        if (ingredient instanceof FluidStack) {
            return new IngredientStack(((FluidStack) ingredient).copy());
        }
        return new IngredientStack(Arrays.asList(ingredient.getMatchingStacks()));
    }

    public static boolean areIngredientsEquals(IngredientStack target, IIngredient other) {
        return other instanceof OreDictIngredient ? target.matches(((OreDictIngredient) other).getOreDict()) : target.matches(other.getMatchingStacks());
    }

    public static boolean areIngredientsEquals(IIngredient target, IngredientStack other) {
        return areIngredientsEquals(other, target);
    }

    public static Object toIEInput(IIngredient ingredient) {
        if (ingredient instanceof OreDictIngredient) {
            return ((OreDictIngredient) ingredient).getOreDict();
        }
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        return matchingStacks.length == 0 ? ItemStack.EMPTY : matchingStacks[0];
    }

}
