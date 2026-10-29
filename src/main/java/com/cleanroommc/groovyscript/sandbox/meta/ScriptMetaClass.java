/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.sandbox.meta;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;

public class ScriptMetaClass extends MetaClassImpl {

    public ScriptMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }
}
