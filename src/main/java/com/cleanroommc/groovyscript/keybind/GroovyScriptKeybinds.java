package com.cleanroommc.groovyscript.keybind;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = GroovyScript.ID, value = Side.CLIENT)
public class GroovyScriptKeybinds extends KeyBinding {

    private static final List<Key> keys = new ArrayList<>();

    protected GroovyScriptKeybinds(GroovyScriptKeybinds.Key key) {
        super(key.getDescription(), key.getKeyConflictContext(), key.getKeyModifier(), key.getKeyCode(), GroovyScript.NAME);
        ClientRegistry.registerKeyBinding(this);
    }

    public static void addKey(GroovyScriptKeybinds.Key key) {
        keys.add(key);
    }

    public static void initialize() {
        ReloadKey.createKeybind();
        RemoveRecipeKey.createKeybind();

        for (Key key : keys) {
            key.getKey();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        for (Key key : keys) {
            if (key.isPressed()) key.handleKeybind();
        }
    }

    @SubscribeEvent
    public static void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();

        for (Key key : keys) {
            if (key.isPressed(typedChar, eventKey)) key.handleKeybind();
        }
    }

    public abstract static class Key {

        private final String name;
        private final IKeyConflictContext keyConflictContext;
        private final KeyModifier keyModifier;
        private final int keyCode;

        private KeyBinding key;

        public Key(String name, int keyCode) {
            this(name, KeyConflictContext.UNIVERSAL, keyCode);
        }

        public Key(String name, IKeyConflictContext keyConflictContext, int keyCode) {
            this(name, keyConflictContext, KeyModifier.NONE, keyCode);
        }

        public Key(String name, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode) {
            this.name = name;
            this.keyConflictContext = keyConflictContext;
            this.keyModifier = keyModifier;
            this.keyCode = keyCode;
        }

        public abstract boolean isValid();

        public abstract void handleKeybind();

        public String getName() {
            return name;
        }

        public IKeyConflictContext getKeyConflictContext() {
            return keyConflictContext;
        }

        public KeyModifier getKeyModifier() {
            return keyModifier;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public KeyBinding getKey() {
            if (key == null) key = new GroovyScriptKeybinds(this);
            return key;
        }

        public void setKey(KeyBinding key) {
            this.key = key;
        }

        public boolean isPressed() {
            return isValid() && getKey().isPressed();
        }

        public boolean isPressed(char typedChar, int keyCode) {
            return isValid() && getKey().isActiveAndMatches(keyCode);
        }

        public String getDescription() {
            return String.format("keybind.%s.%s", GroovyScript.ID, name);
        }
    }

}
