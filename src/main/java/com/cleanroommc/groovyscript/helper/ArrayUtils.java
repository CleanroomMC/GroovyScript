package com.cleanroommc.groovyscript.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ArrayUtils {

    public static <T, V> T[] map(V[] array, Function<V, T> function, T[] newArray) {
        if (newArray.length < array.length) {
            newArray = Arrays.copyOf(newArray, array.length);
        }
        for (int i = 0; i < array.length; i++) {
            newArray[i] = function.apply(array[i]);
        }
        return newArray;
    }

    public static <T, V> T[] map(List<V> list, Function<V, T> function, T[] newArray) {
        if (newArray.length < list.size()) {
            newArray = Arrays.copyOf(newArray, list.size());
        }
        for (int i = 0; i < list.size(); i++) {
            newArray[i] = function.apply(list.get(i));
        }
        return newArray;
    }

    public static <T, V> List<T> mapToList(V[] array, Function<V, T> function, List<T> newList) {
        newList.clear();
        for (V v : array) {
            newList.add(function.apply(v));
        }
        return newList;
    }

    public static <T, V> List<T> mapToList(V[] array, Function<V, T> function) {
        return mapToList(array, function, new ArrayList<>());
    }

    public static <T, V, L extends List<T>> L mapToList(List<V> list, Function<V, T> function, L newList) {
        newList.clear();
        for (V v : list) {
            newList.add(function.apply(v));
        }
        return newList;
    }

    public static <T, V> List<T> mapToList(List<V> list, Function<V, T> function) {
        return mapToList(list, function, new ArrayList<>());
    }

    public static <T, V> T[] mapToArray(List<V> list, Function<V, T> function) {
        T[] newArray = (T[]) new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArray[i] = function.apply(list.get(i));
        }
        return newArray;
    }
}
