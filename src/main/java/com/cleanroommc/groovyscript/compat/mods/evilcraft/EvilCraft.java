package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.cyclops.evilcraft.core.weather.WeatherType;

import java.util.Locale;

public class EvilCraft extends ModPropertyContainer {

    public final BloodInfuser bloodInfuser = new BloodInfuser();
    public final EnvironmentalAccumulator environmentalAccumulator = new EnvironmentalAccumulator();

    public EvilCraft() {
        addRegistry(bloodInfuser);
        addRegistry(environmentalAccumulator);
    }

    @Override
    public void initialize() {
        BracketHandlerManager.registerBracketHandler("weather", s -> WeatherType.valueOf(s.toUpperCase(Locale.ROOT)));
    }
}
