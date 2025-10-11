package com.cleanroommc.groovyscript.helper;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PairList<T1, T2> implements Iterable<Pair<T1, T2>> {

    private final List<T1> l1 = new ArrayList<>();
    private final List<T2> l2 = new ArrayList<>();

    public void add(T1 t1, T2 t2) {
        this.l1.add(t1);
        this.l2.add(t2);
    }

    public int size() {
        return this.l1.size();
    }

    public boolean isEmpty() {
        return this.l1.isEmpty();
    }

    public T1 getLeft(int index) {
        return this.l1.get(index);
    }

    public T2 getRight(int index) {
        return this.l2.get(index);
    }

    public Pair<T1, T2> get(int index) {
        return Pair.of(getLeft(index), getRight(index));
    }

    public Iterable<T1> getLeftIterable() {
        return () -> new AbstractIterator<>() {

            private final Iterator<T1> it = PairList.this.l1.iterator();

            @Override
            protected T1 computeNext() {
                return it.hasNext() ? it.next() : endOfData();
            }
        };
    }

    public Iterable<T2> getRightIterable() {
        return () -> new AbstractIterator<>() {

            private final Iterator<T2> it = PairList.this.l2.iterator();

            @Override
            protected T2 computeNext() {
                return it.hasNext() ? it.next() : endOfData();
            }
        };
    }

    @NotNull
    @Override
    public Iterator<Pair<T1, T2>> iterator() {
        return new AbstractIterator<>() {

            private final MutablePair<T1, T2> pair = MutablePair.of(null, null);
            private int index = -1;

            @Override
            protected Pair<T1, T2> computeNext() {
                if (++this.index == size()) return endOfData();
                this.pair.setLeft(getLeft(this.index));
                this.pair.setRight(getRight(this.index));
                return this.pair;
            }
        };
    }
}
