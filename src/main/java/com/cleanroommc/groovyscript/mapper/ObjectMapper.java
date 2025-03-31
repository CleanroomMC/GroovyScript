package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.sandbox.expand.IDocumented;
import com.cleanroommc.groovyscript.server.CompletionParams;
import com.cleanroommc.groovyscript.server.Completions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class handles global getter functions like `item(...)` and `fluid(...)`
 *
 * @param <T> return type of the function
 */
public class ObjectMapper<T> extends AbstractObjectMapper<T> {

    /**
     * Creates an object mapper builder.
     * Use {@link GroovyContainer#objectMapperBuilder(String, Class)} instead!
     *
     * @param name       the function name
     * @param returnType the return type
     * @param <T>        the return type
     * @return a new object mapper builder
     */
    @ApiStatus.Internal
    public static <T> Builder<T> builder(String name, Class<T> returnType) {
        return new Builder<>(name, returnType);
    }

    private final IObjectParser<T> handler;
    private final Supplier<Result<T>> defaultValue;
    private final Completer completer;
    private final TextureBinder<T> textureBinder;
    private final Function<T, List<String>> tooltip;

    private ObjectMapper(String name, GroovyContainer<?> mod, IObjectParser<T> handler, Supplier<Result<T>> defaultValue, Class<T> returnType, List<Class<?>[]> paramTypes, Completer completer, String documentation, TextureBinder<T> textureBinder, Function<T, List<String>> tooltip) {
        super(name, mod, returnType);
        this.handler = handler;
        this.defaultValue = defaultValue;
        this.tooltip = tooltip;
        this.completer = completer;
        this.documentation = documentation;
        this.textureBinder = textureBinder;
        clearSignatures();
        for (Class<?>[] signature : paramTypes) {
            addSignature(signature);
        }
    }

    @Override
    public @NotNull Result<T> parse(String mainArg, Object[] args) {
        return this.handler.parse(mainArg, args);
    }

    @Override
    public Result<T> getDefaultValue() {
        return defaultValue.get();
    }

    @Override
    public void provideCompletion(int index, CompletionParams params, Completions items) {
        if (this.completer != null) {
            this.completer.complete(index, items);
        }
    }

    @Override
    public void bindTexture(T t) {
        if (this.textureBinder != null) {
            this.textureBinder.bindTexture(t);
        }
    }

    @Override
    public @NotNull List<String> getTooltip(T t) {
        if (this.tooltip != null) {
            return this.tooltip.apply(t);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasTextureBinder() {
        return this.textureBinder != null;
    }

    /**
     * A helper class to create {@link ObjectMapper}s.
     *
     * @param <T> the return type of the mapper
     */
    public static class Builder<T> {

        private final String name;
        private GroovyContainer<?> mod;
        private IObjectParser<T> handler;
        private Supplier<Result<T>> defaultValue;
        private final Class<T> returnType;
        private final List<Class<?>[]> paramTypes = new ArrayList<>();
        private Completer completer;
        private String documentation;
        private TextureBinder<T> textureBinder;
        private Function<T, List<String>> tooltip;

        @ApiStatus.Internal
        public Builder(String name, Class<T> returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        /**
         * Sets the mod who creates this mapper. This is already done when {@link GroovyContainer#objectMapperBuilder(String, Class)} is used.
         *
         * @param mod the creator mod
         * @return this builder
         * @throws IllegalStateException if GroovyScript doesn't have compat with that mod
         */
        public Builder<T> mod(String mod) {
            return mod(ModSupport.INSTANCE.getContainer(mod));
        }

        /**
         * Sets the mod who creates this mapper. This is already done when {@link GroovyContainer#objectMapperBuilder(String, Class)} is used.
         *
         * @param mod the creator mod
         * @return this builder
         */
        public Builder<T> mod(GroovyContainer<?> mod) {
            if (this.mod == null) {
                this.mod = mod;
            }
            return this;
        }

        /**
         * Sets the parser function. It receives a String and any amount of other arguments and returns an object of the specified type or an error.
         *
         * @param handler parser function
         * @return this builder
         */
        public Builder<T> parser(IObjectParser<T> handler) {
            this.handler = handler;
            return this;
        }

        /**
         * Sets a value completer. This is used by the LSP to provide completion for mappers.
         * Take a look at {@link Completer}'s static methods for helpers.
         *
         * @param completer completer
         * @return this builder
         */
        public Builder<T> completer(Completer completer) {
            if (this.completer == null) {
                this.completer = completer;
            } else {
                this.completer = this.completer.and(completer);
            }
            return this;
        }

        /**
         * Sets a value completer. This is used by the LSP to provide completion for mappers.
         *
         * @param values a supplier if a list of names
         * @return this builder
         */
        public Builder<T> completerOfNames(Supplier<Iterable<String>> values) {
            return completer(Completer.ofNamed(values, Function.identity(), 0));
        }

        /**
         * Sets a value completer. This is used by the LSP to provide completion for mappers.
         *
         * @param values   a supplier if a list of objects which have names
         * @param toString a function to turn said objects into names
         * @return this builder
         */
        public <V> Builder<T> completerOfNamed(Supplier<Iterable<V>> values, Function<V, String> toString) {
            return completer(Completer.ofNamed(values, toString, 0));
        }

        /**
         * Sets a value completer. This is used by the LSP to provide completion for mappers.
         *
         * @param values        an enum class
         * @param caseSensitive lower case names are used if this is false
         * @return this builder
         */
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

        /**
         * Sets a value completer. This is used by the LSP to provide completion for mappers.
         *
         * @param values a forge registry
         * @return this builder
         */
        public <V extends IForgeRegistryEntry<V>> Builder<T> completer(IForgeRegistry<V> values) {
            return completer(values::getKeys);
        }

        /**
         * Sets a default value. This is what the mapper returns when no value could be found or an error occurred. Default is null.
         *
         * @param defaultValue default value
         * @return this builder
         */
        public Builder<T> defaultValue(Supplier<T> defaultValue) {
            return defaultValueSup(() -> Result.some(defaultValue.get()));
        }

        /**
         * Sets a default value. This is what the mapper returns when no value could be found or an error occurred. Default is null.
         *
         * @param defaultValue default value
         * @return this builder
         */
        public Builder<T> defaultValueSup(Supplier<Result<T>> defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Adds a method signature. This is only used by LSP to provide helpful tooltips.
         *
         * @param paramTypes parameter types
         * @return this builder
         */
        public Builder<T> addSignature(Class<?>... paramTypes) {
            this.paramTypes.add(paramTypes);
            return this;
        }

        /**
         * Adds a documentation string. This is only used by LSP to provide helpful tooltips.
         *
         * @param doc documentation
         * @return this builder
         */
        public Builder<T> documentation(String doc) {
            this.documentation = doc;
            return this;
        }

        /**
         * Adds a documentation string. This is only used by LSP to provide helpful tooltips.
         *
         * @param type type of the returned objects
         * @return this builder
         */
        public Builder<T> docOfType(String type) {
            String mod = this.mod == null ? StringUtils.EMPTY : this.mod.getContainerName() + ' ';
            return documentation("returns a " + mod + type);
        }

        @ApiStatus.Experimental
        public Builder<T> textureBinder(TextureBinder<T> textureBinder) {
            this.textureBinder = textureBinder;
            return this;
        }

        @ApiStatus.Experimental
        public <V> Builder<T> textureBinder(Function<T, V> mapper, TextureBinder<V> binder) {
            return textureBinder(TextureBinder.of(mapper, binder));
        }

        @ApiStatus.Experimental
        public <V> Builder<T> textureBinderOfList(Function<T, List<V>> mapper, TextureBinder<V> binder) {
            return textureBinder(TextureBinder.ofList(mapper, binder));
        }

        @ApiStatus.Experimental
        public <V> Builder<T> textureBinderOfArray(Function<T, V[]> mapper, TextureBinder<V> binder) {
            return textureBinder(TextureBinder.ofArray(mapper, binder));
        }

        public Builder<T> tooltip(Function<T, List<String>> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public <V> Builder<T> tooltipOfValues(Function<T, Iterable<V>> values, Function<V, String> toString) {
            return tooltip(t -> {
                List<String> list = new ArrayList<>();
                for (V v : values.apply(t)) {
                    list.add(toString.apply(v));
                }
                return list;
            });
        }

        public <V> Builder<T> tooltipOfArray(Function<T, V[]> values, Function<V, String> toString) {
            return tooltip(t -> {
                List<String> list = new ArrayList<>();
                for (V v : values.apply(t)) {
                    list.add(toString.apply(v));
                }
                return list;
            });
        }

        /**
         * Registers the mapper.
         *
         * @throws IllegalArgumentException if the mapper was misconfigured.
         */
        public void register() {
            if (this.name == null || this.name.isEmpty()) throw new IllegalArgumentException("Name must not be empty");
            if (this.mod != null && !this.mod.isLoaded())
                throw new IllegalArgumentException("Tried to register ObjectMapper for mod " + this.mod + ", but it's not loaded");
            Objects.requireNonNull(this.handler, () -> "The ObjectMapper function must no be null");
            Objects.requireNonNull(this.returnType, () -> "The ObjectMapper return type must not be null");
            if (this.paramTypes.isEmpty()) this.paramTypes.add(new Class[]{
                    String.class
            });
            if (this.defaultValue == null) this.defaultValue = () -> null;
            this.documentation = IDocumented.toJavaDoc(this.documentation);
            ObjectMapper<T> mapper = new ObjectMapper<>(
                    this.name,
                    this.mod,
                    this.handler,
                    this.defaultValue,
                    this.returnType,
                    this.paramTypes,
                    this.completer,
                    this.documentation,
                    this.textureBinder,
                    this.tooltip);
            ObjectMapperManager.registerObjectMapper(mapper);
        }
    }
}
