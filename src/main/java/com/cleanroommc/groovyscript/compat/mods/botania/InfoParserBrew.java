package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.Brew;

public class InfoParserBrew extends GenericInfoParser<vazkii.botania.api.brew.Brew> {

    public static final InfoParserBrew instance = new InfoParserBrew();

    @Override
    public String id() {
        return "brew";
    }

    @Override
    public String name() {
        return "Brew";
    }

    @Override
    public String text(@NotNull Brew entry, boolean colored, boolean prettyNbt) {
        return Botania.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) return;
        NBTTagCompound tag = info.getStack().getTagCompound();
        if (tag == null) return;
        String key = tag.getString("brewKey");
        if (key.isEmpty()) return;
        Brew brew = BotaniaAPI.brewMap.get(key);
        if (brew == null) return;
        instance.add(info.getMessages(), brew, info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), brew.getUnlocalizedName(), info.isPrettyNbt());
    }

}
