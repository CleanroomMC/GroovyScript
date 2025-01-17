package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.sandbox.expand.IDocumented;
import com.cleanroommc.groovyscript.server.CompletionParams;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.Closure;
import groovy.lang.groovydoc.Groovydoc;
import groovy.lang.groovydoc.GroovydocHolder;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.*;

public abstract class AbstractObjectMapper<T> extends Closure<T> implements INamed, IDocumented, IObjectParser<T>, TextureBinder<T> {

    private final String name;
    private final GroovyContainer<?> mod;
    private final Class<T> returnType;
    private final List<Class<?>[]> paramTypes;
    protected String documentation = StringUtils.EMPTY;
    private List<MethodNode> methodNodes;

    protected AbstractObjectMapper(String name, GroovyContainer<?> mod, Class<T> returnType) {
        super(null);
        this.name = name;
        this.mod = mod;
        this.returnType = returnType;
        this.paramTypes = new ArrayList<>();
        addSignature(String.class);
    }

    /**
     * Call in ctor to configure signatures
     */
    protected final void clearSignatures() {
        this.paramTypes.clear();
        this.methodNodes = null;
    }

    /**
     * Call in ctor to configure signatures.
     * By default, only `name(String)` exists.
     */
    protected final void addSignature(Class<?>... types) {
        this.paramTypes.add(types);
        this.methodNodes = null;
    }

    public final T doCall(String s, Object... args) {
        return invokeWithDefault(false, s, args);
    }

    public final T doCall() {
        return invokeDefault();
    }

    @Nullable
    public final T invoke(boolean silent, String s, Object... args) {
        Result<T> t = Objects.requireNonNull(parse(s, args), "Object mapper must return a non null result!");
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

    public final T invokeWithDefault(boolean silent, String s, Object... args) {
        T t = invoke(silent, s, args);
        return t != null ? t : invokeDefault();
    }

    public final T invokeDefault() {
        Result<T> t = getDefaultValue();
        return t == null || t.hasError() ? null : t.getValue();
    }

    /**
     * Returns a default value for this mapper. This is called every time the parser returns an errored result.
     *
     * @return default value of this mapper. May be null
     */
    public abstract Result<T> getDefaultValue();

    /**
     * Adds all possible values this mapper can have at a param position.
     * For example the `item()` mapper adds all item registry names when the index is 0.
     *
     * @param index the index of the param to complete
     * @param params the values of all current (constant) params of the mapper
     * @param items a list of completion items
     */
    public void provideCompletion(int index, CompletionParams params, Completions items) {}

    /**
     * Draws an image representation of the given object. This is used for lsp.
     * The icon will show up in VSC or other code editors with compat.
     * If this is implemented, {@link #hasTextureBinder()} must return true. Otherwise, this will not be used.
     *
     * @param t object for which a texture should be drawn.
     */
    public void bindTexture(T t) {}

    /**
     * Determines if {@link #bindTexture(Object)} is implemented and should be used.
     *
     * @return true if this mapper can bind textures
     */
    public abstract boolean hasTextureBinder();

    @NotNull
    public List<String> getTooltip(T t) {
        return Collections.emptyList();
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
                node.setDeclaringClass(
                        this.mod != null ? ClassHelper.makeCached(this.mod.get().getClass()) : ClassHelper.makeCached(ObjectMapperManager.class));
                node.setNodeMetaData(GroovydocHolder.DOC_COMMENT, new Groovydoc(getDocumentation(), node));
                this.methodNodes.add(node);
            }
        }
        return methodNodes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<String> getAliases() {
        return Collections.singleton(this.name);
    }

    public GroovyContainer<?> getMod() {
        return mod;
    }

    public Class<T> getReturnType() {
        return returnType;
    }

    public List<Class<?>[]> getParamTypes() {
        return paramTypes;
    }

    @Override
    public final String getDocumentation() {
        return documentation;
    }

    protected final String docOfType(String type) {
        String mod = this.mod == null ? StringUtils.EMPTY : this.mod.getContainerName() + ' ';
        return "returns a " + mod + type;
    }
}
