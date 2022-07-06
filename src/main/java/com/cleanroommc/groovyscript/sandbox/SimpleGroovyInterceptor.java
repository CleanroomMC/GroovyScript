package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.event.EventHandler;
import com.cleanroommc.groovyscript.event.GroovyEvent;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.Closure;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class SimpleGroovyInterceptor extends GroovyInterceptor {

    public static final String CONSTRUCTOR_METHOD = "<init>";

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

        if (receiver.getClass() == EventHandler.class && args.length >= 1 && args[0] instanceof Closure) {
            GroovyEventManager.registerListener("", method, (Closure<Object>) args[0]);
            return null;
        }
        if (receiver instanceof GroovyEvent.Group && args.length >= 1 && args[0] instanceof Closure) {
            ((GroovyEvent.Group) receiver).registerListener(method, (Closure<Object>) args[0]);
            return null;
        }

        if (!InterceptionManager.INSTANCE.isValid(receiver.getClass(), method) || InterceptionManager.INSTANCE.isBlacklistedMethod(receiver.getClass(), method, args)) {
            throw SandboxSecurityException.format("Prohibited method call '" + method + "' on class '" + receiver.getClass().getName() + "'!");
        }
        return super.onMethodCall(invoker, receiver, method, args);
    }

    @Override
    public Object onNewInstance(Invoker invoker, Class<?> receiver, Object... args) throws Throwable {
        if (!InterceptionManager.INSTANCE.isValid(receiver, CONSTRUCTOR_METHOD) || InterceptionManager.INSTANCE.isBlacklistedMethod(receiver, CONSTRUCTOR_METHOD, args)) {
            throw SandboxSecurityException.format("Prohibited constructor call on class '" + receiver.getName() + "'!");
        }
        return super.onNewInstance(invoker, receiver, args);
    }

    @Override
    public Object onStaticCall(Invoker invoker, Class<?> receiver, String method, Object... args) throws Throwable {
        if (!InterceptionManager.INSTANCE.isValid(receiver, method) || InterceptionManager.INSTANCE.isBlacklistedMethod(receiver, method, args)) {
            throw SandboxSecurityException.format("Prohibited static method call '" + method + "' on class '" + receiver.getName() + "'!");
        }
        return super.onStaticCall(invoker, receiver, method, args);
    }

    @Override
    public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
        if (receiver.getClass() == EventHandler.class) {
            GroovyEvent.Group group = GroovyEventManager.getEventGroup(property);
            if (group == null) {
                throw new NoSuchElementException("There is no such event group '" + property + "'!");
            }
            return group;
        }
        return super.onGetProperty(invoker, receiver, property);
    }
}
