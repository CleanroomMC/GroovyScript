package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.helper.recipe.FluidStack;
import com.cleanroommc.groovyscript.helper.recipe.ItemStack;
import com.cleanroommc.groovyscript.helper.recipe.OreDictIngredient;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class BracketHandler {

    private static final Map<String, Function<String, Object>> bracketHandlers = new HashMap<>();
    public static final Function<String, Object> itemStackBracketHandler = s -> {
        try {
            return ItemStack.parse(s);
        } catch (Exception e) {
            return null;
        }
    };

    public static void registerBracketHandler(String key, Function<String, Object> handler) {
        if (key == null || key.isEmpty() || handler == null) throw new NullPointerException();
        if (bracketHandlers.containsKey(key)) {
            throw new IllegalArgumentException("Bracket handler already exists for key " + key);
        }
        bracketHandlers.put(key, handler);
    }

    public static Object handleBracket(String s) {
        String[] parts = s.split(":", 2);
        if (parts.length == 0) return s;
        Function<String, Object> bracketHandler = bracketHandlers.get(parts[0]);
        Object result = bracketHandler == null ? itemStackBracketHandler.apply(s) : bracketHandler.apply(parts[1]);
        if (result == null) {
            throw new NoSuchElementException("Could not find game object for <" + s + ">!");
        }
        return result;
    }

    public static void init() {
        registerBracketHandler("ore", OreDictIngredient::new);
        registerBracketHandler("item", itemStackBracketHandler);
        registerBracketHandler("liquid", FluidStack::parse);
        registerBracketHandler("fluid", FluidStack::parse);
    }
}
