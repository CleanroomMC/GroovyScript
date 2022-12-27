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
        AtomicReference<IConstellation> rVal = new AtomicReference<>(null);

        if (ConstellationRegistryAccessor.getConstellationList() != null) {
            ConstellationRegistryAccessor.getConstellationList().forEach(constellation -> {
                if (constellation.getSimpleName().equalsIgnoreCase(arg)) rVal.set(constellation);
            });
        }

        if (rVal.get() == null) {
            GroovyLog.get().error("Can't find constellation for '{}'", arg);
            return null;
        } else {
            return rVal.get();
        }
    }

}
