package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.Closure;
import net.minecraft.nbt.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class NbtHelper {

    public static Closure<Object> makeNbtPredicate(Predicate<NBTTagCompound> predicate) {
        return new LambdaClosure<>(args -> predicate.test(((NBTTagCompound) args[0])));
    }

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
            for (Object lo : (List<?>) o) {
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

    public static String toGroovyCode(NBTTagCompound nbt, boolean pretty, boolean colored) {
        return toGroovyCode(nbt, 0, pretty, colored);
    }

    public static String toGroovyCode(NBTTagCompound nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append('[');
        if (!nbt.isEmpty()) {
            indent++;
            for (String key : nbt.getKeySet()) {
                newLine(builder, indent, pretty);
                if (colored) builder.append(TextFormatting.GREEN);
                builder.append('\'').append(key).append('\'');
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append(": ");
                builder.append(toGroovyCode(nbt.getTag(key), indent, pretty, colored));
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            indent--;
            newLine(builder, indent, pretty);
            if (colored) builder.append(TextFormatting.GRAY);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagList nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append('[');
        if (!nbt.isEmpty()) {
            indent++;
            for (NBTBase nbtBase : nbt) {
                newLine(builder, indent, pretty);
                builder.append(toGroovyCode(nbtBase, indent, pretty, colored));
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            indent--;
            newLine(builder, indent, pretty);
            if (colored) builder.append(TextFormatting.GRAY);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagByteArray nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append('[');
        if (!nbt.isEmpty()) {
            newLine(builder, indent, pretty);
            for (byte value : nbt.getByteArray()) {
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append("(byte) ");
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(value);
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            newLine(builder, indent, pretty);
            if (colored) builder.append(TextFormatting.GRAY);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagIntArray nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(TextFormatting.GRAY);
        builder.append('[');
        if (!nbt.isEmpty()) {
            newLine(builder, indent, pretty);
            for (int value : nbt.getIntArray()) {
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(value);
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            newLine(builder, indent, pretty);
            if (colored) builder.append(TextFormatting.GRAY);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTBase nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        switch (nbt.getId()) {
            //TAG_LONG_ARRAY
            case Constants.NBT.TAG_INT_ARRAY: {
                if (colored) builder.append(TextFormatting.GREEN);
                builder.append(toGroovyCode((NBTTagIntArray) nbt, indent, pretty, colored));
                break;
            }
            case Constants.NBT.TAG_COMPOUND: {
                builder.append(toGroovyCode((NBTTagCompound) nbt, indent, pretty, colored));
                break;
            }
            case Constants.NBT.TAG_LIST: {
                builder.append(toGroovyCode((NBTTagList) nbt, indent, pretty, colored));
                break;
            }
            case Constants.NBT.TAG_STRING: {
                if (colored) builder.append(TextFormatting.GREEN);
                builder.append('\'')
                        .append(((NBTTagString) nbt).getString())
                        .append('\'');
                break;
            }
            case Constants.NBT.TAG_BYTE_ARRAY: {
                if (colored) builder.append(TextFormatting.GREEN);
                builder.append(toGroovyCode((NBTTagByteArray) nbt, indent, pretty, colored));
                break;
            }
            case Constants.NBT.TAG_DOUBLE: {
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(((NBTTagDouble) nbt).getDouble())
                        .append("D");
                break;
            }
            case Constants.NBT.TAG_FLOAT: {
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(((NBTTagFloat) nbt).getFloat())
                        .append("F");
                break;
            }
            case Constants.NBT.TAG_LONG: {
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(((NBTTagLong) nbt).getLong())
                        .append("L");
                break;
            }
            case Constants.NBT.TAG_INT: {
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(((NBTTagInt) nbt).getInt());
                break;
            }
            case Constants.NBT.TAG_SHORT: {
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append("(short) ");
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(((NBTTagShort) nbt).getShort());
                break;
            }
            case Constants.NBT.TAG_BYTE: {
                if (colored) builder.append(TextFormatting.GRAY);
                builder.append("(byte) ");
                if (colored) builder.append(TextFormatting.GOLD);
                builder.append(((NBTTagByte) nbt).getByte());
                break;
            }
            default:
                throw new IllegalArgumentException(nbt.toString());
        }
        return builder.toString();
    }

    private static void newLine(StringBuilder builder, int indents, boolean pretty) {
        if (!pretty) return;
        builder.append('\n');
        for (int i = 0; i < indents; i++) builder.append("    ");
    }
}
