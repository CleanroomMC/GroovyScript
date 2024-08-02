package com.cleanroommc.groovyscript.api.jeiremoval.operations;

import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ItemOperation extends IngredientSlotOperation<ItemStack> {

    public ItemOperation() {
        super(VanillaTypes.ITEM, true,
              (stack, all) -> {
                  var oreDict = convertToOreDict(all);
                  return oreDict.isEmpty() ? GroovyScriptCodeConverter.getSingleItemStack(stack, true, false) : oreDict;
              });
    }

    /**
     * @param ingredients a list of itemstacks to check
     * @return if the list of itemstacks are an oredict, represent them as an oredict
     */
    private static String convertToOreDict(List<ItemStack> ingredients) {
        var dict = OperationHandler.getStackHelper().getOreDictEquivalent(ingredients);
        return dict == null ? "" : GroovyScriptCodeConverter.asGroovyCode(dict, true);
    }

    /**
     * @return the base {@link ItemOperation}
     */
    public static ItemOperation defaultOperation() {
        return new ItemOperation();
    }

}
