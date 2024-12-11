package com.cleanroommc.groovyscript.mapper;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TooltipEmbedding<T> {

    private static final Pattern embeddingPattern = Pattern.compile("(?>\\$\\{(?<mapper>\\w+)\\(['\"](?<key>[\\w\\s:-]+)['\"]\\)})");

    private static String computeTextureName(String name, String arg) {
        return DigestUtils.sha1Hex(name + arg);
    }

    public static List<TooltipEmbedding<?>> parseEmbeddings(String content) {
        List<TooltipEmbedding<?>> embeddings = new ArrayList<>();
        var matcher = embeddingPattern.matcher(content);
        while (matcher.find()) {
            AbstractObjectMapper<?> mapper = ObjectMapperManager.getObjectMapper(matcher.group("mapper"));
            if (mapper == null) continue;
            TooltipEmbedding<?> embedding = TooltipEmbedding.of(matcher.start(), matcher.end(), matcher.group("key"), mapper);
            if (embedding == null) continue;
            embeddings.add(embedding);
        }
        return embeddings;
    }

    private final int start;
    private final int end;
    private final T context;
    private final AbstractObjectMapper<T> mapper;
    private final String textureName;

    private static <T> TooltipEmbedding<T> of(int start, int end, String key, AbstractObjectMapper<T> mapper) {
        if (!mapper.hasTextureBinder()) return null;
        T result = mapper.invoke(true, key);
        if (result == null) return null;
        return new TooltipEmbedding<>(start, end, result, mapper, computeTextureName(mapper.getName(), key));
    }

    private TooltipEmbedding(int start, int end, T context, AbstractObjectMapper<T> mapper, String textureName) {
        this.start = start;
        this.end = end;
        this.context = context;
        this.mapper = mapper;
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

    public AbstractObjectMapper<T> getMapper() {
        return mapper;
    }

    public String getTextureName() {
        return textureName;
    }
}
