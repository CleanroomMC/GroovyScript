package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.compat.mods.actuallyadditions.ActuallyAdditions;
import com.cleanroommc.groovyscript.compat.mods.advancedmortars.AdvancedMortars;
import com.cleanroommc.groovyscript.compat.mods.appliedenergistics2.AppliedEnergistics2;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.AstralSorcery;
import com.cleanroommc.groovyscript.compat.mods.avaritia.Avaritia;
import com.cleanroommc.groovyscript.compat.mods.bloodmagic.BloodMagic;
import com.cleanroommc.groovyscript.compat.mods.botania.Botania;
import com.cleanroommc.groovyscript.compat.mods.chisel.Chisel;
import com.cleanroommc.groovyscript.compat.mods.compactmachines.CompactMachines;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.DraconicEvolution;
import com.cleanroommc.groovyscript.compat.mods.enderio.EnderIO;
import com.cleanroommc.groovyscript.compat.mods.evilcraft.EvilCraft;
import com.cleanroommc.groovyscript.compat.mods.extendedcrafting.ExtendedCrafting;
import com.cleanroommc.groovyscript.compat.mods.forestry.Forestry;
import com.cleanroommc.groovyscript.compat.mods.ic2.IC2;
import com.cleanroommc.groovyscript.compat.mods.immersiveengineering.ImmersiveEngineering;
import com.cleanroommc.groovyscript.compat.mods.integrateddynamics.IntegratedDynamics;
import com.cleanroommc.groovyscript.compat.mods.inspirations.Inspirations;
import com.cleanroommc.groovyscript.compat.mods.jei.JustEnoughItems;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.roots.Roots;
import com.cleanroommc.groovyscript.compat.mods.tcomplement.TinkersComplement;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.ThermalExpansion;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.TinkersConstruct;
import com.cleanroommc.groovyscript.compat.mods.woot.Woot;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class ModSupport implements IDynamicGroovyProperty {

    private static final Map<String, Container<? extends ModPropertyContainer>> containers = new Object2ObjectOpenHashMap<>();
    private static boolean frozen = false;

    public static final ModSupport INSTANCE = new ModSupport(); // Just for Binding purposes

    public static final Container<ActuallyAdditions> ACTUALLY_ADDITIONS = new Container<>("actuallyadditions", "Actually Additions", ActuallyAdditions::new, "aa");
    public static final Container<AdvancedMortars> ADVANCED_MORTARS = new Container<>("advancedmortars", "Advanced Mortars", AdvancedMortars::new);
    public static final Container<AppliedEnergistics2> APPLIED_ENERGISTICS_2 = new Container<>("appliedenergistics2", "Applied Energistics 2", AppliedEnergistics2::new, "ae2");
    public static final Container<AstralSorcery> ASTRAL_SORCERY = new Container<>("astralsorcery", "Astral Sorcery", AstralSorcery::new, "astral", "astral_sorcery", "as");
    public static final Container<Avaritia> AVARITIA = new Container<>("avaritia", "Avaritia", Avaritia::new);
    public static final Container<BloodMagic> BLOOD_MAGIC = new Container<>("bloodmagic", "Blood Magic: Alchemical Wizardry", BloodMagic::new, "bm");
    public static final Container<Botania> BOTANIA = new Container<>("botania", "Botania", Botania::new);
    public static final Container<Chisel> CHISEL = new Container<>("chisel", "Chisel", Chisel::new);
    public static final Container<CompactMachines> COMPACT_MACHINES = new Container<>("compactmachines3", "Compact Machines 3", CompactMachines::new, "compactmachines");
    public static final Container<DraconicEvolution> DRACONIC_EVOLUTION = new Container<>("draconicevolution", "Draconic Evolution", DraconicEvolution::new, "de");
    public static final Container<EnderIO> ENDER_IO = new Container<>("enderio", "Ender IO", EnderIO::new, "eio");
    public static final Container<EvilCraft> EVILCRAFT = new Container<>("evilcraft", "EvilCraft", EvilCraft::new);
    public static final Container<ExtendedCrafting> EXTENDED_CRAFTING = new Container<>("extendedcrafting", "Extended Crafting", ExtendedCrafting::new);
    public static final Container<Forestry> FORESTRY = new Container<>("forestry", "Forestry", Forestry::new);
    public static final Container<ImmersiveEngineering> IMMERSIVE_ENGINEERING = new Container<>("immersiveengineering", "Immersive Engineering", ImmersiveEngineering::new, "ie");
    public static final Container<IC2> INDUSTRIALCRAFT = new Container<>("ic2", "Industrial Craft 2", IC2::new, "industrialcraft");
    public static final Container<Inspirations> INSPIRATIONS = new Container<>("inspirations", "Inspirations", Inspirations::new);
    public static final Container<IntegratedDynamics> INTEGRATED_DYNAMICS = new Container<>("integrateddynamics", "Integrated Dynamics", IntegratedDynamics::new, "id");
    public static final Container<JustEnoughItems> JEI = new Container<>("jei", "Just Enough Items", JustEnoughItems::new, "hei");
    public static final Container<Mekanism> MEKANISM = new Container<>("mekanism", "Mekanism", Mekanism::new);
    public static final Container<Roots> ROOTS = new Container<>("roots", "Roots 3", Roots::new);
    public static final Container<Thaumcraft> THAUMCRAFT = new Container<>("thaumcraft", "Thaumcraft", Thaumcraft::new, "tc", "thaum");
    public static final Container<ThermalExpansion> THERMAL_EXPANSION = new Container<>("thermalexpansion", "Thermal Expansion", ThermalExpansion::new, "te", "thermal");
    public static final Container<TinkersComplement> TINKERS_COMPLEMENT = new Container<>("tcomplement", "Tinkers Complement", TinkersComplement::new, "tcomp", "tinkerscomplement");
    public static final Container<TinkersConstruct> TINKERS_CONSTRUCT = new Container<>("tconstruct", "Tinkers' Construct", TinkersConstruct::new, "ticon", "tinkersconstruct");
    public static final Container<Woot> WOOT = new Container<>("woot", "Woot", Woot::new);

    public static Collection<Container<? extends ModPropertyContainer>> getAllContainers() {
        return new ObjectOpenHashSet<>(containers.values());
    }

    private ModSupport() {
    }

    @Override
    @Nullable
    public Object getProperty(String name) {
        Container<?> container = containers.get(name);
        if (container != null) {
            return container.modProperty.get();
        }
        return null;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void init() {
        frozen = true;
        for (Container<?> container : getAllContainers()) {
            if (container.isLoaded()) {
                container.get().initialize();
            }
        }
    }

    @SuppressWarnings("all")
    public static class Container<T extends ModPropertyContainer> {

        private final String modId, modName;
        private final Supplier<T> modProperty;
        private final boolean loaded;

        public Container(String modId, String modName, @NotNull Supplier<T> modProperty) {
            this(modId, modName, modProperty, new String[0]);
        }

        public Container(String modId, String modName, @NotNull Supplier<T> modProperty, String... aliases) {
            if (frozen) {
                throw new RuntimeException("Groovy mod containers must be registered at construction event! Tried to register '" + modName + "' too late.");
            }
            this.modId = modId;
            this.modName = modName;
            this.modProperty = Suppliers.memoize(modProperty);
            this.loaded = Loader.isModLoaded(modId);
            containers.put(modId, this);
            for (String alias : aliases) {
                Container<?> container = containers.put(alias, this);
                if (container != null) {
                    throw new IllegalArgumentException("Alias already exists for: " + container.modId + " mod.");
                }
            }
        }

        public boolean isLoaded() {
            return loaded;
        }

        public String getId() {
            return modId;
        }

        public T get() {
            return modProperty == null ? null : isLoaded() ? modProperty.get() : null;
        }

        @Override
        public String toString() {
            return modName;
        }

    }

}
