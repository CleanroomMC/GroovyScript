package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.*;

public class Utils {

    public static ItemHandle convertToItemHandle(IIngredient in) {
        return Utils.convertToItemHandle((Object) in);
    }

    private static ItemHandle convertToItemHandle(Object in) {
        if (in == null) {
            return ItemHandle.EMPTY;
        }
        if (in instanceof ItemStack) {
            return new ItemHandle((ItemStack) in);
        } else if (in instanceof ItemStack[]) {
            return new ItemHandle((ItemStack[]) in);
        } else if (in instanceof OreDictIngredient) {
            return new ItemHandle(((OreDictIngredient) in).getOreDict());
        } else if (in instanceof FluidStack) {
            return new ItemHandle((FluidStack) in);
        }
        return ItemHandle.EMPTY;
    }

    public static boolean isEqual(ItemHandle item1, ItemHandle item2) {
        if (item1.getOreDictName() != null && item2.getOreDictName() != null
                && !item1.getOreDictName().equals("") && !item2.getOreDictName().equals("")
                && item1.getOreDictName().equals(item2.getOreDictName())) {
            return true;
        } else if (item1.getApplicableItems() != null && item2.getApplicableItems() != null
                && item1.getApplicableItems().size() == item2.getApplicableItems().size()) {
            boolean rVal = true;
            for (int i = 0; i < item1.getApplicableItems().size(); i++) {
                if (!item1.getApplicableItems().get(i).isItemEqual(item2.getApplicableItems().get(i)))
                    rVal = false;
            }
            return rVal;
        }
        return false;
    }

    public static FluidRarityRegistry.FluidRarityEntry createNewFRE(Fluid fluid, int rarity, int guaranteedAmt, int addRand) {
        try {
            Class<?>[] args = new Class[4];
            args[0] = Fluid.class;
            args[1] = int.class;
            args[2] = int.class;
            args[3] = int.class;
            Constructor<FluidRarityRegistry.FluidRarityEntry> constructor = FluidRarityRegistry.FluidRarityEntry.class.getDeclaredConstructor(args);
            constructor.setAccessible(true);
            return constructor.newInstance(fluid, rarity, guaranteedAmt, addRand);
        } catch(Exception e) {
            GroovyLog.get().exception(e);
        }

        return null;
    }

}
