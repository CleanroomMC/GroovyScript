package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.roots.ModifierRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import epicsquid.roots.modifiers.Modifier;
import epicsquid.roots.modifiers.ModifierRegistry;
import epicsquid.roots.spell.SpellBase;
import net.minecraft.util.ResourceLocation;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Modifiers extends VirtualizedRegistry<ResourceLocation> {

    @Override
    public void onReload() {
        removeScripted().forEach(ModifierRegistryAccessor.getDisabledModifiers()::remove);
        restoreFromBackup().forEach(ModifierRegistryAccessor.getDisabledModifiers()::add);
    }

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.disable0")
    public boolean disable(String name) {
        return disable(name.contains(":") ? new ResourceLocation(name) : new ResourceLocation("roots", name));
    }

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.disable1")
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

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.disable2")
    public boolean disable(Modifier modifier) {
        if (ModifierRegistry.get(modifier) == null) {
            GroovyLog.msg("Error disabling modifier {}", modifier).error().post();
            return false;
        }
        ModifierRegistry.disable(modifier);
        addScripted(modifier.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.disable3", example = @Example("spell('spell_geas')"))
    public boolean disable(SpellBase spell) {
        for (Modifier mod : spell.getModifiers()) {
            ModifierRegistry.disable(mod);
            addScripted(mod.getRegistryName());
        }
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.enable0", example = @Example("'extended_geas'"), type = MethodDescription.Type.ADDITION)
    public boolean enable(String name) {
        return enable(name.contains(":") ? new ResourceLocation(name) : new ResourceLocation("roots", name));
    }

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.enable1", example = @Example("resource('roots:animal_savior')"), type = MethodDescription.Type.ADDITION)
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

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.enable2", example = @Example("modifier('roots:weakened_response')"), type = MethodDescription.Type.ADDITION)
    public boolean enable(Modifier modifier) {
        if (ModifierRegistry.get(modifier) == null) {
            GroovyLog.msg("Error enabling modifier {}", modifier).error().post();
            return false;
        }
        ModifierRegistryAccessor.getDisabledModifiers().remove(modifier.getRegistryName());
        addScripted(modifier.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.roots.modifiers.enable3", type = MethodDescription.Type.ADDITION)
    public boolean enable(SpellBase spell) {
        for (Modifier mod : spell.getModifiers()) {
            ModifierRegistryAccessor.getDisabledModifiers().remove(mod.getRegistryName());
            addBackup(mod.getRegistryName());
        }
        return true;
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = @Example(commented = true))
    public void disableAll() {
        for (Modifier mod : ModifierRegistry.getModifiers()) {
            ModifierRegistry.disable(mod);
            addScripted(mod.getRegistryName());
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void enableAll() {
        for (Modifier mod : ModifierRegistry.getModifiers()) {
            ModifierRegistryAccessor.getDisabledModifiers().remove(mod.getRegistryName());
            addBackup(mod.getRegistryName());
        }
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ResourceLocation> streamRecipes() {
        return new SimpleObjectStream<>(ModifierRegistryAccessor.getDisabledModifiers()).setRemover(this::disable);
    }
}
