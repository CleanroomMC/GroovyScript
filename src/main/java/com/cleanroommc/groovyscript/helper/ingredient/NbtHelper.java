package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.helper.StyleConstant;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.Closure;
import net.minecraft.nbt.*;
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
        if (o instanceof List<?>objects) {
            NBTTagList list = new NBTTagList();
            byte type = 0;
            for (Object lo : objects) {
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

        if (o instanceof Integer x) return new NBTTagInt(x);
        if (o instanceof Long x) return new NBTTagLong(x);
        if (o instanceof Short x) return new NBTTagShort(x);
        if (o instanceof Byte x) return new NBTTagByte(x);
        if (o instanceof Boolean x) return new NBTTagByte((byte) (x ? 1 : 0));
        if (o instanceof Float x) return new NBTTagFloat(x);
        if (o instanceof Double x) return new NBTTagDouble(x);
        if (o instanceof String x) return new NBTTagString(x);

        throw new IllegalArgumentException("Error parsing Object to NBT: Invalid type " + o.getClass());
    }

    public static String toGroovyCode(NBTTagByte nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.CLASS);
        builder.append("(byte) ");
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(nbt.getByte());
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagShort nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.CLASS);
        builder.append("(short) ");
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(nbt.getShort());
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagInt nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(nbt.getInt());
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagLong nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(nbt.getLong());
        if (colored) builder.append(StyleConstant.CLASS);
        builder.append("l");
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagFloat nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(nbt.getFloat());
        if (colored) builder.append(StyleConstant.CLASS);
        builder.append("f");
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagDouble nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.NUMBER);
        builder.append(nbt.getDouble());
        if (colored) builder.append(StyleConstant.CLASS);
        builder.append("d");
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagByteArray nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append('[');
        if (!nbt.isEmpty()) {
            newLine(builder, indent, pretty);
            for (byte value : nbt.getByteArray()) {
                if (colored) builder.append(StyleConstant.CLASS);
                builder.append("(byte) ");
                if (colored) builder.append(StyleConstant.NUMBER);
                builder.append(value);
                if (colored) builder.append(StyleConstant.BASE);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            newLine(builder, indent, pretty);
            if (colored) builder.append(StyleConstant.BASE);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagString nbt, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("'");
        if (colored) builder.append(StyleConstant.STRING);
        builder.append(nbt.getString());
        if (colored) builder.append(StyleConstant.BASE);
        builder.append("'");
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagList nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append('[');
        if (!nbt.isEmpty()) {
            int internalIndent = indent + 1;
            for (NBTBase nbtBase : nbt) {
                newLine(builder, internalIndent, pretty);
                builder.append(toGroovyCode(nbtBase, internalIndent, pretty, colored));
                if (colored) builder.append(StyleConstant.BASE);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            newLine(builder, indent, pretty);
            if (colored) builder.append(StyleConstant.BASE);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagCompound nbt, boolean pretty, boolean colored) {
        return toGroovyCode(nbt, 0, pretty, colored);
    }

    public static String toGroovyCode(NBTTagCompound nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append('[');
        if (!nbt.isEmpty()) {
            int internalIndent = indent + 1;
            for (String key : nbt.getKeySet()) {
                newLine(builder, internalIndent, pretty);
                if (colored) builder.append(StyleConstant.BASE);
                builder.append("'");
                if (colored) builder.append(StyleConstant.STRING);
                builder.append(key);
                if (colored) builder.append(StyleConstant.BASE);
                builder.append("'");
                builder.append(": ");
                builder.append(toGroovyCode(nbt.getTag(key), internalIndent, pretty, colored));
                if (colored) builder.append(StyleConstant.BASE);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            newLine(builder, indent, pretty);
            if (colored) builder.append(StyleConstant.BASE);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTTagIntArray nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        if (colored) builder.append(StyleConstant.BASE);
        builder.append('[');
        if (!nbt.isEmpty()) {
            newLine(builder, indent, pretty);
            for (int value : nbt.getIntArray()) {
                if (colored) builder.append(StyleConstant.NUMBER);
                builder.append(value);
                if (colored) builder.append(StyleConstant.BASE);
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            newLine(builder, indent, pretty);
            if (colored) builder.append(StyleConstant.BASE);
        }
        builder.append(']');
        return builder.toString();
    }

    public static String toGroovyCode(NBTBase nbt, int indent, boolean pretty, boolean colored) {
        StringBuilder builder = new StringBuilder();
        switch (nbt.getId()) {
            case Constants.NBT.TAG_BYTE -> builder.append(toGroovyCode((NBTTagByte) nbt, colored));
            case Constants.NBT.TAG_SHORT -> builder.append(toGroovyCode((NBTTagShort) nbt, colored));
            case Constants.NBT.TAG_INT -> builder.append(toGroovyCode((NBTTagInt) nbt, colored));
            case Constants.NBT.TAG_LONG -> builder.append(toGroovyCode((NBTTagLong) nbt, colored));
            case Constants.NBT.TAG_FLOAT -> builder.append(toGroovyCode((NBTTagFloat) nbt, colored));
            case Constants.NBT.TAG_DOUBLE -> builder.append(toGroovyCode((NBTTagDouble) nbt, colored));
            case Constants.NBT.TAG_BYTE_ARRAY -> builder.append(toGroovyCode((NBTTagByteArray) nbt, indent, pretty, colored));
            case Constants.NBT.TAG_STRING -> builder.append(toGroovyCode((NBTTagString) nbt, colored));
            case Constants.NBT.TAG_LIST -> builder.append(toGroovyCode((NBTTagList) nbt, indent, pretty, colored));
            case Constants.NBT.TAG_COMPOUND -> builder.append(toGroovyCode((NBTTagCompound) nbt, indent, pretty, colored));
            case Constants.NBT.TAG_INT_ARRAY -> builder.append(toGroovyCode((NBTTagIntArray) nbt, indent, pretty, colored));
            //TAG_LONG_ARRAY
            default -> throw new IllegalArgumentException(nbt.toString());
        }
        return builder.toString();
    }

    private static void newLine(StringBuilder builder, int indents, boolean pretty) {
        if (!pretty) return;
        builder.append('\n');
        for (int i = 0; i < indents; i++) builder.append("    ");
    }
}
