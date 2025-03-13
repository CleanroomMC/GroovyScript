package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class GroovyPropertyContainer extends BasicGroovyPropertyContainer {

    private final Map<String, INamed> properties = new Object2ObjectOpenHashMap<>();
    private final Map<String, INamed> view = Collections.unmodifiableMap(properties);

    protected void addProperty(INamed property) {
        int i = 0;
        for (String alias : property.getAliases()) {
            INamed old = this.properties.put(alias, property);
            if (old != null && old != property && GroovyScript.getRunConfig().isDebug()) {
                // old property is replaced, sometimes this is intended
                GroovyLog.get().warn("Property {} was replaced with property {} in class {}!", old.getName(), alias, getClass());
            }
            ExpansionHelper.mixinConstProperty(getClass(), alias, property, i++ > 0);
        }
    }

    public @UnmodifiableView Collection<INamed> getRegistries() {
        return this.view.values();
    }

    public @UnmodifiableView Map<String, INamed> getProperties() {
        return view;
    }

    /**
     * Register bracket handlers, bindings, expansions etc. here
     */
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void initialize(GroovyContainer<?> owner) {}

    protected void addPropertyFieldsOf(Object object, boolean privateToo) {
        boolean staticOnly = false;
        Class<?> clazz;
        if (object instanceof Class<?>c) {
            clazz = c;
            staticOnly = true;
        } else {
            clazz = object.getClass();
        }
        for (Field field : clazz.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!field.isAnnotationPresent(GroovyBlacklist.class) && INamed.class.isAssignableFrom(field.getType()) && (!staticOnly || isStatic) && (privateToo || (Modifier.isPublic(field.getModifiers())))) {
                try {
                    if (!field.isAccessible()) field.setAccessible(true);
                    Object o = field.get(isStatic ? null : object);
                    if (o != null) {
                        addProperty((INamed) o);
                    }
                } catch (IllegalAccessException e) {
                    GroovyLog.get().errorMC("Failed to register {} as named property", field.getName());
                }
            }
        }
    }
}
