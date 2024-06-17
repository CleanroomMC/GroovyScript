package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.vmplugin.v8.Java8;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

@Mixin(value = Java8.class, remap = false)
public abstract class Java8Mixin {

    @Shadow
    protected abstract ClassNode makeClassNode(CompileUnit cu, Type t, Class<?> c);

    @Shadow
    protected abstract void setAnnotationMetaData(Annotation[] annotations, AnnotatedNode target);

    @Shadow
    protected abstract Parameter[] makeParameters(CompileUnit cu, Type[] types, Class<?>[] cls, Annotation[][] parameterAnnotations, Member member);

    @Shadow
    protected abstract ClassNode[] makeClassNodes(CompileUnit cu, Type[] types, Class<?>[] cls);

    @Shadow
    protected abstract Annotation[][] getConstructorParameterAnnotations(Constructor<?> constructor);

    @Shadow
    protected abstract void makeInterfaceTypes(CompileUnit cu, ClassNode classNode, Class<?> clazz);

    @Shadow
    protected abstract void makePermittedSubclasses(CompileUnit cu, ClassNode classNode, Class<?> clazz);

    @Shadow
    protected abstract void makeRecordComponents(CompileUnit cu, ClassNode classNode, Class<?> clazz);

    @Shadow
    protected abstract GenericsType[] configureTypeParameters(TypeVariable<?>[] tp);

    /**
     * @author brachy84
     * @reason remapping minecraft fields and methods
     */
    @Overwrite
    public void configureClassNode(final CompileUnit compileUnit, final ClassNode classNode) {
        try {
            Class<?> clazz = classNode.getTypeClass();
            Map<String, String> deobfFields = FMLLaunchHandler.isDeobfuscatedEnvironment() ? null : GroovyDeobfMapper.getDeobfFields(clazz);
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                ClassNode ret = makeClassNode(compileUnit, f.getGenericType(), f.getType());
                String name = deobfFields != null ? deobfFields.getOrDefault(f.getName(), f.getName()) : f.getName();
                FieldNode fn = new FieldNode(name, f.getModifiers(), ret, classNode, null);
                setAnnotationMetaData(f.getAnnotations(), fn);
                classNode.addField(fn);
            }
            Map<String, String> deobfMethods = FMLLaunchHandler.isDeobfuscatedEnvironment() ? null : GroovyDeobfMapper.getDeobfMethods(clazz);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                ClassNode ret = makeClassNode(compileUnit, m.getGenericReturnType(), m.getReturnType());
                Parameter[] params = makeParameters(compileUnit, m.getGenericParameterTypes(), m.getParameterTypes(), m.getParameterAnnotations(), m);
                ClassNode[] exceptions = makeClassNodes(compileUnit, m.getGenericExceptionTypes(), m.getExceptionTypes());
                String name = deobfMethods != null ? deobfMethods.getOrDefault(m.getName(), m.getName()) : m.getName();
                MethodNode mn = new MethodNode(name, m.getModifiers(), ret, params, exceptions, null);
                setAnnotationMetaData(m.getAnnotations(), mn);
                if (true) { // TODO: GROOVY-10862
                    mn.setAnnotationDefault(true);
                    mn.setCode(new ReturnStatement(new ConstantExpression(m.getDefaultValue())));
                }
                mn.setGenericsTypes(configureTypeParameters(m.getTypeParameters()));
                mn.setSynthetic(m.isSynthetic());
                classNode.addMethod(mn);
            }
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> ctor : constructors) {
                Parameter[] params = makeParameters(compileUnit, ctor.getGenericParameterTypes(), ctor.getParameterTypes(), getConstructorParameterAnnotations(ctor), ctor);
                ClassNode[] exceptions = makeClassNodes(compileUnit, ctor.getGenericExceptionTypes(), ctor.getExceptionTypes());
                ConstructorNode cn = classNode.addConstructor(ctor.getModifiers(), params, exceptions, null);
                setAnnotationMetaData(ctor.getAnnotations(), cn);
            }

            Class<?> sc = clazz.getSuperclass();
            if (sc != null) classNode.setUnresolvedSuperClass(makeClassNode(compileUnit, clazz.getGenericSuperclass(), sc));
            makeInterfaceTypes(compileUnit, classNode, clazz);
            makePermittedSubclasses(compileUnit, classNode, clazz);
            makeRecordComponents(compileUnit, classNode, clazz);
            setAnnotationMetaData(clazz.getAnnotations(), classNode);

            PackageNode packageNode = classNode.getPackage();
            if (packageNode != null) {
                setAnnotationMetaData(clazz.getPackage().getAnnotations(), packageNode);
            }
        } catch (NoClassDefFoundError e) {
            throw new NoClassDefFoundError("Unable to load class " + classNode.toString(false) + " due to missing dependency " + e.getMessage());
        } catch (MalformedParameterizedTypeException e) {
            throw new RuntimeException("Unable to configure class node for class " + classNode.toString(false) + " due to malformed parameterized types", e);
        }
    }
}
