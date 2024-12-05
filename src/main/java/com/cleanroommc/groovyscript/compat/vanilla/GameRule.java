package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import net.minecraft.world.GameRules;

import java.util.HashMap;
import java.util.Map;

public class GameRule extends NamedRegistry implements IScriptReloadable {

    private static final String LOG_MESSAGE = "Could not find an already existing rule with the name {}. This may be intentional! If it is, you can disable this via `gameRule.setWarnNewGameRule(false)`";
    private final Map<String, String> defaultGameRules = new HashMap<>();
    private boolean warnNewGameRule;

    @GroovyBlacklist
    public void setDefaultGameRules(GameRules gameRules) {
        defaultGameRules.forEach((k, v) -> {
            if (warnNewGameRule && !gameRules.hasRule(k)) GroovyLog.get().warn(LOG_MESSAGE, k);
            GroovyLog.get().debug("Setting the GameRule '{}' to the value '{}'", k, v);
            gameRules.setOrCreateGameRule(k, v);
        });
        GroovyLog.get().debug("Set or created {} GameRules", defaultGameRules.size());
    }

    @MethodDescription
    public void add(String gameRule, String value) {
        defaultGameRules.put(gameRule, value);
    }

    @MethodDescription
    public void add(Map<String, String> gameRules) {
        defaultGameRules.putAll(gameRules);
    }

    @MethodDescription
    public void setWarnNewGameRule(boolean value) {
        warnNewGameRule = value;
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        defaultGameRules.clear();
        warnNewGameRule = false;
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {}
}
