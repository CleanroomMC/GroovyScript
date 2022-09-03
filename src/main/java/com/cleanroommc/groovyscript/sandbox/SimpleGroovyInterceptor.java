package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.BracketHandler;
import com.cleanroommc.groovyscript.api.IGroovyEventHandler;
import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.event.GroovyEvent;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.Closure;
import groovy.lang.Script;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import java.util.Arrays;
import java.util.function.Function;

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
        if (receiver.getClass().getSuperclass() == Script.class) {
            if (args.length == 1 && args[0] instanceof String) {
                Function<String, Object> bracketHandler = BracketHandler.getBracketHandler(method);
                if (bracketHandler != null) {
                    return bracketHandler.apply((String) args[0]);
                }
            }
            if (method.equals("print") || method.equals("println") || method.equals("printf")) {
                Object msg = args.length == 0 ? "" : args[0];
                if (msg == null) {
                    GroovyLog.LOG.info("null");
                    return null;
                }
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

        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        String obfMethodName = method;
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            obfMethodName = GroovyDeobfuscationMapper.getObfuscatedMethodName(clazz, method);
        }
        if (!InterceptionManager.INSTANCE.isValid(clazz, method, obfMethodName, args)) {
            throw SandboxSecurityException.format("Prohibited method call '" + method + "' on class '" + clazz.getName() + "'!");
        }
        return super.onMethodCall(invoker, receiver, obfMethodName, args);
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
        String obfMethodName = method;
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            obfMethodName = GroovyDeobfuscationMapper.getObfuscatedMethodName(receiver, method);
        }
        if (!InterceptionManager.INSTANCE.isValid(receiver, method, obfMethodName, args)) {
            throw SandboxSecurityException.format("Prohibited static method call '" + method + "' on class '" + receiver.getName() + "'!");
        }
        return super.onStaticCall(invoker, receiver, obfMethodName, args);
    }

    @Override
    public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
        if (receiver instanceof IGroovyEventHandler) {
            receiver = ((IGroovyEventHandler) receiver).getEventManager();
        }
        if (receiver instanceof IGroovyPropertyGetter) {
            Object r = ((IGroovyPropertyGetter) receiver).getProperty(property);
            return r != null ? r : super.onGetProperty(invoker, receiver, property);
        }
        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        String obfPropertyName = property;
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            obfPropertyName = GroovyDeobfuscationMapper.getObfuscatedMethodName(clazz, property);
        }
        if (InterceptionManager.INSTANCE.isBlacklistedField(clazz, obfPropertyName, FieldAccess.GET)) {
            throw SandboxSecurityException.format("Prohibited property access '" + property + "' on class '" + clazz.getName() + "'!");
        }
        return super.onGetProperty(invoker, receiver, obfPropertyName);
    }

    @Override
    public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        String obfPropertyName = property;
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            obfPropertyName = GroovyDeobfuscationMapper.getObfuscatedMethodName(clazz, property);
        }
        if (InterceptionManager.INSTANCE.isBlacklistedField(clazz, obfPropertyName, FieldAccess.SET)) {
            throw SandboxSecurityException.format("Prohibited property access '" + property + "' on class '" + clazz.getName() + "'!");
        }
        return super.onSetProperty(invoker, receiver, obfPropertyName, value);
    }
}
