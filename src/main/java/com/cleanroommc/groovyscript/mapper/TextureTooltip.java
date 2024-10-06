package com.cleanroommc.groovyscript.mapper;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextureTooltip {

    private static final Pattern embeddingPattern = Pattern.compile("(?>\\$\\{(?<mapper>\\w+)\\(['\\\"](?<key>[\\w\\s:-]+)['\\\"]\\)})");

    private final String content;
    private final List<Embedding> embeddings;

    public TextureTooltip(String content) {
        this.content = content;
        this.embeddings = new ArrayList<>();

        var matcher = embeddingPattern.matcher(content);

        while (matcher.find()) {
            var mapper = ObjectMapperManager.getObjectMapper(matcher.group("mapper"));
            if (mapper == null) {
                continue;
            }

            var binder = mapper.getTextureBinder();

            if (binder == null) {
                continue;
            }

            var key = matcher.group("key");
            var result = mapper.invoke(true, key);

            if (result == null) {
                continue;
            }

            embeddings.add(new Embedding(matcher.start(), matcher.end(), result, binder, computeTextureName(mapper.getName(), key)));
        }
    }

    private static String computeTextureName(String name, String arg) {
        return DigestUtils.sha1Hex(name + arg);
    }

    public String getContent() {
        return content;
    }

    public List<Embedding> getEmbeddings() {
        return embeddings;
    }

    public class Embedding<T> {

        private final int start;
        private final int end;
        private final T context;
        private final TextureBinder<T> textureBinder;
        private final String textureName;

        private Embedding(int start, int end, T context, TextureBinder<T> textureBinder, String textureName) {
            this.start = start;
            this.end = end;
            this.context = context;
            this.textureBinder = textureBinder;
            this.textureName = textureName;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public T getContext() {
            return context;
        }

        public TextureBinder<T> getTextureBinder() {
            return textureBinder;
        }

        public String getTextureName() {
            return textureName;
        }

    }
}
