package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import thaumcraft.api.aspects.Aspect;

public class AspectBracketHandler implements IBracketHandler<AspectStack> {

    public static final AspectBracketHandler INSTANCE = new AspectBracketHandler();

    private AspectBracketHandler() {
    }

    public static Aspect validateAspect(String tag) {
        Aspect aspect = Aspect.getAspect(tag);
        if (aspect == null) GroovyLog.msg("Can't find aspect for name {}!", tag).error().post();
        return aspect;
    }

    @Override
    public AspectStack parse(String mainArg, Object[] args) {
        if (args.length > 1 || (args.length == 1 && !(args[0] instanceof Integer))) {
            throw new IllegalArgumentException("Arguments not valid for bracket handler. Use 'aspect(String)' or 'aspect(String, int quantity)'");
        }
        if (Aspect.getAspect(mainArg) == null) {
            return null;
        }
        if (args.length == 1) {
            int quantity = (int) args[0];
            if (quantity < 0) {
                throw new IllegalArgumentException("Arguments not valid for bracket handler. 'aspect('{}', int quantity)' quantity must be greater than 0");
            } else {
                return new AspectStack(mainArg, quantity);
            }
        } else {
            return new AspectStack(mainArg);
        }
    }

    @Override
    public AspectStack parse(String arg) {
        if (Aspect.getAspect(arg) == null) {
            return null;
        } else {
            return new AspectStack(arg);
        }
    }
}
