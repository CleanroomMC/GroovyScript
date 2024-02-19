package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.api.machine.Machine;
import com.rwtema.extrautils2.api.machine.XUMachineCrusher;

import java.util.ArrayList;
import java.util.List;

import static com.rwtema.extrautils2.api.machine.XUMachineGenerators.*;

public class Generator extends VirtualizedRegistry<IMachineRecipe> {

    public static Generator furnace = new Generator(FURNACE_GENERATOR);
    public static Generator survivalist = new Generator(SURVIVALIST_GENERATOR);
    public static Generator culinary = new Generator(CULINARY_GENERATOR);
    public static Generator potion = new Generator(POTION_GENERATOR);
    public static Generator tnt = new Generator(TNT_GENERATOR);
    public static Generator lava = new Generator(LAVA_GENERATOR);
    public static Generator pink = new Generator(PINK_GENERATOR);
    public static Generator netherstar = new Generator(NETHERSTAR_GENERATOR);
    public static Generator ender = new Generator(ENDER_GENERATOR);
    public static Generator redstone = new Generator(REDSTONE_GENERATOR);
    public static Generator overclock = new Generator(OVERCLOCK_GENERATOR);
    public static Generator dragon = new Generator(DRAGON_GENERATOR);
    public static Generator ice = new Generator(ICE_GENERATOR);
    public static Generator death = new Generator(DEATH_GENERATOR);
    public static Generator enchant = new Generator(ENCHANT_GENERATOR);
    public static Generator slime = new Generator(SLIME_GENERATOR);

    final Machine generator;

    public Generator(Machine generator) {
        super(Alias.generateOf(generator.name));
        this.generator = generator;
    }

    @Override
    public void onReload() {
        removeScripted().forEach(generator.recipes_registry::removeRecipe);
        restoreFromBackup().forEach(generator.recipes_registry::addRecipe);
    }

    public IMachineRecipe add(IMachineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            generator.recipes_registry.addRecipe(recipe);
        }
        return recipe;
    }

    public boolean remove(IMachineRecipe recipe) {
        if (generator.recipes_registry.removeRecipe(recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<IMachineRecipe> streamRecipes() {
        List<IMachineRecipe> list = new ArrayList<>();
        for (IMachineRecipe recipe : XUMachineCrusher.INSTANCE.recipes_registry) {
            list.add(recipe);
        }
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    public void removeAll() {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : generator.recipes_registry) {
            agony.add(recipe);
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(recipe);
            generator.recipes_registry.removeRecipe(recipe);
        }
    }
}
