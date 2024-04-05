package com.cleanroommc.groovyscript.gameobjects;

import com.cleanroommc.groovyscript.api.*;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
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

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameObjectHandler<T> extends Closure<T> implements INamed {

    public static <T> Builder<T> builder(String name, Class<T> returnTpe) {
        return new Builder<>(name, returnTpe);
    }

    private final String name;
    private final GroovyContainer<?> mod;
    private final IGameObjectParser<T> handler;
    private final Supplier<Result<T>> defaultValue;
    private final Class<T> returnType;
    private final List<Class<?>[]> paramTypes;
    private final Completer completer;
    private List<MethodNode> methodNodes;

    private GameObjectHandler(String name, GroovyContainer<?> mod, IGameObjectParser<T> handler, Supplier<Result<T>> defaultValue, Class<T> returnType, List<Class<?>[]> paramTypes, Completer completer) {
        super(null);
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

    public GroovyContainer<?> getMod() {
        return mod;
    }

    @Override
    public Collection<String> getAliases() {
        return Collections.singleton(this.name);
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

    @GroovyBlacklist
    public Completer getCompleter() {
        return completer;
    }

    public T doCall(String s, Object... args) {
        return invoke(s, args);
    }

    public List<MethodNode> getMethodNodes() {
        if (methodNodes == null) {
            this.methodNodes = new ArrayList<>();
            for (Class<?>[] paramType : this.paramTypes) {
                Parameter[] params = new Parameter[paramType.length];
                for (int i = 0; i < params.length; i++) {
                    params[i] = new Parameter(ClassHelper.makeCached(paramType[i]), "arg" + i);
                }
                MethodNode node = new MethodNode(this.name, Modifier.PUBLIC | Modifier.FINAL, ClassHelper.makeCached(this.returnType), params, null, null);
                node.setDeclaringClass(
                        this.mod != null ? ClassHelper.makeCached(this.mod.get().getClass()) : ClassHelper.makeCached(GameObjectHandlerManager.class));
                this.methodNodes.add(node);
            }
        }
        return methodNodes;
    }

    public static class Builder<T> {

        private final String name;
        private GroovyContainer<?> mod;
        private IGameObjectParser<T> handler;
        private Supplier<Result<T>> defaultValue;
        private final Class<T> returnType;
        private final List<Class<?>[]> paramTypes = new ArrayList<>();
        private Completer completer;
        private String documentation;

        @ApiStatus.Internal
        public Builder(String name, Class<T> returnType) {
            this.name = name;
            this.returnType = returnType;
        }

        public Builder<T> mod(String mod) {
            return mod(ModSupport.INSTANCE.getContainer(mod));
        }

        public Builder<T> mod(GroovyContainer<?> mod) {
            if (this.mod == null) {
                this.mod = mod;
            }
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

        public Builder<T> documentation(String doc) {
            this.documentation = doc;
            return this;
        }

        public Builder<T> docOfType(String type) {
            String mod = this.mod == null ? StringUtils.EMPTY : this.mod.getContainerName();
            return documentation("returns a " + mod + " " + type);
        }

        public void register() {
            if (this.name == null || this.name.isEmpty()) throw new IllegalArgumentException("Name must not be empty");
            if (GameObjectHandlerManager.hasGameObjectHandler(this.name))
                throw new IllegalArgumentException("GameObjectHandler with name " + this.name + " already exists");
            if (this.mod != null && !this.mod.isLoaded())
                throw new IllegalArgumentException("Tried to register GameObjectHandler for mod " + this.mod + ", but it's not loaded");
            Objects.requireNonNull(this.handler, () -> "The GameObjectHandler function must no be null");
            Objects.requireNonNull(this.returnType, () -> "The GameObjectHandler return type must not be null");
            if (this.paramTypes.isEmpty()) this.paramTypes.add(new Class[]{String.class});
            if (this.defaultValue == null) this.defaultValue = () -> null;
            GameObjectHandler<T> goh = new GameObjectHandler<>(this.name, this.mod, this.handler, this.defaultValue,
                                                               this.returnType, this.paramTypes, this.completer);
            GameObjectHandlerManager.registerGameObjectHandler(this.mod == null ? null : this.mod.get(), goh);
            if (this.mod != null) {
                Class<?> clazz = this.mod.get().getClass();
                for (Class<?>[] paramTypes : goh.paramTypes) {
                    ExpandoMetaClass emc = ExpansionHelper.getExpandoClass(clazz);
                    emc.registerInstanceMethod(new GohMetaMethod(goh, paramTypes, clazz, this.documentation != null ? this.documentation : StringUtils.EMPTY));
                }
            }
        }
    }
}
