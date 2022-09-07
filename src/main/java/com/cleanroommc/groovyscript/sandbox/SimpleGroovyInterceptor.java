package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.event.GroovyEvent;
import com.cleanroommc.groovyscript.event.IGroovyEventHandler;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import java.util.Arrays;

public class SimpleGroovyInterceptor extends GroovyInterceptor {

    private static final String PRINT = "print", PRINTF = "printf", PRINTLN = "println";

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
        if (receiver.getClass().getSuperclass() == Script.class) {
            if (args.length >= 1 && args[0] instanceof String) {
                IBracketHandler bracketHandler = BracketHandlerManager.getBracketHandler(method);
                if (bracketHandler != null) {
                    return bracketHandler.parse(args);
                }
            }
            if (method.equals(PRINT) || method.equals(PRINTLN) || method.equals(PRINTF)) {
                Object msg = args.length == 0 ? "" : args[0];
                Object[] args2 = args.length < 2 ? new Object[0] : Arrays.copyOfRange(args, 1, args.length);
                GroovyLog.LOG.info(msg.toString(), args2);
                return null;
            }
        }


        if (receiver instanceof IGroovyEventHandler && args.length >= 1 && args[0] instanceof Closure) {
            ((IGroovyEventHandler) receiver).getEventManager().registerListener(method, (Closure<Object>) args[0]);
            return null;
        }

        if (receiver instanceof GroovyEvent.Group && args.length >= 1 && args[0] instanceof Closure) {
            ((GroovyEvent.Group) receiver).registerListener(method, (Closure<Object>) args[0]);
            return null;
        }

        if (!InterceptionManager.INSTANCE.isValid(receiver.getClass(), method, args)) {
            throw SandboxSecurityException.format("Prohibited method call '" + method + "' on class '" + receiver.getClass().getName() + "'!");
        }
        return super.onMethodCall(invoker, receiver, method, args);
    }

    @Override
    public Object onNewInstance(Invoker invoker, Class<?> receiver, Object... args) throws Throwable {
        if (!InterceptionManager.INSTANCE.isValidConstructor(receiver, args)) {
            throw SandboxSecurityException.format("Prohibited constructor call on class '" + receiver.getName() + "'!");
        }
        return super.onNewInstance(invoker, receiver, args);
    }

    @Override
    public Object onStaticCall(Invoker invoker, Class<?> receiver, String method, Object... args) throws Throwable {
        if (!InterceptionManager.INSTANCE.isValid(receiver, method, args)) {
            throw SandboxSecurityException.format("Prohibited static method call '" + method + "' on class '" + receiver.getName() + "'!");
        }
        return super.onStaticCall(invoker, receiver, method, args);
    }

    @Override
    public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
        if (receiver instanceof IGroovyPropertyGetter) {
            Object r = ((IGroovyPropertyGetter) receiver).getProperty(property);
            return r != null ? r : super.onGetProperty(invoker, receiver, property);
        }
        if (receiver instanceof IGroovyEventHandler) {
            return onGetProperty(invoker, ((IGroovyEventHandler) receiver).getEventManager(), property);
        }
        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        if (InterceptionManager.INSTANCE.isBlacklistedField(clazz, property, FieldAccess.GET)) {
            throw SandboxSecurityException.format("Prohibited property access '" + property + "' on class '" + clazz.getName() + "'!");
        }
        return super.onGetProperty(invoker, receiver, property);
    }

    @Override
    public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        if (InterceptionManager.INSTANCE.isBlacklistedField(clazz, property, FieldAccess.SET)) {
            throw SandboxSecurityException.format("Prohibited property access '" + property + "' on class '" + clazz.getName() + "'!");
        }
        return super.onSetProperty(invoker, receiver, property, value);
    }
}
