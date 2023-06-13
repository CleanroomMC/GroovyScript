package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.IBracketHandler;
import net.minecraft.util.ResourceLocation;

import static com.cleanroommc.groovyscript.brackets.BracketHandlerManager.SPLITTER;

public class ResourceLocationBracketHandler implements IBracketHandler<ResourceLocation> {

    public static final ResourceLocationBracketHandler INSTANCE = new ResourceLocationBracketHandler();

    private ResourceLocationBracketHandler() {
    }

    @Override
    public ResourceLocation parse(String mainArg, Object[] args) {
        String[] parts = mainArg.split(SPLITTER);
        if (parts.length > 1) {
            return new ResourceLocation(parts[0], parts[1]);
        }

        if (args.length > 1 || (args.length == 1 && !(args[0] instanceof String))) {
            throw new IllegalArgumentException("Arguments not valid for bracket handler. Use 'resource(String)' or 'resource(String mod, String path)'");
        }
        return new ResourceLocation(mainArg, (String) args[0]);
    }

    @Override
    public ResourceLocation parse(String arg) {
        String[] parts = arg.split(SPLITTER);
        if (parts.length < 2) {
            return null;
        }
        return new ResourceLocation(parts[0], parts[1]);
    }
}
