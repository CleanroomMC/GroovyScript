package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.mapper.Completer;
import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
public class GameObjectHandler<T> {

    @ApiStatus.Internal
    public static <T> Builder<T> builder(String name, Class<T> returnTpe) {
        return new Builder<>(name, returnTpe);
    }

    public static class Builder<T> extends ObjectMapper.Builder<T> {

        public Builder(String name, Class<T> returnType) {
            super(name, returnType);
        }

        @Override
        public Builder<T> mod(String mod) {
            return (Builder<T>) super.mod(mod);
        }

        @Override
        public Builder<T> mod(GroovyContainer<?> mod) {
            return (Builder<T>) super.mod(mod);
        }

        @Override
        public Builder<T> parser(IObjectParser<T> handler) {
            return (Builder<T>) super.parser(handler);
        }

        public Builder<T> parser(IGameObjectParser<T> handler) {
            return (Builder<T>) super.parser(handler);
        }

        @Override
        public Builder<T> completer(Completer completer) {
            return (Builder<T>) super.completer(completer);
        }

        public Builder<T> completer(com.cleanroommc.groovyscript.gameobjects.Completer completer) {
            return (Builder<T>) super.completer(completer);
        }

        @Override
        public Builder<T> completerOfNames(Supplier<Iterable<String>> values) {
            return (Builder<T>) super.completerOfNames(values);
        }

        @Override
        public <V> Builder<T> completerOfNamed(Supplier<Iterable<V>> values, Function<V, String> toString) {
            return (Builder<T>) super.completerOfNamed(values, toString);
        }

        @Override
        public <V extends Enum<V>> Builder<T> completerOfEnum(Class<V> values, boolean caseSensitive) {
            return (Builder<T>) super.completerOfEnum(values, caseSensitive);
        }

        @Override
        public Builder<T> completer(Supplier<Iterable<ResourceLocation>> values) {
            return (Builder<T>) super.completer(values);
        }

        @Override
        public <V extends IForgeRegistryEntry<V>> Builder<T> completer(IForgeRegistry<V> values) {
            return (Builder<T>) super.completer(values);
        }

        @Override
        public Builder<T> defaultValue(Supplier<T> defaultValue) {
            return (Builder<T>) super.defaultValue(defaultValue);
        }

        @Override
        public Builder<T> defaultValueSup(Supplier<Result<T>> defaultValue) {
            return (Builder<T>) super.defaultValueSup(defaultValue);
        }

        @Override
        public Builder<T> addSignature(Class<?>... paramTypes) {
            return (Builder<T>) super.addSignature(paramTypes);
        }

        @Override
        public Builder<T> documentation(String doc) {
            return (Builder<T>) super.documentation(doc);
        }

        @Override
        public Builder<T> docOfType(String type) {
            return (Builder<T>) super.docOfType(type);
        }
    }
}
