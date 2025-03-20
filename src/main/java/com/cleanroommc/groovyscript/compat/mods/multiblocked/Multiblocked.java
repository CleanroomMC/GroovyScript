package com.cleanroommc.groovyscript.compat.mods.multiblocked;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.multiblocked.api.recipe.RecipeMap;

public class Multiblocked extends GroovyPropertyContainer {

    @Override
    public void initialize(GroovyContainer<?> owner) {
        super.initialize(owner);
        GroovyScript.getSandbox().registerBinding("mbd", mappers);
    }

    public final MbdMappers mappers = new MbdMappers();

    public final RecipeMapManager recipeMap(String name) {
        if (!RecipeMap.RECIPE_MAP_REGISTRY.containsKey(name)) {
            RecipeMap recipeMap = new RecipeMap(name);
            RecipeMap.register(recipeMap);
        }
        return new RecipeMapManager(RecipeMap.RECIPE_MAP_REGISTRY.get(name));
    }
}