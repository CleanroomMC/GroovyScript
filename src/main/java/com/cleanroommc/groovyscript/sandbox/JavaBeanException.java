package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.SideOnlyConfig;
import groovy.lang.MetaClassImpl;
import io.github.classgraph.*;
import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaBeanException extends RuntimeException {

    public final Throwable parent;
    public final MetaClassImpl metaClass;
    public final List<String> failingMembers;

    private JavaBeanException(Throwable parent, MetaClassImpl metaClass, List<String> failingMembers) {
        super("An error occurred while trying to gather java properties for class " + metaClass.getTheClass().getName());
        this.parent = parent;
        this.metaClass = metaClass;
        this.failingMembers = failingMembers;
    }

    public void logError(String script) {
        GroovyLog.Msg msg = GroovyLog.msg("Script '{}' ran into an error while loading class '{}'", script, this.metaClass.getTheClass().getName())
                .error();
        if (this.failingMembers.isEmpty()) {
            msg.add("no failing members could be found");
        } else {
            msg.add("Found {} failing members. GroovyScript will add these to sideOnlyGenerated.json to attempt to fix it.", this.failingMembers.size());
            msg.add("Please restart the game. If the EXACT SAME ERROR still appears, report the issue to the mod authors.");
            SideOnlyConfig.addAutoDetectedFailingMembers(this.metaClass.getTheClass(), this.failingMembers);
            SideOnlyConfig.writeGeneratedConfig();
        }
        msg.post();
    }

    public static JavaBeanException of(Throwable parent, MetaClassImpl metaClass) {
        return new JavaBeanException(parent, metaClass, findFailingMembers(metaClass.getTheClass()));
    }

    private static List<String> findFailingMembers(Class<?> c) {
        ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableMethodInfo()
                .enableFieldInfo()
                .enableSystemJarsAndModules()
                .overrideClassLoaders(Launch.classLoader)
                .acceptClasses(c.getName())
                .scan();
        ClassInfo ci = scanResult.getClassInfo(c.getName());

        try {
            ci.loadClass();
        } catch (Throwable e) {
            scanResult.close();
            return Collections.emptyList();
        }

        List<String> failingMembers = new ArrayList<>();

        for (MethodInfo mi : ci.getDeclaredConstructorInfo()) {
            try {
                mi.loadClassAndGetConstructor();
            } catch (Throwable t) {
                failingMembers.add(mi.getName() + "()");
            }
        }

        for (MethodInfo mi : ci.getDeclaredMethodInfo()) {
            try {
                mi.loadClassAndGetMethod();
            } catch (Throwable t) {
                failingMembers.add(mi.getName() + "()");
            }
        }

        for (FieldInfo fi : ci.getDeclaredFieldInfo()) {
            try {
                fi.loadClassAndGetField();
            } catch (Throwable t) {
                failingMembers.add(fi.getName());
            }
        }

        scanResult.close();
        return failingMembers;
    }
}
