package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2CharArrayMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Recipes {

    public void addShaped(String name, String output, List<List<String>> inputMatrix) {
        IRecipe recipe = new ShapedOreRecipe(null, getItemStack(output), makeShapedMatrix(inputMatrix))
                .setMirrored(false)
                .setRegistryName(name);
        ReloadableRegistryManager.registerRecipe(recipe);
    }

    private static ItemStack getItemStack(String item) {
        String[] parts = item.split(":");
        if (parts.length < 2) throw new IllegalArgumentException();
        Item item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item1 == null) {
            throw new NoSuchElementException("Can't find item for " + item);
        }
        int meta = 0;
        if (parts.length > 2) {
            try {
                meta = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        return new ItemStack(item1, 1, meta);
    }

    private static Object[] makeShapedMatrix(List<List<String>> inputMatrix) {
        List<Object> result = new ArrayList<>();
        Object2CharArrayMap<String> inputs = new Object2CharArrayMap<>();
        Char2ObjectArrayMap<Object> inputs2 = new Char2ObjectArrayMap<>();
        char c = 'a';
        for (List<String> row : inputMatrix) {
            StringBuilder finalizedRow = new StringBuilder();
            for (String input : row) {
                if (input == null || input.isEmpty()) {
                    finalizedRow.append(' ');
                    continue;
                }
                if (inputs.containsKey(input)) {
                    finalizedRow.append(inputs.getChar(input));
                } else {
                    inputs.put(input, c);
                    finalizedRow.append(c);
                    inputs2.put(c, getIngredient(input));
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

    private static Object getIngredient(String raw) {
        try {
            return getItemStack(raw);
        } catch (Exception e) {
            return raw;
        }
    }
}
