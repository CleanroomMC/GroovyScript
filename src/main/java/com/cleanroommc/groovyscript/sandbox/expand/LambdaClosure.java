package com.cleanroommc.groovyscript.sandbox.expand;

import groovy.lang.Closure;

import java.util.function.Function;

public class LambdaClosure<T> extends Closure<T> {

    private final Function<Object[], T> function;

    public LambdaClosure(Object owner, Object thisObject, Function<Object[], T> function) {
        super(owner, thisObject);
        this.function = function;
    }

    public LambdaClosure(Object owner, Function<Object[], T> function) {
        super(owner);
        this.function = function;
    }

    public LambdaClosure(Function<Object[], T> function) {
        this(function.getClass(), function);
    }

    public T doCall(Object[] args) {
        return function.apply(args);
    }

    @Override
    public T call(Object... arguments) {
        return function.apply(arguments);
    }
}
