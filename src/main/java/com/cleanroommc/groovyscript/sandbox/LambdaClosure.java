package com.cleanroommc.groovyscript.sandbox;

import groovy.lang.Closure;

public class LambdaClosure extends Closure<Object> {

    private final AnyFunction function;

    public LambdaClosure(Object owner, AnyFunction function) {
        super(owner);
        this.function = function;
    }

    public Object doCall(Object[] args) {
        return function.run(args);
    }

    public interface AnyFunction {
        Object run(Object[] args);
    }
}
