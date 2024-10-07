package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.StyleConstant;
import com.cleanroommc.groovyscript.helper.ingredient.NbtHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class InfoParserNBT extends GenericInfoParser<NBTTagCompound> {

    public static final InfoParserNBT instance = new InfoParserNBT();

    @Override
    public int priority() {
        return 500;
    }

    @Override
    public String id() {
        return "nbt";
    }

    @Override
    public String name() {
        return "NBT";
    }

    @Override
    public String plural() {
        return name();
    }

    private String trimText() {
        return StyleConstant.ERROR + "(trimmed)";
    }

    /**
     * if the length is above 300 characters, we trim to the first space after that,
     * and we there's more than 8 lines, we trim to that.
     * this prevents the entire chatbox from being filled with just the NBT data.
     * note that we do *have* to use {@link StringUtils#indexOf} because
     * if the cut happens just after a section sign it would break the formatting of {@link #trimText}.
     */
    @Override
    public String text(@NotNull NBTTagCompound entry, boolean colored, boolean prettyNbt) {
        String msg = NbtHelper.toGroovyCode(entry, prettyNbt, true);
        if (msg.length() > 300) {
            int endIndex = StringUtils.indexOf(msg, " ", 300);
            return endIndex == -1 ? msg : msg.substring(0, StringUtils.indexOf(msg, " ", 300)) + trimText();
        }
        int trimLocation = StringUtils.ordinalIndexOf(msg, "\n", 8);
        return trimLocation == -1 ? msg : msg.substring(0, trimLocation) + "\n" + trimText();
    }

    @Override
    public String copyText(@NotNull NBTTagCompound entry, boolean prettyNbt) {
        return NbtHelper.toGroovyCode(entry, prettyNbt, false);
    }

    @Override
    public ITextComponent information(@NotNull NBTTagCompound entry, boolean prettyNbt) {
        return information(copyText(entry, prettyNbt), msg(entry, prettyNbt));
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getEntity() != null) {
            if (info.getEntity() instanceof EntityPlayer) {
                NBTTagCompound nbt = new NBTTagCompound();
                info.getEntity().writeToNBT(nbt);
                instance.add(info.getMessages(), nbt, info.isPrettyNbt());
            } else {
                instance.add(info.getMessages(), info.getEntity().serializeNBT(), info.isPrettyNbt());
            }
        } else if (info.getTileEntity() != null) {
            instance.add(info.getMessages(), info.getTileEntity().serializeNBT(), info.isPrettyNbt());
        } else if (!info.getStack().isEmpty()) {
            instance.add(info.getMessages(), info.getStack().serializeNBT(), info.isPrettyNbt());
        }
    }

}
