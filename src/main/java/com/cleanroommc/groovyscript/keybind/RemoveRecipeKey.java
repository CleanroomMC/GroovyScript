package com.cleanroommc.groovyscript.keybind;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.compat.mods.jei.JeiRemovalHelper;
import mezz.jei.api.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.input.Keyboard;

public class RemoveRecipeKey extends GroovyScriptKeybinds.Key {

    public RemoveRecipeKey() {
        super("remove_recipe_from_jei", KeyConflictContext.GUI, KeyModifier.CONTROL, Keyboard.KEY_X);
    }

    public static void createKeybind() {
        if (ModSupport.JEI.isLoaded()) GroovyScriptKeybinds.addKey(new RemoveRecipeKey());
    }

    @Override
    public boolean isValid() {
        return Minecraft.getMinecraft().currentScreen instanceof IRecipesGui && !JeiPlugin.jeiRuntime.getIngredientListOverlay().hasKeyboardFocus();
    }

    @Override
    public void handleKeybind() {
        JeiRemovalHelper.getRemovalMethod();
    }
}
