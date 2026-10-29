/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;

@Mixin(value = LightOreTransmutations.class, remap = false)
public interface LightOreTransmutationsAccessor {

    @Accessor
    static Collection<LightOreTransmutations.Transmutation> getRegisteredTransmutations() {
        return null;
    }
}
