package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.api.IGroovyPropertyGetter;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.GroovyObject;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

public class SimpleGroovyInterceptor extends GroovyInterceptor {

    @Override
    public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
        // TODO: Do this at compile time
        if (receiver instanceof GroovyObject) {
            if (args.length >= 1 && args[0] instanceof String) {
                IBracketHandler<?> bracketHandler = BracketHandlerManager.getBracketHandler(method);
                if (bracketHandler != null) {
                    return bracketHandler.parse(args);
                }
            }
        }
        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        String obfMethodName = method;
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            obfMethodName = GroovyDeobfuscationMapper.getObfuscatedMethodName(clazz, method, args);
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
            obfMethodName = GroovyDeobfuscationMapper.getObfuscatedMethodName(receiver, method, args);
        }
        if (!InterceptionManager.INSTANCE.isValid(receiver, method, obfMethodName, args)) {
            throw SandboxSecurityException.format("Prohibited static method call '" + method + "' on class '" + receiver.getName() + "'!");
        }
        return super.onStaticCall(invoker, receiver, obfMethodName, args);
    }

    @Override
    public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
        if (receiver instanceof IGroovyPropertyGetter) {
            Object r = ((IGroovyPropertyGetter) receiver).getProperty(property);
            return r != null ? r : super.onGetProperty(invoker, receiver, property);
        }
        Class<?> clazz = receiver instanceof Class ? (Class<?>) receiver : receiver.getClass();
        String obfPropertyName = property;
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) {
            obfPropertyName = GroovyDeobfuscationMapper.getObfuscatedFieldName(clazz, property);
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
            obfPropertyName = GroovyDeobfuscationMapper.getObfuscatedFieldName(clazz, property);
        }
        if (InterceptionManager.INSTANCE.isBlacklistedField(clazz, obfPropertyName, FieldAccess.SET)) {
            throw SandboxSecurityException.format("Prohibited property access '" + property + "' on class '" + clazz.getName() + "'!");
        }
        return super.onSetProperty(invoker, receiver, obfPropertyName, value);
    }
}
