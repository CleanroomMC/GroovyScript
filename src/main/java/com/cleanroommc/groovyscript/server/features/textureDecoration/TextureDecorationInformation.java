package com.cleanroommc.groovyscript.server.features.textureDecoration;

import org.eclipse.lsp4j.Range;

import java.util.List;

public class TextureDecorationInformation {

    private Range range;
    private String textureUri;
    private List<String> tooltips;

    public TextureDecorationInformation(Range range, String textureUri, List<String> tooltips) {
        this.range = range;
        this.textureUri = textureUri;
        this.tooltips = tooltips;
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

    public List<String> getTooltips() {
        return tooltips;
    }

    public void setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
    }
}
