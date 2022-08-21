package com.cleanroommc.groovyscript.helper;

import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class NbtHelper {

    public static final Predicate<NBTTagCompound> MATCH_ANY = nbt -> true;

    public static boolean containsNbt(NBTTagCompound nbtContainer, NBTTagCompound nbtMatcher) {
        if (nbtMatcher == null || nbtMatcher.isEmpty()) return true;
        if (nbtContainer == null || nbtContainer.isEmpty()) return false;
        for (String key : nbtMatcher.getKeySet()) {
            NBTBase nbt1 = nbtContainer.getTag(key);
            if (nbt1 == null) return false;
            NBTBase nbt2 = nbtMatcher.getTag(key);
            if (!matches(nbt1, nbt2, true)) {
                return false;
            }
        }
        return true;
    }

    public static boolean matches(NBTBase nbt1, NBTBase nbt2, boolean contains) {
        if (!contains) return nbt1.equals(nbt2);
        if (nbt1.getId() != nbt2.getId()) return false;
        if (nbt1.getId() == Constants.NBT.TAG_COMPOUND) {
            return containsNbt((NBTTagCompound) nbt1, (NBTTagCompound) nbt2);
        }
        return nbt1.equals(nbt2);
    }

    public static NBTTagCompound ofMap(Map<String, Object> map) {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            NBTBase base = toNbt(entry.getValue());
            nbt.setTag(entry.getKey(), base);
        }
        return nbt;
    }

    public static NBTBase toNbt(Object o) {
        if (o instanceof Map) {
            return ofMap((Map<String, Object>) o);
        }
        if (o instanceof List) {
            NBTTagList list = new NBTTagList();
            byte type = 0;
            for (Object lo : list) {
                NBTBase lNbt = toNbt(lo);
                if (type == 0) {
                    type = lNbt.getId();
                } else if (type != lNbt.getId()) {
                    throw new IllegalArgumentException("A NBTTagList can only contain elements of one type!");
                }
                list.appendTag(lNbt);
            }
            return list;
        }
        if (o instanceof Integer) {
            return new NBTTagInt((Integer) o);
        }
        if (o instanceof Long) {
            return new NBTTagLong((Long) o);
        }
        if (o instanceof Short) {
            return new NBTTagShort((Short) o);
        }
        if (o instanceof Byte) {
            return new NBTTagByte((Byte) o);
        }
        if (o instanceof Boolean) {
            return new NBTTagByte((byte) ((boolean) o ? 1 : 0));
        }
        if (o instanceof Float) {
            return new NBTTagFloat((Float) o);
        }
        if (o instanceof Double) {
            return new NBTTagDouble((Double) o);
        }
        if (o instanceof String) {
            return new NBTTagString((String) o);
        }
        throw new IllegalArgumentException("Error parsing Object to NBT: Invalid type " + o.getClass());
    }

    public static String toGroovyCode(NBTTagCompound nbt, boolean pretty) {
        return toGroovyCode(nbt, 0, pretty);
    }

    public static String toGroovyCode(NBTTagCompound nbt, int indent, boolean pretty) {
        StringBuilder builder = new StringBuilder();
        newLine(builder, indent, pretty);
        builder.append('[');
        indent++;
        for (String key : nbt.getKeySet()) {
            newLine(builder, indent, pretty);
            builder.append(key)
                    .append(": ")
                    .append(toGroovyCode(nbt.getTag(key), indent, pretty))
                    .append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        indent--;
        newLine(builder, indent, pretty);
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagList nbt, int indent, boolean pretty) {
        StringBuilder builder = new StringBuilder();
        newLine(builder, indent, pretty);
        builder.append('[');
        indent++;
        for (NBTBase nbtBase : nbt) {
            newLine(builder, indent, pretty);
            builder.append(toGroovyCode(nbtBase, indent, pretty))
                    .append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        indent--;
        newLine(builder, indent, pretty);
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTBase nbt, int indent, boolean pretty) {
        if (nbt.getId() == Constants.NBT.TAG_COMPOUND) {
            return toGroovyCode((NBTTagCompound) nbt, indent, pretty);
        }
        if (nbt.getId() == Constants.NBT.TAG_LIST) {
            return toGroovyCode((NBTTagList) nbt, indent, pretty);
        }
        if (nbt.getId() == Constants.NBT.TAG_INT) {
            return String.valueOf(((NBTTagInt) nbt).getInt());
        }
        if (nbt.getId() == Constants.NBT.TAG_LONG) {
            return ((NBTTagLong) nbt).getLong() + "L";
        }
        if (nbt.getId() == Constants.NBT.TAG_SHORT) {
            return "(short) " + String.valueOf(((NBTTagShort) nbt).getShort());
        }
        if (nbt.getId() == Constants.NBT.TAG_BYTE) {
            return "(byte) " + String.valueOf(((NBTTagByte) nbt).getByte());
        }
        if (nbt.getId() == Constants.NBT.TAG_FLOAT) {
            return ((NBTTagFloat) nbt).getFloat() + "F";
        }
        if (nbt.getId() == Constants.NBT.TAG_DOUBLE) {
            return ((NBTTagDouble) nbt).getDouble() + "D";
        }
        if (nbt.getId() == Constants.NBT.TAG_STRING) {
            return '"' + ((NBTTagString) nbt).getString() + '"';
        }
        throw new IllegalArgumentException(nbt.toString());
    }

    private static void newLine(StringBuilder builder, int indents, boolean pretty) {
        if (!pretty) return;
        builder.append('\n');
        for (int i = 0; i < indents; i++) builder.append('\t');
    }
}
