package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyLog;

public class NormalClass {

    private static int call;

    public static void call() {
        GroovyLog.get().infoMC("Calling method in NormalClass");
    }

    public static void failingMethod(String s, ServerClass serverClass) {

    }

}
