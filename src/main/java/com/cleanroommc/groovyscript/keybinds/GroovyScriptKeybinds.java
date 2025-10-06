package com.cleanroommc.groovyscript.keybinds;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collection;

@SideOnly(Side.CLIENT)
public class GroovyScriptKeybinds {

    private static final Collection<Key> KEYS = new ArrayList<>();

    public static void initialize() {
        addKey(ReloadKey.createKeybind());
        addKey(CopyKey.createKeybind());
    }

    private static void addKey(Key key) {
        if (key != null) KEYS.add(key);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        for (Key key : KEYS) {
            if (key.isPressed()) key.runOperation();
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent event) {
        for (Key key : KEYS) {
            if (key.isPressed()) key.runOperation();
        }
    }

    @SubscribeEvent
    public static void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Post event) {
        if (!Keyboard.getEventKeyState()) return; // only activate on press, not on release
        char typedChar = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();

        for (Key key : KEYS) {
            if (key.isPressed(typedChar, eventKey)) {
                key.runOperation();
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Post event) {
        if (!Mouse.getEventButtonState()) return; // only activate on click, not on release or movement
        for (Key key : KEYS) {
            if (key.isValid() && GameSettings.isKeyDown(key.getKey()) && key.getKey().getKeyConflictContext().isActive()) {
                key.runOperation();
                event.setCanceled(true);
                return;
            }
        }
    }

    private static KeyBinding createKeybind(boolean setByDefault, Key key) {
        var binding = setByDefault
                      ? new KeyBinding(key.getDescription(), key.getKeyConflictContext(), key.getKeyModifier(), key.getKeyCode(), GroovyScript.NAME)
                      : new KeyBinding(key.getDescription(), key.getKeyConflictContext(), KeyModifier.NONE, Keyboard.KEY_NONE, GroovyScript.NAME);
        ClientRegistry.registerKeyBinding(binding);
        return binding;
    }

    public abstract static class Key {

        private final String name;
        private final IKeyConflictContext keyConflictContext;
        private final KeyModifier keyModifier;
        private final int keyCode;

        private final KeyBinding key;

        public Key(String name, int keyCode) {
            this(name, KeyConflictContext.UNIVERSAL, KeyModifier.NONE, keyCode);
        }

        public Key(String name, IKeyConflictContext keyConflictContext, int keyCode) {
            this(name, keyConflictContext, KeyModifier.NONE, keyCode);
        }

        public Key(String name, KeyModifier keyModifier, int keyCode) {
            this(name, KeyConflictContext.UNIVERSAL, keyModifier, keyCode);
        }

        public Key(String name, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode) {
            this.name = name;
            this.keyConflictContext = keyConflictContext;
            this.keyModifier = keyModifier;
            this.keyCode = keyCode;
            this.key = createKeybind(isSetByDefault(), this);
        }

        protected boolean isSetByDefault() {
            return GroovyScript.getRunConfig().isDebug();
        }

        public abstract boolean isValid();

        public abstract void runOperation();

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
            return key;
        }

        public boolean isPressed() {
            return isValid() && key.isPressed() && key.getKeyConflictContext().isActive();
        }

        public boolean isPressed(char typedChar, int keyCode) {
            return isValid() && key.isActiveAndMatches(keyCode);
        }

        public String getDescription() {
            return String.format("keybind.%s.%s", GroovyScript.ID, name);
        }
    }
}
