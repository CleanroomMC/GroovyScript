package com.cleanroommc.groovyscript.compat.mods.appliedenergistics2;

import appeng.api.AEApi;
import appeng.api.config.TunnelType;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.compat.vanilla.command.infoparser.GenericInfoParser;
import org.jetbrains.annotations.NotNull;

public class InfoParserTunnel extends GenericInfoParser<TunnelType> {

    public static final InfoParserTunnel instance = new InfoParserTunnel();

    @Override
    public String id() {
        return "tunnel";
    }

    @Override
    public String name() {
        return "Tunnel Attunement Type";
    }

    @Override
    public String text(@NotNull TunnelType entry, boolean colored, boolean prettyNbt) {
        return AppliedEnergistics2.asGroovyCode(entry, colored);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void parse(InfoParserPackage info) {
//        if (info.getBlock())
        if (info.getStack().isEmpty()) return;

        TunnelType tunnelType = AEApi.instance().registries().p2pTunnel().getTunnelTypeByItem(info.getStack());
        if (tunnelType == null) return;
        instance.add(info.getMessages(), tunnelType, info.isPrettyNbt());
    }
}
