package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyLog;

public class TestClass {

    static {
        GroovyLog.get().info("Hello we are now initialising TestClass");
    }

    public static void sayHello() {
        GroovyLog.get().info("Hello from TestClass");
    }
}
