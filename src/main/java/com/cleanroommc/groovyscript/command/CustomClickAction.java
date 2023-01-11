package com.cleanroommc.groovyscript.command;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.text.event.ClickEvent;

import java.util.Map;
import java.util.function.Consumer;

public class CustomClickAction {

    public static final String PREFIX = "$custom$";
    private static final Map<String, Consumer<String>> ACTIONS = new Object2ObjectOpenHashMap<>();

    public static ClickEvent makeCustomClickEvent(String name, String value) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, PREFIX + name + "::" + value);
    }

    public static ClickEvent makeCopyEvent(String value) {
        return makeCustomClickEvent("copy", value);
    }

    public static void registerAction(String name, Consumer<String> action) {
        if (ACTIONS.containsKey(name)) {
            throw new IllegalArgumentException("Action " + name + " already exists!");
        }
        ACTIONS.put(name, action);
    }

    public static boolean runActionHook(String fullId) {
        String[] parts = fullId.split("::", 2);
        if (parts.length < 2) return false;
        Consumer<String> action = ACTIONS.get(parts[0]);
        if (action == null) {
            return false;
        }
        action.accept(parts[1]);
        return true;
    }
}
