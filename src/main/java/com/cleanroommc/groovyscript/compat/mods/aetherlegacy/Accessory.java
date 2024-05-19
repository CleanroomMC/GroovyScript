package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.EnumHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.api.accessories.AetherAccessory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@RegistryDescription
public class Accessory extends ForgeRegistryWrapper<AetherAccessory> {

    public Accessory() {
        super(GameRegistry.findRegistry(AetherAccessory.class));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(ItemStack item, String type) {
        AccessoryType accessoryType = EnumHelper.valueOfNullable(AccessoryType.class, type, false);
        if (accessoryType == null) {
            GroovyLog.msg("Error adding Aether accessory")
                    .add(accessoryType == null, "type with name {} does not exist. Valid values are {}.", type, Arrays.toString(AccessoryType.values()))
                    .error()
                    .post();
            return;
        }
        AetherAccessory accessory = new AetherAccessory(item, accessoryType);
        add(accessory);
    }

    @MethodDescription(example = @Example("item('aether_legacy:iron_pendant')"))
    public void removeByInput(IIngredient input) {
        this.getRegistry().getValuesCollection().forEach(accessory -> {
            if (input.test(accessory.getAccessoryStack())) {
                remove(accessory);
            }
        });
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:shield')).accessoryType('shield')"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Property(property = "input", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherAccessory> {

        @Property
        private AccessoryType accessoryType;

        @RecipeBuilderMethodDescription
        public RecipeBuilder accessoryType(String type) {
            accessoryType = EnumHelper.valueOfNullable(AccessoryType.class, type, false);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder accessoryType(AccessoryType type) {
            accessoryType = type;
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
            msg.add(accessoryType == null, "type with name {} does not exist. Valid values are {}.", accessoryType.toString(), Arrays.toString(AccessoryType.values()));
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
