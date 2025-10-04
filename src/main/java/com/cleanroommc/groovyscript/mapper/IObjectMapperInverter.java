package com.cleanroommc.groovyscript.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A function to create a string from an object, with the string reprenting a full Object Mapper.
 * This is used for {@link com.cleanroommc.groovyscript.mapper.ObjectMapper object mappers}.
 * <p>
 * A few examples of what this outputs would be
 * {@code item('minecraft:clay')}, {@code biome('minecraft:desert')}, or {@code blockstate('minecraft:log:axis=z:variant=oak')}.
 * <p>
 * The return value should exactly correspond to the Object Mapper that creates it,
 * thus, {@code item % item('minecraft:clay') === "item('minecraft:clay')"}
 *
 * @param <T> the type of the objects being inverted
 */
@FunctionalInterface
public interface IObjectMapperInverter<T> {

    /**
     * @param value the object being converted
     * @return a String directly corresponding to the code that would create {@param value}
     */
    String getGroovyCode(T value);

    /**
     * Using Groovy's Operator Overloading, this is %.
     */
    default String mod(T value) {
        return getGroovyCode(value);
    }

    /**
     * Converts any number of objects into a collection of their strings
     */
    @SuppressWarnings("unchecked")
    default Collection<String> mod(T... values) {
        List<String> list = new ArrayList<>();
        for (T value : values) {
            list.add(getGroovyCode(value));
        }
        return list;
    }

    /**
     * Converts any number of objects into a collection of their strings
     */
    default Collection<String> mod(Collection<T> values) {
        List<String> list = new ArrayList<>();
        for (T value : values) {
            list.add(getGroovyCode(value));
        }
        return list;
    }
}
