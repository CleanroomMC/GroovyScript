package com.cleanroommc.groovyscript.compat.mods.aether_legacy;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.api.accessories.AetherAccessory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Accessory extends ForgeRegistryWrapper<AetherAccessory> {

    private static final AccessoryType[] VALUES = AccessoryType.values();

    public Accessory() {
        super(GameRegistry.findRegistry(AetherAccessory.class), Alias.generateOf("accessory"));
    }

    public void add(AetherAccessory accessory) {
        if (accessory != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), accessory);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(ItemStack item, String type) {
        AccessoryType accessoryType = AccessoryType.MISC;
        for (AccessoryType value : VALUES) {
            if (value.getDisplayName().equalsIgnoreCase(type)) {
                accessoryType = value;
            }
        }
        AetherAccessory accessory = new AetherAccessory(item, accessoryType);
        add(accessory);
    }

    public boolean remove(AetherAccessory accessory) {
        if (accessory == null) return false;
        ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), accessory.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('aether_legacy:iron_pendant')"))
    public void removeByInput(ItemStack input) {
        this.getRegistry().getValuesCollection().forEach(accessory -> {
            if (accessory.getAccessoryStack().isItemEqual(input)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), accessory.getRegistryName());
            }
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        this.getRegistry().getValuesCollection().forEach(accessory -> {
            ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), accessory.getRegistryName());
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AetherAccessory> streamEntries() {
        return new SimpleObjectStream<>(this.getRegistry().getValuesCollection()).setRemover(this::remove);
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:shield')).accessoryType('shield')"))
    public RecipeBuilder recipeBuilder() {return new RecipeBuilder();}

    @Property(property = "input", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherAccessory> {

        @Property
        private AccessoryType accessoryType = AccessoryType.MISC;

        @RecipeBuilderMethodDescription
        public RecipeBuilder accessoryType(String type) {
            for (AccessoryType value : VALUES) {
                if (value.getDisplayName().equalsIgnoreCase(type)) {
                    accessoryType = value;
                }
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Aether Accessory";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(Aether.accessory.getRegistry().getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable AetherAccessory register() {
            if (!validate()) return null;

            AetherAccessory accessory = new AetherAccessory(input.get(0).getMatchingStacks()[0], accessoryType);
            Aether.accessory.add(accessory);
            return accessory;
        }
    }

}
