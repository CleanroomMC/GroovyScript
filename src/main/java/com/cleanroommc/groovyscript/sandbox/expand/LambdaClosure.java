package com.cleanroommc.groovyscript.sandbox.expand;

import groovy.lang.Closure;

public class LambdaClosure<T> extends Closure<T> {

    private final AnyFunction<T> function;

    public LambdaClosure(Object owner, AnyFunction<T> function) {
        super(owner);
        this.function = function;
    }

    public LambdaClosure(AnyFunction<T> function) {
        this(function.getClass(), function);
    }

    public T doCall(Object[] args) {
        return function.run(args);
    }

    public interface AnyFunction<T> {

        T run(Object[] args);
    }
}
