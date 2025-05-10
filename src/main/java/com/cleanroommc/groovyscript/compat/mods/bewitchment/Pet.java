package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.bewitchment.api.BewitchmentAPI;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodOverride;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;

import java.util.Collection;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, override = @MethodOverride(method = {
        @MethodDescription(method = "add", type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.add_to_list", example = @Example("entity('minecraft:cow')")),
        @MethodDescription(method = "remove", description = "groovyscript.wiki.remove_from_list", example = @Example("entity('minecraft:ocelot')"))
}))
public class Pet extends StandardListRegistry<EntityEntry> {

    @Override
    public Collection<EntityEntry> getRecipes() {
        return BewitchmentAPI.VALID_PETS;
    }
}
