/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.sandbox.security;

public class SandboxSecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SandboxSecurityException(String msg) {
        super(msg);
    }

    public static SandboxSecurityException format(String msg) {
        return new SandboxSecurityException(msg);
    }
}
