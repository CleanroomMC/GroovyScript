package com.cleanroommc.groovyscript.compat.mods.draconicevolution.helpers;

public interface TileInvisECoreBlockState {

    boolean getDefault();

    /**
     * This should also set metadata to 0.
     */
    void setIsDefault();

    int getMetadata();

    /**
     * This should also set default to false, if meta set is not 0 (legacy compat)
     */
    void setMetadata(int metadata);
}
