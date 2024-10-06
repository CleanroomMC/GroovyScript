package com.cleanroommc.groovyscript.helper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Objects;

public class BetterList<K> extends ObjectArrayList<K> {

    public void addFirst(K k) {
        add(0, k);
    }

    public void addLast(K k) {
        add(k);
    }

    public K removeFirst() {
        return remove(0);
    }

    public K removeLast() {
        return remove(size() - 1);
    }

    public K pollFirst() {
        return isEmpty() ? null : removeFirst();
    }

    public K pollLast() {
        return isEmpty() ? null : removeLast();
    }

    public K getFirst() {
        return get(0);
    }

    public K getLast() {
        return get(size() - 1);
    }

    public K peekFirst() {
        return isEmpty() ? null : get(0);
    }

    public K peekLast() {
        return isEmpty() ? null : get(size() - 1);
    }

    public boolean removeFirstOccurrence(Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i), o)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeLastOccurrence(Object o) {
        for (int i = size() - 1; i >= 0; i--) {
            if (Objects.equals(get(i), o)) {
                remove(i);
                return true;
            }
        }
        return false;
    }
}
