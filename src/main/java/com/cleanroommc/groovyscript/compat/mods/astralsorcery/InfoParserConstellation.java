package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.InfoParserTranslationKey;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class InfoParserConstellation extends GenericInfoParser<IConstellation> {

    public static final InfoParserConstellation instance = new InfoParserConstellation();

    @Override
    public String id() {
        return "constellation";
    }

    @Override
    public String name() {
        return "Constellation";
    }

    @Override
    public String text(@NotNull IConstellation entry, boolean colored, boolean prettyNbt) {
        return AstralSorcery.asGroovyCode(entry, colored);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) {
            if (info.getEntity() == null) return;
            DataActiveCelestials s = SyncDataHolder.getDataClient("AstralConstellations");
            Collection<IConstellation> constellations = s.getActiveConstellations(info.getEntity().dimension);
            if (constellations == null) return;
            instance.add(info.getMessages(), constellations, info.isPrettyNbt());
            InfoParserTranslationKey.instance.add(info.getMessages(), constellations.stream().map(IConstellation::getUnlocalizedName).collect(Collectors.toList()), info.isPrettyNbt());
            return;
        }

        if (!NBTHelper.hasPersistentData(info.getStack())) return;
        IConstellation constellation = IConstellation.readFromNBT(NBTHelper.getPersistentData(info.getStack()));
        if (constellation == null) return;
        instance.add(info.getMessages(), constellation, info.isPrettyNbt());
        InfoParserTranslationKey.instance.add(info.getMessages(), constellation.getUnlocalizedName(), info.isPrettyNbt());
    }
}
