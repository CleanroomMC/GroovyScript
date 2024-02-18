package com.cleanroommc.groovyscript.compat.mods.aether_legacy;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.gildedgames.the_aether.api.freezables.AetherFreezable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Freezer extends ForgeRegistryWrapper<AetherFreezable> {

    public Freezer() {
        super(GameRegistry.findRegistry(AetherFreezable.class), Alias.generateOf("freezer"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:dirt')).time(200)"))
    public RecipeBuilder recipeBuilder() {return new RecipeBuilder();}

    public void add(AetherFreezable freezable) {
        if (freezable != null) {
            ReloadableRegistryManager.addRegistryEntry(this.getRegistry(), freezable);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(ItemStack input, ItemStack output, int timeRequired) {
        AetherFreezable freezable = new AetherFreezable(input, output, timeRequired);
        add(freezable);
    }

    public boolean remove(AetherFreezable freezable) {
        if (freezable == null) return false;
        ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezable.getRegistryName());
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:obsidian')"))
    public void removeByOutput(ItemStack output) {
        this.getRegistry().getValuesCollection().forEach(freezable -> {
            if (freezable.getOutput().isItemEqual(output)) {
                ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezable.getRegistryName());
            }
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        this.getRegistry().getValuesCollection().forEach(freezable -> {
            ReloadableRegistryManager.removeRegistryEntry(this.getRegistry(), freezable.getRegistryName());
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AetherFreezable> streamRecipes() {
        return new SimpleObjectStream<>(this.getRegistry().getValuesCollection()).setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AetherFreezable> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "0"))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Aether Freezer Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(time < 0, "time must be a non-negative integer, yet it was {}", time);
            msg.add(Aether.freezer.getRegistry().getValue(name) != null, "tried to register {}, but it already exists.", name);
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable AetherFreezable register() {
            if (!validate()) return null;
            AetherFreezable freezable = new AetherFreezable(input.get(0).getMatchingStacks()[0], output.get(0), time);
            Aether.freezer.add(freezable);
            return freezable;
        }
    }

}
