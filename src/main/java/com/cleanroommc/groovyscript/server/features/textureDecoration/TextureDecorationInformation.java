package com.cleanroommc.groovyscript.server.features.textureDecoration;

import org.eclipse.lsp4j.Range;

public class TextureDecorationInformation {

    private Range range;
    private String textureUri;

    public TextureDecorationInformation(Range range, String textureUri) {
        this.range = range;
        this.textureUri = textureUri;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public String getTextureUri() {
        return textureUri;
    }

    public void setTextureUri(String textureUri) {
        this.textureUri = textureUri;
    }
}
