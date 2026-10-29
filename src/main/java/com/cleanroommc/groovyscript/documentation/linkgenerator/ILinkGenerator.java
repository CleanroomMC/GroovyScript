/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.documentation.linkgenerator;

/**
 * Parses a class and converts it into a relative link to a website.
 * Has a default implementation in {@link BasicLinkGenerator}
 */
public interface ILinkGenerator {

    String id();

    default String extension() {
        return ".java";
    }

    String convert(String location);
}
