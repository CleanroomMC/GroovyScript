package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated this class has been replaced by {@link GroovyPropertyContainer}
 */
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
@Deprecated
public class ModPropertyContainer extends GroovyPropertyContainer {

    /**
     * @deprecated use {@link #addProperty(INamed)}
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @Deprecated
    protected void addRegistry(INamed property) {
        addProperty(property);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @Deprecated
    public @Nullable Object getProperty(String name) {
        INamed property = getProperties().get(name);
        if (property == null) {
            GroovyLog.get().error("Attempted to access property {}, but could not find a property with that name", name);
            return null;
        }
        if (!property.isEnabled()) {
            GroovyLog.get().error("Attempted to access registry {}, but that registry was disabled", property.getName());
            return null;
        }
        return property;
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @Deprecated
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void initialize() {
    }

    /**
     * Register bracket handlers, bindings, expansions etc. here
     */
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    @Override
    public void initialize(GroovyContainer<?> owner) {
        initialize();
    }

}

