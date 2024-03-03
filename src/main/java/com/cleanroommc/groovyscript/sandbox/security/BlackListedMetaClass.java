package com.cleanroommc.groovyscript.sandbox.security;

import com.cleanroommc.groovyscript.sandbox.RunConfig;

import groovy.lang.*;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.Phases;

import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * A {@link MetaClass} that rejects ANY interaction
 */
public class BlackListedMetaClass implements MetaClass {

    private final Class<?> theClass;
    private ClassNode classNode;

    public BlackListedMetaClass(Class<?> theClass) {
        this.theClass = theClass;
    }

    protected SandboxSecurityException makeSecurityError() {
        return SandboxSecurityException.format("Tried to access class " + theClass.getName() + ", but class is blacklisted!");
    }

    @Override
    public Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        throw makeSecurityError();
    }

    @Override
    public Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        throw makeSecurityError();
    }

    @Override
    public void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        throw makeSecurityError();
    }

    @Override
    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        return null;
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        return null;
    }

    @Override
    public Object getAttribute(Class sender, Object receiver, String messageName, boolean useSuper) {
        return null;
    }

    @Override
    public void setAttribute(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        throw makeSecurityError();
    }

    @Override
    public void initialize() {
    }

    @Override
    public List<MetaProperty> getProperties() {
        return Collections.emptyList();
    }

    @Override
    public List<MetaMethod> getMethods() {
        return Collections.emptyList();
    }

    @Override
    public List<MetaMethod> respondsTo(Object obj, String name, Object[] argTypes) {
        return Collections.emptyList();
    }

    @Override
    public List<MetaMethod> respondsTo(Object obj, String name) {
        return Collections.emptyList();
    }

    @Override
    public MetaProperty hasProperty(Object obj, String name) {
        return null;
    }

    @Override
    public MetaProperty getMetaProperty(String name) {
        return null;
    }

    @Override
    public MetaMethod getStaticMetaMethod(String name, Object[] args) {
        return null;
    }

    @Override
    public MetaMethod getMetaMethod(String name, Object[] args) {
        return null;
    }

    @Override
    public Class getTheClass() {
        return theClass;
    }

    @Override
    public Object invokeConstructor(Object[] arguments) {
        throw makeSecurityError();
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        throw makeSecurityError();
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object arguments) {
        throw makeSecurityError();
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        throw makeSecurityError();
    }

    @Override
    public Object getProperty(Object object, String property) {
        throw makeSecurityError();
    }

    @Override
    public void setProperty(Object object, String property, Object newValue) {
        throw makeSecurityError();
    }

    @Override
    public Object getAttribute(Object object, String attribute) {
        throw makeSecurityError();
    }

    @Override
    public void setAttribute(Object object, String attribute, Object newValue) {
        throw makeSecurityError();
    }

    /**
     * Copy pasted from {@link MetaClassImpl}
     *
     * @return class node for this meta class
     */
    @Override
    public ClassNode getClassNode() {
        if (classNode == null && GroovyObject.class.isAssignableFrom(theClass)) {
            // let's try load it from the classpath
            String groovyFile = theClass.getName();
            int idx = groovyFile.indexOf('$');
            if (idx > 0) {
                groovyFile = groovyFile.substring(0, idx);
            }
            groovyFile = groovyFile.replace('.', '/');

            URL url = null;
            for (String suffix : RunConfig.GROOVY_SUFFIXES) {
                url = theClass.getClassLoader().getResource(groovyFile + suffix);
                if (url != null) break;
                url = Thread.currentThread().getContextClassLoader().getResource(groovyFile + suffix);
                if (url != null) break;
            }

            if (url != null) {
                try {

                    /*
                     * todo there is no CompileUnit in scope so class name
                     * checking won't work but that mostly affects the bytecode
                     * generation rather than viewing the AST
                     */
                    CompilationUnit.ClassgenCallback search = (writer, node) -> {
                        if (node.getName().equals(theClass.getName())) {
                            BlackListedMetaClass.this.classNode = node;
                        }
                    };

                    CompilationUnit unit = new CompilationUnit();
                    unit.setClassgenCallback(search);
                    unit.addSource(url);
                    unit.compile(Phases.CLASS_GENERATION);
                } catch (Exception e) {
                    throw new GroovyRuntimeException("Exception thrown parsing: " + groovyFile + ". Reason: " + e, e);
                }
            }

        }
        return classNode;
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        return Collections.emptyList();
    }

    @Override
    public int selectConstructorAndTransformArguments(int numberOfConstructors, Object[] arguments) {
        throw makeSecurityError();
    }

    @Override
    public MetaMethod pickMethod(String methodName, Class[] arguments) {
        return null;
    }
}
