package com.cleanroommc.groovyscript.compat.mods.roots;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import epicsquid.roots.spell.SpellBase;
import epicsquid.roots.spell.SpellRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class InfoParserSpell extends GenericInfoParser<SpellBase> {

    public static final InfoParserSpell instance = new InfoParserSpell();

    @Override
    public String id() {
        return "spell";
    }

    @Override
    public String name() {
        return "Spell";
    }

    @Override
    public String text(@NotNull SpellBase entry, boolean colored, boolean prettyNbt) {
        return Roots.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        NBTTagCompound tag = info.getStack().getTagCompound();
        if (tag == null) return;
        NBTTagCompound spell_storage = tag.getCompoundTag("spell_storage");
        if (spell_storage.isEmpty()) return;
        String key = spell_storage.getString("s");
        if (key.isEmpty()) return;
        SpellBase spell = SpellRegistry.getSpell(new ResourceLocation(key));
        if (spell == null) return;
        instance.add(info.getMessages(), spell, info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), spell.getTranslationKey(), info.isPrettyNbt());
    }

}
