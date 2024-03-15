package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameObjectHandler<T> {

    public static <T> Builder<T> builder(String name, Class<T> returnTpe) {
        return new Builder<>(name, returnTpe);
    }

    private final String name;
    private final String mod;
    private final IGameObjectParser<T> handler;
    private final Supplier<Result<T>> defaultValue;
    private final Class<T> returnType;
    private final List<Class<?>[]> paramTypes;
    private final Completer completer;

    private GameObjectHandler(String name, String mod, IGameObjectParser<T> handler, Supplier<Result<T>> defaultValue, Class<T> returnType, List<Class<?>[]> paramTypes, Completer completer) {
        this.name = name;
        this.mod = mod;
        this.handler = handler;
        this.defaultValue = defaultValue;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.completer = completer;
    }

    T invoke(String s, Object... args) {
        Result<T> t = Objects.requireNonNull(handler.parse(s, args), "Bracket handlers must return a non null result!");
        if (t.hasError()) {
            if (this.mod == null) {
                GroovyLog.get().error("Can't find {} for name {}!", name, s);
            } else {
                GroovyLog.get().error("Can't find {} {} for name {}!", mod, name, s);
            }
            if (t.getError() != null && !t.getError().isEmpty()) {
                GroovyLog.get().error(" - reason: {}", t.getError());
            }
            t = this.defaultValue.get();
            return t.hasError() ? null : t.getValue();
        }
        return Objects.requireNonNull(t.getValue(), "Bracket handler result must contain a non-null value!");
    }

    public String getMod() {
        return mod;
    }

    public String getName() {
        return name;
    }

    public List<Class<?>[]> getParamTypes() {
        return this.paramTypes;
    }

    public Class<T> getReturnType() {
        return returnType;
    }

    public Completer getCompleter() {
        return completer;
    }

    public static class Builder<T> {

        private final String name;
        private String mod;
        private IGameObjectParser<T> handler;
        private Supplier<Result<T>> defaultValue;
        private final Class<T> returnType;
        private final List<Class<?>[]> paramTypes = new ArrayList<>();
        private Completer completer;

        public Builder(String name, Class<T> returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        public Builder<T> mod(String mod) {
            this.mod = mod;
            return this;
        }

        public Builder<T> parser(IGameObjectParser<T> handler) {
            this.handler = handler;
            return this;
        }

        public Builder<T> completer(Completer completer) {
            if (this.completer == null) {
                this.completer = completer;
            } else {
                this.completer = this.completer.and(completer);
            }
            return this;
        }

        public Builder<T> completerOfNames(Supplier<Iterable<String>> values) {
            return completer(Completer.ofNamed(values, Function.identity(), 0));
        }

        public <V> Builder<T> completerOfNamed(Supplier<Iterable<V>> values, Function<V, String> toString) {
            return completer(Completer.ofNamed(values, toString, 0));
        }

        public <V extends Enum<V>> Builder<T> completerOfEnum(Class<V> values, boolean caseSensitive) {
            return completerOfNamed(() -> Arrays.asList(values.getEnumConstants()), s -> caseSensitive ? s.name() : s.name().toLowerCase(Locale.ROOT));
        }

        public Builder<T> completer(Supplier<Iterable<ResourceLocation>> values) {
            return completer(Completer.ofValues(values, v -> {
                CompletionItem item = new CompletionItem(v.toString());
                item.setKind(CompletionItemKind.Constant);
                return item;
            }, 0));
        }

        public <V extends IForgeRegistryEntry<V>> Builder<T> completer(IForgeRegistry<V> values) {
            return completer(values::getKeys);
        }

        public Builder<T> defaultValue(Supplier<T> defaultValue) {
            return defaultValueSup(() -> Result.some(defaultValue.get()));
        }

        public Builder<T> defaultValueSup(Supplier<Result<T>> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<T> addSignature(Class<?>... paramTypes) {
            this.paramTypes.add(paramTypes);
            return this;
        }

        public void register() {
            if (this.name == null || this.name.isEmpty()) throw new IllegalArgumentException("Name must not be empty");
            if (GameObjectHandlerManager.hasGameObjectHandler(this.name))
                throw new IllegalArgumentException("GameObjectHandler with name " + this.name + " already exists");
            if (this.mod != null && !Loader.isModLoaded(this.mod))
                throw new IllegalArgumentException("Tried to register GameObjectHandler for mod " + this.mod + ", but it's not loaded");
            Objects.requireNonNull(this.handler, () -> "The GameObjectHandler function must no be null");
            Objects.requireNonNull(this.returnType, () -> "The GameObjectHandler return type must not be null");
            if (this.paramTypes.isEmpty()) this.paramTypes.add(new Class[]{String.class});
            if (this.defaultValue == null) this.defaultValue = () -> null;
            GameObjectHandlerManager.registerGameObjectHandler(new GameObjectHandler<>(this.name, this.mod, this.handler, this.defaultValue,
                                                                                       this.returnType, this.paramTypes, this.completer));
        }
    }
}
