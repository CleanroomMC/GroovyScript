/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationInformation;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@JsonSegment("groovyScript")
public interface GroovyScriptFeaturesService {

    @JsonRequest
    default CompletableFuture<List<TextureDecorationInformation>> textureDecoration(TextureDecorationParams params) {
        throw new UnsupportedOperationException();
    }
}
