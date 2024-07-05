package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class InfoParserTileEntity extends GenericInfoParser<TileEntity> {

    public static final InfoParserTileEntity instance = new InfoParserTileEntity();

    @Override
    public String id() {
        return "tileentity";
    }

    @Override
    public String name() {
        return "Tile NBT";
    }

    @Override
    public String plural() {
        return name();
    }

    private String trimText() {
        return TextFormatting.RED + "(trimmed)";
    }

    /**
     * if the length is above 300 characters, we trim to the first space after that,
     * and we there's more than 8 lines, we trim to that.
     * this prevents the entire chatbox from being filled with just the NBT data.
     * note that we do *have* to use {@link StringUtils#indexOf} because
     * if the cut happens just after a section sign it would break the formatting of {@link #trimText}.
     */
    @Override
    public String text(@NotNull TileEntity entry, boolean colored, boolean prettyNbt) {
        String msg = NbtHelper.toGroovyCode(entry.serializeNBT(), prettyNbt, true);
        if (msg.length() > 300) {
            int endIndex = StringUtils.indexOf(msg, " ", 300);
            return endIndex == -1 ? msg : msg.substring(0, StringUtils.indexOf(msg, " ", 300)) + trimText();
        }
        int trimLocation = StringUtils.ordinalIndexOf(msg, "\n", 8);
        return trimLocation == -1 ? msg : msg.substring(0, trimLocation) + "\n" + trimText();
    }

    @Override
    public String copyText(@NotNull TileEntity entry, boolean prettyNbt) {
        return NbtHelper.toGroovyCode(entry.serializeNBT(), prettyNbt, false);
    }

    @Override
    public ITextComponent information(@NotNull TileEntity entry, boolean prettyNbt) {
        return information(copyText(entry, prettyNbt), msg(entry, prettyNbt));
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getTileEntity() == null) return;
        instance.add(info.getMessages(), info.getTileEntity(), info.isPrettyNbt());
    }

}
