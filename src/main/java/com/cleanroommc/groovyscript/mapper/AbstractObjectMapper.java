package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.helper.ArrayUtils;
import com.cleanroommc.groovyscript.sandbox.expand.IDocumented;
import com.cleanroommc.groovyscript.server.Completions;
import groovy.lang.Closure;
import groovy.lang.groovydoc.Groovydoc;
import groovy.lang.groovydoc.GroovydocHolder;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class AbstractObjectMapper<T> extends Closure<T> implements INamed, IDocumented, IObjectParser<T>, TextureBinder<T> {

    private final String name;
    private final GroovyContainer<?> mod;
    private final Class<T> returnType;
    protected List<Class<?>[]> paramTypes;
    protected String documentation = StringUtils.EMPTY;
    private List<MethodNode> methodNodes;
    private Boolean hasTextureBinder;

    protected AbstractObjectMapper(String name, GroovyContainer<?> mod, Class<T> returnType) {
        super(null);
        this.name = name;
        this.mod = mod;
        this.returnType = returnType;
        this.paramTypes = new ArrayList<>();
        addSignature(String.class);
    }

    protected final void addSignature(Class<?>... types) {
        this.paramTypes.add(types);
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

    public abstract Result<T> getDefaultValue();


    public void provideCompletion(int index, Completions items) {}

    public void bindTexture(T t) {}

    public List<String> getTooltip(T t) {
        return Collections.emptyList();
    }

    public boolean hasTextureBinder() {
        if (this.hasTextureBinder == null) {
            for (Method method : getClass().getDeclaredMethods()) {
                if (method.getName().equals("bindTexture") && method.getParameterTypes().length == 1 && this.returnType.isAssignableFrom(method.getParameterTypes()[0])) {
                    this.hasTextureBinder = true;
                    return true;
                }
            }
            this.hasTextureBinder = false;
        }
        return this.hasTextureBinder;
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
