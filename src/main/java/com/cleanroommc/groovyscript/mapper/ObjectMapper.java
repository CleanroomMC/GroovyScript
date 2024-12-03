package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.*;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.sandbox.expand.IDocumented;
import groovy.lang.Closure;
import groovy.lang.groovydoc.Groovydoc;
import groovy.lang.groovydoc.GroovydocHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class handles global getter functions like `item(...)` and `fluid(...)`
 *
 * @param <T> return type of the function
 */
public class ObjectMapper<T> extends Closure<T> implements INamed, IDocumented {

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

    private final String name;
    private final GroovyContainer<?> mod;
    private final IObjectParser<T> handler;
    private final Supplier<Result<T>> defaultValue;
    private final Class<T> returnType;
    private final List<Class<?>[]> paramTypes;
    private final Completer completer;
    private final String documentation;
    private final TextureBinder<T> textureBinder;
    private List<MethodNode> methodNodes;

    private ObjectMapper(String name, GroovyContainer<?> mod, IObjectParser<T> handler, Supplier<Result<T>> defaultValue, Class<T> returnType, List<Class<?>[]> paramTypes, Completer completer, String documentation, TextureBinder<T> textureBinder) {
        super(null);
        this.name = name;
        this.mod = mod;
        this.handler = handler;
        this.defaultValue = defaultValue;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.completer = completer;
        this.documentation = documentation;
        this.textureBinder = textureBinder;
    }

    public @Nullable T invoke(boolean silent, String s, Object... args) {
        Result<T> t = Objects.requireNonNull(handler.parse(s, args), "Object mapper must return a non null result!");
        if (t.hasError()) {
            if (!silent) {
                if (this.mod == null) {
                    GroovyLog.get().error("Can't find {} for name {}!", name, s);
                } else {
                    GroovyLog.get().error("Can't find {} {} for name {}!", mod, name, s);
                }
                if (t.getError() != null && !t.getError().isEmpty()) {
                    GroovyLog.get().error(" - reason: {}", t.getError());
                }
            }
            return null;
        }
        return Objects.requireNonNull(t.getValue(), "Object mapper result must contain a non-null value!");
    }

    public T invokeWithDefault(boolean silent, String s, Object... args) {
        T t = invoke(silent, s, args);
        return t != null ? t : invokeDefault();
    }

    public T invokeDefault() {
        Result<T> t = this.defaultValue.get();
        return t == null || t.hasError() ? null : t.getValue();
    }

    public GroovyContainer<?> getMod() {
        return mod;
    }

    @Override
    public Collection<String> getAliases() {
        return Collections.singleton(this.name);
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Class<?>[]> getParamTypes() {
        return this.paramTypes;
    }

    public Class<T> getReturnType() {
        return returnType;
    }

    @GroovyBlacklist
    public Completer getCompleter() {
        return completer;
    }

    public T doCall(String s, Object... args) {
        return invokeWithDefault(false, s, args);
    }

    public T doCall() {
        return invokeDefault();
    }

    @Override
    public String getDocumentation() {
        return documentation;
    }

    public List<MethodNode> getMethodNodes() {
        if (methodNodes == null) {
            this.methodNodes = new ArrayList<>();
            for (Class<?>[] paramType : this.paramTypes) {
                Parameter[] params = ArrayUtils.map(
                        paramType,
                        c -> new Parameter(ClassHelper.makeCached(c), ""),
                        new Parameter[paramType.length]);
                MethodNode node = new MethodNode(
                        this.name,
                        Modifier.PUBLIC | Modifier.FINAL,
                        ClassHelper.makeCached(this.returnType),
                        params,
                        null,
                        null);
                node.setDeclaringClass(this.mod != null ? ClassHelper.makeCached(this.mod.get().getClass()) : ClassHelper.makeCached(ObjectMapperManager.class));
                node.setNodeMetaData(GroovydocHolder.DOC_COMMENT, new Groovydoc(this.documentation, node));
                this.methodNodes.add(node);
            }
        }
        return methodNodes;
    }

    @ApiStatus.Experimental
    public TextureBinder<T> getTextureBinder() {
        return textureBinder;
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
            ObjectMapper<T> goh = new ObjectMapper<>(
                    this.name,
                    this.mod,
                    this.handler,
                    this.defaultValue,
                    this.returnType,
                    this.paramTypes,
                    this.completer,
                    this.documentation,
                    this.textureBinder);
            ObjectMapperManager.registerObjectMapper(this.mod, goh);
        }
    }
}
