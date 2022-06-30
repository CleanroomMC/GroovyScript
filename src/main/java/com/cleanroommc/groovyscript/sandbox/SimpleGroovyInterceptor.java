package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import java.util.Arrays;

public class SimpleGroovyInterceptor extends GroovyInterceptor {

    public static void makeSureExists() {
        if (!getApplicableInterceptors().isEmpty()) {
            for (GroovyInterceptor interceptor : getApplicableInterceptors()) {
                if (interceptor.getClass() == SimpleGroovyInterceptor.class) {
                    return;
                }
            }
        }
        new SimpleGroovyInterceptor().register();
    }

    @Override
    public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        if (method.equals("print") || method.equals("println") || method.equals("printf")) {
            Object msg = args.length == 0 ? "" : args[0];
            Object[] args2 = args.length < 2 ? new Object[0] : Arrays.copyOfRange(args, 1, args.length);
            GroovyLog.LOG.info(msg.toString(), args2);
            return null;
        }

        if (!InterceptionManager.INSTANCE.isValid(receiver.getClass(), method)) {
            throw SandboxSecurityException.format("Prohibited method call on class '" + receiver.getClass().getName() + "'!");
        }
        return super.onMethodCall(invoker, receiver, method, args);
    }

    @Override
    public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
        if (!InterceptionManager.INSTANCE.isValid(receiver, "<init>")) {
            throw SandboxSecurityException.format("Prohibited constructor call on class '" + receiver.getName() + "'!");
        }
        return super.onNewInstance(invoker, receiver, args);
    }

    @Override
    public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
        if (!InterceptionManager.INSTANCE.isValid(receiver, method)) {
            throw SandboxSecurityException.format("Prohibited static method call on class '" + receiver.getName() + "'!");
        }
        return super.onStaticCall(invoker, receiver, method, args);
    }
}
