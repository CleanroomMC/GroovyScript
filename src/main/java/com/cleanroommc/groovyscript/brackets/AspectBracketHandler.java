package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import thaumcraft.api.aspects.Aspect;

public class AspectBracketHandler implements IBracketHandler<AspectStack> {

    public static final AspectBracketHandler INSTANCE = new AspectBracketHandler();

    private AspectBracketHandler() {}

    @Override
    public AspectStack parse(Object[] args) {
        if (args.length > 2 || (args.length == 2 && !(args[1] instanceof Integer))) {
            throw new IllegalArgumentException("Arguments not valid for bracket handler. Use 'aspect(String)' or 'aspect(String, int quantity)'");
        }
        String main = (String) args[0];
        if (Aspect.getAspect(main) == null) {
            GroovyLog.get().error("Can't find aspect for '{}'", main);
            return null;
        }
        if (args.length == 2) {
            int quantity = (int) args[1];
            if (quantity < 0) {
                throw new IllegalArgumentException("Arguments not valid for bracket handler. 'aspect('{}', int quantity)' quantity must be greater than 0");
            } else {
                return new AspectStack(main, quantity);
            }
        } else {
            return new AspectStack(main);
        }
    }

    @Override
    public AspectStack parse(String arg) {
        if (Aspect.getAspect(arg) == null) {
            GroovyLog.get().error("Can't find aspect for '{}'", arg);
            return null;
        } else {
            return new AspectStack(arg);
        }
    }
}
