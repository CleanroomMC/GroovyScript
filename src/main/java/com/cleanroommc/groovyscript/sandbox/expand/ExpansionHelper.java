package com.cleanroommc.groovyscript.sandbox.expand;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.ClosureHelper;
import groovy.lang.*;
import groovy.transform.Internal;
import org.codehaus.groovy.reflection.*;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.codehaus.groovy.runtime.metaclass.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Function;

public class ExpansionHelper {

    public static ExpandoMetaClass getExpandoClass(Class<?> clazz) {
        return getExpandoClass(GroovySystem.getMetaClassRegistry().getMetaClass(clazz));
    }

    public static ExpandoMetaClass getExpandoClass(MetaClass clazz) {
        if (clazz instanceof HandleMetaClass) {
            clazz = (MetaClass) ((HandleMetaClass) clazz).replaceDelegate();
        }

        if (!(clazz instanceof ExpandoMetaClass)) {
            if (clazz instanceof DelegatingMetaClass && ((DelegatingMetaClass) clazz).getAdaptee() instanceof ExpandoMetaClass) {
                clazz = ((DelegatingMetaClass) clazz).getAdaptee();
            } else {
                ExpandoMetaClass emc = new ExpandoMetaClass(clazz.getTheClass(), true, true);
                emc.initialize();
                return emc;
            }
        }

        return (ExpandoMetaClass) clazz;
    }

    public static MixinInMetaClass getMixinMetaClass(Class<?> clazz, ExpandoMetaClass emc) {
        return new MixinInMetaClass(emc, ReflectionCache.getCachedClass(clazz));
    }

    public static MixinInMetaClass getMixinMetaClass(MetaClass metaClass, ExpandoMetaClass emc) {
        return getMixinMetaClass(metaClass.getTheClass(), emc);
    }

    public static void mixinProperties(ExpandoMetaClass emc, MetaClass metaClass, MixinInMetaClass mixin) {
        final CachedClass cachedCategoryClass = ReflectionCache.getCachedClass(metaClass.getClass());

        for (MetaProperty prop : metaClass.getProperties()) {
            if (isValidProperty(prop) && emc.getMetaProperty(prop.getName()) == null) {
                emc.registerBeanProperty(prop.getName(), new MixinInstanceMetaProperty(prop, mixin));
            }
        }

        for (MetaProperty prop : cachedCategoryClass.getFields()) {
            if (isValidProperty(prop) && emc.getMetaProperty(prop.getName()) == null) {
                emc.registerBeanProperty(prop.getName(), new MixinInstanceMetaProperty(prop, mixin));
            }
        }
    }

    private static boolean isValidProperty(MetaProperty prop) {
        if (prop instanceof MetaBeanProperty) {
            MetaBeanProperty beanProperty = (MetaBeanProperty) prop;
            if (!isValid(beanProperty.getField())) return false;
            if (!(beanProperty.getGetter() instanceof CachedMethod) || isValid((CachedMethod) beanProperty.getGetter()))
                return false;
            return beanProperty.getSetter() instanceof CachedMethod && !isValid((CachedMethod) beanProperty.getSetter());
        }
        if (prop instanceof MethodMetaProperty && ((MethodMetaProperty) prop).getMetaMethod() instanceof CachedMethod) {
            return isValid((CachedMethod) ((MethodMetaProperty) prop).getMetaMethod());
        }
        if (prop instanceof CachedField) {
            return isValid((CachedField) prop);
        }
        return false;
    }

    public static void mixinClasses(Class<?> self, Collection<Class<?>> mixinClasses) {
        ExpandoMetaClass emc = getExpandoClass(self);
        for (Class<?> clazz : mixinClasses) {
            mixinClass(emc, getMixinMetaClass(clazz, emc), clazz);
        }
    }

    public static void mixinClass(Class<?> self, Class<?> mixinClass) {
        ExpandoMetaClass emc = getExpandoClass(self);
        MixinInMetaClass mixin = getMixinMetaClass(mixinClass, emc);
        mixinClass(emc, mixin, mixinClass);
    }

    private static void mixinClass(ExpandoMetaClass emc, MixinInMetaClass mixin, Class<?> clazz) {
        MetaClass other = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
        mixinProperties(emc, other, mixin);
        for (MetaMethod method : other.getMethods()) {
            if (method instanceof CachedMethod && clazz == ((CachedMethod) method).getCachedMethod().getDeclaringClass()) {
                mixinMethod(emc, (CachedMethod) method, mixin);
            }
        }
    }

    public static void mixinMethod(Class<?> self, Method method) {
        ExpandoMetaClass emc = getExpandoClass(self);
        MixinInMetaClass mixin = Modifier.isStatic(method.getModifiers()) ? null : getMixinMetaClass(method.getDeclaringClass(), emc);
        mixinMethod(emc, CachedMethod.find(method), mixin);
    }

    public static void mixinMethod(Class<?> self, Class<?> other, String methodName) {
        ExpandoMetaClass emc = getExpandoClass(self);
        MixinInMetaClass mixin = getMixinMetaClass(other, emc);
        for (Method method : other.getDeclaredMethods()) {
            if (methodName.equals(method.getName())) {
                mixinMethod(emc, CachedMethod.find(method), mixin);
            }
        }
    }

    public static void mixinMethod(Class<?> self, String name, Function<Object[], ?> function) {
        ExpandoMetaClass emc = getExpandoClass(self);
        emc.registerInstanceMethod(name, ClosureHelper.of(function));
    }

    private static void mixinMethod(ExpandoMetaClass self, CachedMethod method, MixinInMetaClass mixin) {
        final int mod = method.getModifiers();
        if (!isValid(method)) {
            return;
        }
        CachedClass[] paramTypes = method.getParameterTypes();
        MetaMethod metaMethod;
        if (Modifier.isStatic(mod)) {
            if (paramTypes.length > 0 && paramTypes[0].isAssignableFrom(self.getTheClass())) {
                // instance method disguised as static method
                if (paramTypes[0].getTheClass() == self.getTheClass())
                    metaMethod = new NewInstanceMetaMethod(method);
                else
                    metaMethod = new NewInstanceMetaMethod(method) {
                        public CachedClass getDeclaringClass() {
                            return ReflectionCache.getCachedClass(self.getTheClass());
                        }
                    };
            } else {
                // true static method
                metaMethod = new NewStaticMetaMethod(self.getTheCachedClass(), method);
            }
        } else if (method.getDeclaringClass().getTheClass() != Object.class || "toString".equals(method.getName())) {
            metaMethod = new MixinInstanceMetaMethod(method, mixin);
        } else {
            return;
        }
        self.registerInstanceMethod(metaMethod);
    }

    private static boolean isValid(CachedMethod method) {
        final int mod = method.getModifiers();
        return Modifier.isPublic(mod) && !Modifier.isAbstract(mod) && !method.isSynthetic() &&
               method.getAnnotation(Internal.class) == null &&
               method.getAnnotation(GroovyBlacklist.class) == null;
    }

    private static boolean isValid(CachedField cachedField) {
        if (cachedField == null) return true;
        final int mod = cachedField.getModifiers();
        Field field = cachedField.getCachedField();
        return Modifier.isPublic(mod) && !field.isSynthetic() &&
               !field.isAnnotationPresent(Internal.class) &&
               !field.isAnnotationPresent(GroovyBlacklist.class);
    }

    /**
     * Groovy's {@link org.codehaus.groovy.runtime.metaclass.NewStaticMetaMethod} does weird shit.
     */
    private static class NewStaticMetaMethod extends NewMetaMethod {

        private final CachedClass owner;

        public NewStaticMetaMethod(CachedClass owner, CachedMethod method) {
            super(method);
            this.owner = owner;
        }

        @Override
        public boolean isStatic() {
            return true;
        }

        @Override
        public int getModifiers() {
            return Modifier.PUBLIC;
        }

        @Override
        public CachedClass getDeclaringClass() {
            return owner;
        }

        @Override
        public CachedClass getOwnerClass() {
            return owner;
        }
    }
}
