package com.cleanroommc.groovyscript.brackets;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IBracketHandler;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationRegistryAccessor;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;

import java.util.concurrent.atomic.AtomicReference;

public class ConstellationBracketHandler implements IBracketHandler<IConstellation> {

    public static final ConstellationBracketHandler INSTANCE = new ConstellationBracketHandler();

    private ConstellationBracketHandler() {}

    @Override
    public IConstellation parse(String arg) {
        if (ConstellationRegistryAccessor.getConstellationList() != null) {
            for (IConstellation constellation : ConstellationRegistryAccessor.getConstellationList()) {
                if (constellation.getSimpleName().equalsIgnoreCase(arg)) return constellation;
            }
        }

        GroovyLog.get().error("Can't find constellation for '{}'", arg);
        return null;
    }

}
