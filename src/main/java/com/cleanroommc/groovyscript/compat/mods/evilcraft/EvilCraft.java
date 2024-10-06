package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.IObjectParser;
import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import org.cyclops.evilcraft.core.weather.WeatherType;

import java.util.Arrays;
import java.util.List;

public class EvilCraft extends GroovyPropertyContainer {

    public final BloodInfuser bloodInfuser = new BloodInfuser();
    public final EnvironmentalAccumulator environmentalAccumulator = new EnvironmentalAccumulator();

    public static String asGroovyCode(String weatherType, boolean colored) {
        return GroovyScriptCodeConverter.formatGenericHandler("weather", weatherType, colored);
    }

    @Override
    public void initialize(GroovyContainer<?> container) {
        final List<String> weatherTypes = Arrays.asList("any", "clear", "rain", "lightning");
        container.objectMapperBuilder("weather", WeatherType.class)
                .parser(IObjectParser.wrapStringGetter(WeatherType::valueOf, true))
                .completerOfNames(() -> weatherTypes) // elements don't have names
                .defaultValue(() -> WeatherType.ANY)
                .docOfType("weather type")
                .register();

        InfoParserRegistry.addInfoParser(InfoParserWeather.instance);
    }
}
