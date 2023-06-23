package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.roots.ModifierRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.modifiers.Modifier;
import epicsquid.roots.modifiers.ModifierRegistry;
import epicsquid.roots.spell.SpellBase;
import net.minecraft.util.ResourceLocation;

public class Modifiers extends VirtualizedRegistry<ResourceLocation> {

    public Modifiers() {
        super();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(ModifierRegistryAccessor.getDisabledModifiers()::remove);
        restoreFromBackup().forEach(ModifierRegistryAccessor.getDisabledModifiers()::add);
    }

    public boolean disable(String name) {
        return disable(name.contains(":") ? new ResourceLocation(name) : new ResourceLocation("roots", name));
    }

    public boolean disable(ResourceLocation rl) {
        Modifier modifier = ModifierRegistry.get(rl);
        if (modifier == null) {
            GroovyLog.msg("Error disabling modifier {}", rl).error().post();
        } else {
            ModifierRegistry.disable(modifier);
            addScripted(modifier.getRegistryName());
            return true;
        }
        return false;
    }

    public boolean disable(Modifier modifier) {
        if (ModifierRegistry.get(modifier) == null) {
            GroovyLog.msg("Error disabling modifier {}", modifier).error().post();
            return false;
        }
        ModifierRegistry.disable(modifier);
        addScripted(modifier.getRegistryName());
        return true;
    }

    public boolean disable(SpellBase spell) {
        for (Modifier mod : spell.getModifiers()) {
            ModifierRegistry.disable(mod);
            addScripted(mod.getRegistryName());
        }
        return true;
    }

    public boolean enable(String name) {
        return enable(name.contains(":") ? new ResourceLocation(name) : new ResourceLocation("roots", name));
    }

    public boolean enable(ResourceLocation rl) {
        Modifier modifier = ModifierRegistry.get(rl);
        if (modifier == null) {
            GroovyLog.msg("Error enabling modifier {}", rl).error().post();
        } else {
            ModifierRegistryAccessor.getDisabledModifiers().remove(rl);
            addBackup(rl);
            return true;
        }
        return false;
    }

    public boolean enable(Modifier modifier) {
        if (ModifierRegistry.get(modifier) == null) {
            GroovyLog.msg("Error enabling modifier {}", modifier).error().post();
            return false;
        }
        ModifierRegistryAccessor.getDisabledModifiers().remove(modifier.getRegistryName());
        addScripted(modifier.getRegistryName());
        return true;
    }

    public boolean enable(SpellBase spell) {
        for (Modifier mod : spell.getModifiers()) {
            ModifierRegistryAccessor.getDisabledModifiers().remove(mod.getRegistryName());
            addBackup(mod.getRegistryName());
        }
        return true;
    }

    public void disableAll() {
        for (Modifier mod : ModifierRegistry.getModifiers()) {
            ModifierRegistry.disable(mod);
            addScripted(mod.getRegistryName());
        }
    }

    public void enableAll() {
        for (Modifier mod : ModifierRegistry.getModifiers()) {
            ModifierRegistryAccessor.getDisabledModifiers().remove(mod.getRegistryName());
            addBackup(mod.getRegistryName());
        }
    }

    public SimpleObjectStream<ResourceLocation> streamRecipes() {
        return new SimpleObjectStream<>(ModifierRegistryAccessor.getDisabledModifiers()).setRemover(this::disable);
    }
}
