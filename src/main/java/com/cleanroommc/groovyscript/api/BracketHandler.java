package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.helper.recipe.ItemStack;
import com.cleanroommc.groovyscript.helper.recipe.OreDictIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BracketHandler {

    public static final List<Function<String, Object>> handlers = new ArrayList<>();

    public static void registerBracketHandler(Function<String, Object> handler) {
        handlers.add(handler);
    }

    public static Object handleBracket(String s) {
        for (Function<String, Object> handler : handlers) {
            Object result = handler.apply(s);
            if (result != null) return result;
        }
        return s;
    }

    public static void init() {
        registerBracketHandler(s -> {
            String[] parts = s.split(":");
            if (parts.length == 2 && parts[0].equals("ore")) {
                return new OreDictIngredient(parts[1]);
            }
            return null;
        });
        registerBracketHandler(s -> {
            try {
                return ItemStack.parse(s);
            } catch (Exception e) {
                return null;
            }
        });
    }
}
