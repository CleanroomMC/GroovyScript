package com.cleanroommc.groovyscript.sandbox;

public class AliasClassLoader extends ClassLoader {

    public static AliasClassLoader create() {
        return new AliasClassLoader(AliasClassLoader.class.getClassLoader());
    }

    public AliasClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = AliasGroovyManager.getClass(name);
        return clazz != null ? clazz : super.loadClass(name, resolve);
    }
}
