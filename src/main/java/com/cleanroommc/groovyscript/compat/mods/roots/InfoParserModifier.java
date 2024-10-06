package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import epicsquid.roots.modifiers.Modifier;
import epicsquid.roots.modifiers.ModifierRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InfoParserModifier extends GenericInfoParser<Modifier> {

    public static final InfoParserModifier instance = new InfoParserModifier();

    @Override
    public String id() {
        return "modifier";
    }

    @Override
    public String name() {
        return "Modifier";
    }

    @Override
    public String text(@NotNull Modifier entry, boolean colored, boolean prettyNbt) {
        return Roots.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        NBTTagCompound tag = info.getStack().getTagCompound();
        if (tag == null) return;
        String key = tag.getString("modifier");
        if (key.isEmpty()) return;
        Modifier modifier = ModifierRegistry.get(new ResourceLocation(key));
        if (modifier == null) return;
        instance.add(info.getMessages(), modifier, info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), modifier.getTranslationKey(), info.isPrettyNbt());
    }
}
