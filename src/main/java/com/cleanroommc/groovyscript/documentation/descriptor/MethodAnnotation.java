package com.cleanroommc.groovyscript.documentation.descriptor;

import com.github.bsideup.jabel.Desugar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Desugar
public record MethodAnnotation<A extends Annotation> (Method method, A annotation) {

    public String getName() {
        return method.getName();
    }
}
