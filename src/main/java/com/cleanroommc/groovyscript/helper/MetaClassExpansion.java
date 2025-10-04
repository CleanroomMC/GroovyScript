package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.GroovyLog;
import groovy.lang.*;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;

public class MetaClassExpansion {

    /**
     * Allows making any fields or methods with a specific name public. Logs an error if there is no method or field with that name. The member must be a normal
     * field/method from java and not some special cases like {@link groovy.lang.MetaBeanProperty} or other injected members via groovy.
     * Note that the {@link java.lang.reflect.Field Field} instance groovy stores and the one you get from {@link Class#getDeclaredField(String)} are different.
     * This means that calling this method will make the member only public for groovy, but not for java.
     *
     * @param mc         self meta class
     * @param memberName name of members to make public
     */
    public static void makePublic(MetaClass mc, String memberName) {
        boolean success = false;
        MetaProperty mp = mc.getMetaProperty(memberName);
        CachedField field = null;
        if (mp instanceof MetaBeanProperty beanProperty) {
            field = beanProperty.getField();
        } else if (mp instanceof CachedField cachedField) {
            field = cachedField;
        }
        if (field != null) {
            ReflectionHelper.makeFieldPublic(field.getCachedField());
            success = true;
        }
        for (MetaMethod mm : mc.getMethods()) {
            if (memberName.equals(mm.getName()) && mm instanceof CachedMethod cachedMethod) {
                ReflectionHelper.makeMethodPublic(cachedMethod.getCachedMethod());
                success = true;
            }
        }
        if (!success) {
            GroovyLog.get().error("Failed to make member '{}' of class {} public, because no member was found!", memberName, getName(mc));
        }
    }

    /**
     * Allows making a field with a specific name non-final. Does nothing if the field is already non-final.
     * Logs an error if there is no field with that name. The field must be a normal field from java and not some special cases like
     * {@link groovy.lang.MetaBeanProperty} or other injected members via groovy. Note that the {@link java.lang.reflect.Field Field} instance groovy stores and
     * the one you get from {@link Class#getDeclaredField(String)} are different. This means that calling this method will make the member only non-final for
     * groovy, but not for java.
     *
     * @param mc        self meta class
     * @param fieldName name of field to make non-final
     */
    public static void makeMutable(MetaClass mc, String fieldName) {
        /*while (mc instanceof DelegatingMetaClass delegatingMetaClass) {
            mc = delegatingMetaClass.getAdaptee();
        }*/
        MetaProperty mp = mc.getMetaProperty(fieldName);
        CachedField field = null;
        if (mp instanceof MetaBeanProperty beanProperty) {
            field = beanProperty.getField();
        } else if (mp instanceof CachedField cachedField) {
            field = cachedField;
        }
        if (field != null) {
            ReflectionHelper.setFinal(field.getCachedField(), false);
            return;
        }
        GroovyLog.get().error("Failed to make member '{}' of class {} mutable, because no field was found!", fieldName, getName(mc));
    }

    public static String getName(MetaClass mc) {
        ClassNode cn = mc.getClassNode();
        return cn == null ? "Unknown" : cn.getName();
    }
}
