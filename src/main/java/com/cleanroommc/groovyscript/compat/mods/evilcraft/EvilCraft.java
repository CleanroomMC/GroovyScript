package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.IGameObjectParser;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandler;
import org.cyclops.evilcraft.core.weather.WeatherType;

import java.util.Arrays;
import java.util.List;

public class EvilCraft extends ModPropertyContainer {

    public final BloodInfuser bloodInfuser = new BloodInfuser();
    public final EnvironmentalAccumulator environmentalAccumulator = new EnvironmentalAccumulator();

    public EvilCraft() {
        addRegistry(bloodInfuser);
        addRegistry(environmentalAccumulator);
    }

    @Override
    public void initialize() {
        final List<String> weatherTypes = Arrays.asList("any", "clear", "rain", "lightning");
        GameObjectHandler.builder("weather", WeatherType.class)
                .mod("evilcraft")
                .parser(IGameObjectParser.wrapStringGetter(WeatherType::valueOf, true))
                .completerOfNames(() -> weatherTypes) // elements don't have names
                .defaultValue(() -> WeatherType.ANY)
                .register();
    }
}
