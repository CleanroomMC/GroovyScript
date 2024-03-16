package com.cleanroommc.groovyscript.compat.mods.extrautils2;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.extrautils2.MachineInitAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.rwtema.extrautils2.api.machine.IMachineRecipe;
import com.rwtema.extrautils2.api.machine.Machine;
import com.rwtema.extrautils2.api.machine.MachineRegistry;
import com.rwtema.extrautils2.api.machine.XUMachineGenerators;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class Generator extends VirtualizedRegistry<Pair<Machine, IMachineRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".generator('extrautils2:generator_pink').input(item('minecraft:clay')).energy(1000).energyPerTick(100)"),
            @Example(".generator('extrautils2:generator_slime').input(item('minecraft:clay') * 3).input(item('minecraft:gold_ingot')).energy(1000000).energyPerTick(100)"),
            @Example(".generator('extrautils2:generator_redstone').input(item('minecraft:clay') * 3).fluidInput(fluid('water') * 300).energy(1000).energyPerTick(100)"),
            @Example(".generator('extrautils2:generator_lava').fluidInput(fluid('water') * 300).energy(100).energyPerTick(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(x -> x.getKey().recipes_registry.removeRecipe(x.getValue()));
        restoreFromBackup().forEach(x -> x.getKey().recipes_registry.addRecipe(x.getValue()));
    }

    public IMachineRecipe add(Machine machine, IMachineRecipe recipe) {
        if (recipe != null) {
            machine.recipes_registry.addRecipe(recipe);
            addScripted(Pair.of(machine, recipe));
        }
        return recipe;
    }

    public IMachineRecipe add(String name, IMachineRecipe recipe) {
        Machine machine = MachineRegistry.getMachine(name);
        if (machine == null) {
            GroovyLog.get().error("machine cannot be null");
            return null;
        }
        return add(machine, recipe);
    }

    public IMachineRecipe add(ResourceLocation name, IMachineRecipe recipe) {
        return add(name.toString(), recipe);
    }

    public boolean remove(Machine machine, IMachineRecipe recipe) {
        if (machine.recipes_registry.removeRecipe(recipe)) {
            addBackup(Pair.of(machine, recipe));
            return true;
        }
        return false;
    }

    public boolean remove(String name, IMachineRecipe recipe) {
        Machine machine = MachineRegistry.getMachine(name);
        if (machine == null) {
            GroovyLog.get().error("machine cannot be null");
            return false;
        }
        return remove(machine, recipe);
    }

    public boolean remove(ResourceLocation name, IMachineRecipe recipe) {
        return remove(name.toString(), recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.remove0")
    public boolean remove(Machine machine, ItemStack input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : machine.recipes_registry) {
            if (recipe.getJEIInputItemExamples().stream().flatMap(x -> x.getKey().get(XUMachineGenerators.INPUT_ITEM).stream()).anyMatch(input::isItemEqual)) {
                agony.add(recipe);
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(Pair.of(machine, recipe));
            machine.recipes_registry.removeRecipe(recipe);
        }
        return !agony.isEmpty();
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.remove0", example = @Example("'extrautils2:generator_culinary', item('minecraft:apple')"))
    public boolean remove(String name, ItemStack input) {
        Machine machine = MachineRegistry.getMachine(name);
        if (machine == null) {
            GroovyLog.get().error("machine cannot be null");
            return false;
        }
        return remove(machine, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.remove0")
    public boolean remove(ResourceLocation name, ItemStack input) {
        return remove(name.toString(), input);
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.remove1")
    public boolean remove(Machine machine, FluidStack input) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : machine.recipes_registry) {
            if (recipe.getJEIInputItemExamples().stream().flatMap(x -> x.getValue().get(XUMachineGenerators.INPUT_FLUID).stream()).anyMatch(input::isFluidEqual)) {
                agony.add(recipe);
            }
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(Pair.of(machine, recipe));
            machine.recipes_registry.removeRecipe(recipe);
        }
        return !agony.isEmpty();
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.remove1", example = @Example("'extrautils2:generator_lava', fluid('lava')"))
    public boolean remove(String name, FluidStack input) {
        Machine machine = MachineRegistry.getMachine(name);
        if (machine == null) {
            GroovyLog.get().error("machine cannot be null");
            return false;
        }
        return remove(machine, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.remove1")
    public boolean remove(ResourceLocation name, FluidStack input) {
        return remove(name.toString(), input);
    }


    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Pair<Machine, IMachineRecipe>> streamRecipes() {
        List<Pair<Machine, IMachineRecipe>> list = new ArrayList<>();
        for (Generators name : Generators.values()) {
            Machine generator = MachineRegistry.getMachine(name.toString());
            if (generator == null) continue; // given that this is in an enum this should never happen. inb4 trollface
            for (IMachineRecipe recipe : generator.recipes_registry) {
                list.add(Pair.of(generator, recipe));
            }
        }
        return new SimpleObjectStream<>(list).setRemover(x -> x.getKey().recipes_registry.removeRecipe(x.getValue()));
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.removeByGenerator")
    public boolean removeByGenerator(Machine machine) {
        List<IMachineRecipe> agony = new ArrayList<>();
        for (IMachineRecipe recipe : machine.recipes_registry) {
            agony.add(recipe);
        }
        for (IMachineRecipe recipe : agony) {
            addBackup(Pair.of(machine, recipe));
            machine.recipes_registry.removeRecipe(recipe);
        }
        return !agony.isEmpty();
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.removeByGenerator", example = @Example("'extrautils2:generator_death'"))
    public boolean removeByGenerator(String name) {
        Machine machine = MachineRegistry.getMachine(name);
        if (machine == null) {
            GroovyLog.get().error("machine cannot be null");
            return false;
        }
        return removeByGenerator(machine);
    }

    @MethodDescription(description = "groovyscript.wiki.extrautils2.generator.removeByGenerator")
    public boolean removeByGenerator(ResourceLocation name) {
        return removeByGenerator(name.toString());
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        for (Generators name : Generators.values()) {
            Machine machine = MachineRegistry.getMachine(name.toString());
            if (machine == null) continue;
            removeByGenerator(machine);
        }
    }

    public enum Generators {

        FURNACE("extrautils2:generator"),
        SURVIVALIST("extrautils2:generator_survival"),
        CULINARY("extrautils2:generator_culinary"),
        POTION("extrautils2:generator_potion"),
        TNT("extrautils2:generator_tnt"),
        LAVA("extrautils2:generator_lava", 0, true),
        PINK("extrautils2:generator_pink"),
        NETHERSTAR("extrautils2:generator_netherstar"),
        ENDER("extrautils2:generator_ender"),
        REDSTONE("extrautils2:generator_redstone", 1, true),
        OVERCLOCK("extrautils2:generator_overclock"),
        DRAGON("extrautils2:generator_dragonsbreath"),
        ICE("extrautils2:generator_ice"),
        DEATH("extrautils2:generator_death"),
        ENCHANT("extrautils2:generator_enchant"),
        SLIME("extrautils2:generator_slime", 2);

        private final String location;
        private final int itemSlots;
        private final int hasFluid;

        Generators(String location) {
            this.location = location;
            this.itemSlots = 1;
            this.hasFluid = 0;
        }

        Generators(String location, int itemSlots) {
            this.location = location;
            this.itemSlots = itemSlots;
            this.hasFluid = 0;
        }

        Generators(String location, int itemSlots, boolean hasFluid) {
            this.location = location;
            this.itemSlots = itemSlots;
            this.hasFluid = hasFluid ? 1 : 0;
        }

        public static Generators byName(String name) {
            for (Generators generator : values()) {
                if (generator.toString().equals(name)) return generator;
            }
            return null;
        }

        public String toString() {
            return location;
        }

    }

    @Property(property = "input", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    @Property(property = "fluidInput", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<IMachineRecipe> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT))
        private Machine generator;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int energy;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GT))
        private int energyPerTick;

        @RecipeBuilderMethodDescription
        public RecipeBuilder generator(Machine generator) {
            this.generator = generator;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder generator(String name) {
            return generator(MachineRegistry.getMachine(name));
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder generator(ResourceLocation name) {
            return generator(name.toString());
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder energyPerTick(int energyPerTick) {
            this.energyPerTick = energyPerTick;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Extra Utilities 2 Generator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(generator == null, "generator must be defined");
            Generators generatorValues = Generators.byName(generator.name);
            if (generatorValues == null) {
                msg.add("could not find a generator with the name {}", generator.name);
                return;
            }
            validateItems(msg, generatorValues.itemSlots, generatorValues.itemSlots, 0, 0);
            validateFluids(msg, generatorValues.hasFluid, generatorValues.hasFluid, 0, 0);
            // If we have any current error messages, note that slot requirements vary based on the generator.
            msg.add(msg.hasSubMessages(), "different generators have different slot requirements");
            msg.add(energy <= 0, () -> "energy must not be less than or equal to 0");
            msg.add(energyPerTick <= 0, () -> "energyPerTick must not be less than or equal to 0");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public IMachineRecipe register() {
            if (!validate()) return null;

            com.rwtema.extrautils2.api.machine.RecipeBuilder builder = com.rwtema.extrautils2.api.machine.RecipeBuilder.newbuilder(generator);
            builder.setRFRate(energy, energyPerTick);
            if (!input.isEmpty()) {
                builder.setItemInput(XUMachineGenerators.INPUT_ITEM, Arrays.stream(input.get(0).getMatchingStacks()).collect(Collectors.toList()), input.get(0).getAmount());
                if (input.size() == 2) {
                    builder.setItemInput(MachineInitAccessor.getSLOT_SLIME_SECONDARY(), Arrays.stream(input.get(1).getMatchingStacks()).collect(Collectors.toList()), input.get(1).getAmount());
                }
            }

            if (!fluidInput.isEmpty()) {
                builder.setFluidInputFluidStack(XUMachineGenerators.INPUT_FLUID, fluidInput.get(0));
            }
            IMachineRecipe recipe = builder.build();
            ModSupport.EXTRA_UTILITIES_2.get().generator.add(generator, recipe);
            return recipe;
        }
    }

}
