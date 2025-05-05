package com.cleanroommc.groovyscript.documentation.helper.descriptor;

import com.github.bsideup.jabel.Desugar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Desugar
public record MethodAnnotation<A extends Annotation> (Method method, A annotation) {

}
