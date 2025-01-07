package com.cleanroommc.groovyscript.api;

import java.util.List;

/**
 * Indicates that the IIngredient represents one or more oredicts.
 */
public interface IOreDicts extends IIngredient {
    // TODO
    //  There are a large number of places currently in the GroovyScript codebase
    //  that check if something is "instanceof OreDictIngredient".
    //  these should be replaced by checks against IOreDicts,
    //  and surrounding code changed appropriately.

    /**
     * @return a list of oredict strings
     */
    List<String> getOreDicts();
}
