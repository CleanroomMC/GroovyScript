package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.IGameObjectHandler;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import org.cyclops.evilcraft.core.weather.WeatherType;

public class EvilCraft extends ModPropertyContainer {

    public final BloodInfuser bloodInfuser = new BloodInfuser();
    public final EnvironmentalAccumulator environmentalAccumulator = new EnvironmentalAccumulator();

    public EvilCraft() {
        addRegistry(bloodInfuser);
        addRegistry(environmentalAccumulator);
    }

    @Override
    public void initialize() {
        GameObjectHandlerManager.registerGameObjectHandler("evilcraft", "weather", IGameObjectHandler.wrapStringGetter(WeatherType::valueOf, true));
    }
}
