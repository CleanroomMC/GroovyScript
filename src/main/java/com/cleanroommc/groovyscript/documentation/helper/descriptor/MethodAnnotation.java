/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.documentation.helper.descriptor;

import com.github.bsideup.jabel.Desugar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Desugar
public record MethodAnnotation<A extends Annotation> (Method method, A annotation) {

}
