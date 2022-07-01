package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.wrapper.ItemStack;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2CharArrayMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class Recipes {

    public void addShaped(String name, ItemStack output, List<List<ItemStack>> inputMatrix) {
        IRecipe recipe = new ShapedOreRecipe(null, output.getMCItemStack(), makeShapedMatrix(inputMatrix))
                .setMirrored(false)
                .setRegistryName(name);
        ReloadableRegistryManager.registerRecipe(recipe);
    }

    private static Object[] makeShapedMatrix(List<List<ItemStack>> inputMatrix) {
        List<Object> result = new ArrayList<>();
        Object2CharArrayMap<ItemStack> inputs = new Object2CharArrayMap<>();
        Char2ObjectArrayMap<Object> inputs2 = new Char2ObjectArrayMap<>();
        char c = 'a';
        for (List<ItemStack> row : inputMatrix) {
            StringBuilder finalizedRow = new StringBuilder();
            for (ItemStack input : row) {
                if (input == null || input.isEmpty()) {
                    finalizedRow.append(' ');
                    continue;
                }
                if (inputs.containsKey(input)) {
                    finalizedRow.append(inputs.getChar(input));
                } else {
                    inputs.put(input, c);
                    finalizedRow.append(c);
                    inputs2.put(c, input.getMCIngredient());
                    c++;
                }
            }
            result.add(finalizedRow.toString());
        }
        for (Char2ObjectMap.Entry<Object> entry : inputs2.char2ObjectEntrySet()) {
            result.add(entry.getCharKey());
            result.add(entry.getValue());
        }
        GroovyScript.LOGGER.info("Creating recipe {}", result);
        return result.toArray();
    }
}
