package com.cleanroommc.groovyscript.sandbox.security;

import com.cleanroommc.groovyscript.GroovyScript;
import sun.misc.Unsafe;

import java.io.FilePermission;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Permission;

public class SandboxSecurityManager extends SecurityManager {

    private static final Object securityFieldBase;
    private static final long securityFieldOffset;
    private static final Unsafe UNSAFE;
    private final SecurityManager parent;

    // cursed
    static {
        Object base = null;
        long offset = 0;
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);

            Method getFields = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            getFields.setAccessible(true);
            for (Field field : (Field[]) getFields.invoke(System.class, false)) {
                if (field.getName().equals("security")) {
                    offset = unsafe.staticFieldOffset(field);
                    base = unsafe.staticFieldBase(field);
                    break;
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        securityFieldBase = base;
        securityFieldOffset = offset;
        UNSAFE = unsafe;
    }

    public SandboxSecurityManager() {
        this.parent = System.getSecurityManager();
        if (this.parent == null) {
            throw new NullPointerException();
        }
    }

    public void install() {
        UNSAFE.putObject(securityFieldBase, securityFieldOffset, this);
    }

    public void uninstall() {
        System.setSecurityManager(this.parent);
    }

    public void checkFile(Permission perm) {
        if (perm instanceof FilePermission filePerm) {
            String path = filePerm.getName();
            Class<?>[] classContext = getClassContext();
            if (!path.startsWith(GroovyScript.getMinecraftHome().getPath())) {
                for (Class<?> clazz : classContext) {
                    if (ClassLoader.class.isAssignableFrom(clazz)) {
                        // allow loading classes
                        return;
                    }
                }
                throw new SecurityException("Only files in minecraft home and sub directories can be accessed from scripts! Tried to access " + perm.getName());
            }
        }
    }

    @Override
    public Object getSecurityContext() {
        return parent.getSecurityContext();
    }

    @Override
    public void checkPermission(Permission perm) {
        parent.checkPermission(perm);
        checkFile(perm);
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        parent.checkPermission(perm, context);
        checkFile(perm);
    }
}
