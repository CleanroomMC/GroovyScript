package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.GameRules;

import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = @Admonition(value = "groovyscript.wiki.minecraft.game_rule.note", type = Admonition.Type.WARNING))
public class GameRule extends NamedRegistry implements IScriptReloadable {

    private static final String LOG_MESSAGE = "Could not find an already existing rule with the name {}. This may be intentional! If it is, you can disable this via `gameRule.setWarnNewGameRule(false)`";
    private final Map<String, String> defaultGameRules = new Object2ObjectOpenHashMap<>();
    private boolean warnNewGameRule;

    @GroovyBlacklist
    public void applyDefaultGameRules(GameRules gameRules) {
        defaultGameRules.forEach((k, v) -> {
            if (warnNewGameRule && !gameRules.hasRule(k)) GroovyLog.get().warn(LOG_MESSAGE, k);
            GroovyLog.get().debug("Setting the GameRule '{}' to the value '{}'", k, v);
            gameRules.setOrCreateGameRule(k, v);
        });
        GroovyLog.get().debug("Set or created {} GameRules", defaultGameRules.size());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("'doDaylightCycle', 'false'"))
    public void add(String gameRule, String value) {
        defaultGameRules.put(gameRule, value);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.game_rule.addMap", example = @Example("['mobGriefing': 'false', 'keepInventory': 'true']"))
    public void add(Map<String, String> gameRules) {
        defaultGameRules.putAll(gameRules);
    }

    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example("true"))
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
