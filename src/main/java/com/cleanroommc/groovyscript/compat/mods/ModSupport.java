package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.compat.mods.actuallyadditions.ActuallyAdditions;
import com.cleanroommc.groovyscript.compat.mods.advancedmortars.AdvancedMortars;
import com.cleanroommc.groovyscript.compat.mods.aetherlegacy.Aether;
import com.cleanroommc.groovyscript.compat.mods.alchemistry.Alchemistry;
import com.cleanroommc.groovyscript.compat.mods.appliedenergistics2.AppliedEnergistics2;
import com.cleanroommc.groovyscript.compat.mods.arcanearchives.ArcaneArchives;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.AstralSorcery;
import com.cleanroommc.groovyscript.compat.mods.avaritia.Avaritia;
import com.cleanroommc.groovyscript.compat.mods.betterwithmods.BetterWithMods;
import com.cleanroommc.groovyscript.compat.mods.bloodmagic.BloodMagic;
import com.cleanroommc.groovyscript.compat.mods.botania.Botania;
import com.cleanroommc.groovyscript.compat.mods.calculator.Calculator;
import com.cleanroommc.groovyscript.compat.mods.chisel.Chisel;
import com.cleanroommc.groovyscript.compat.mods.compactmachines.CompactMachines;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.DraconicEvolution;
import com.cleanroommc.groovyscript.compat.mods.enderio.EnderIO;
import com.cleanroommc.groovyscript.compat.mods.evilcraft.EvilCraft;
import com.cleanroommc.groovyscript.compat.mods.extendedcrafting.ExtendedCrafting;
import com.cleanroommc.groovyscript.compat.mods.extrautils2.ExtraUtils2;
import com.cleanroommc.groovyscript.compat.mods.forestry.Forestry;
import com.cleanroommc.groovyscript.compat.mods.ic2.IC2;
import com.cleanroommc.groovyscript.compat.mods.immersiveengineering.ImmersiveEngineering;
import com.cleanroommc.groovyscript.compat.mods.inspirations.Inspirations;
import com.cleanroommc.groovyscript.compat.mods.integrateddynamics.IntegratedDynamics;
import com.cleanroommc.groovyscript.compat.mods.jei.JustEnoughItems;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.naturesaura.NaturesAura;
import com.cleanroommc.groovyscript.compat.mods.pyrotech.PyroTech;
import com.cleanroommc.groovyscript.compat.mods.roots.Roots;
import com.cleanroommc.groovyscript.compat.mods.rustic.Rustic;
import com.cleanroommc.groovyscript.compat.mods.tcomplement.TinkersComplement;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.ThermalExpansion;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.TinkersConstruct;
import com.cleanroommc.groovyscript.compat.mods.woot.Woot;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ModSupport implements IDynamicGroovyProperty {

    private static final Map<String, GroovyContainer<? extends ModPropertyContainer>> containers = new Object2ObjectOpenHashMap<>();
    private static final List<GroovyContainer<? extends ModPropertyContainer>> containerList = new ArrayList<>();
    private static final Set<Class<?>> externalPluginClasses = new ObjectOpenHashSet<>();
    private static boolean frozen = false;

    public static final ModSupport INSTANCE = new ModSupport(); // Just for Binding purposes

    public static final GroovyContainer<ActuallyAdditions> ACTUALLY_ADDITIONS = new InternalModContainer<>("actuallyadditions", "Actually Additions", ActuallyAdditions::new, "aa");
    public static final GroovyContainer<AdvancedMortars> ADVANCED_MORTARS = new InternalModContainer<>("advancedmortars", "Advanced Mortars", AdvancedMortars::new);
    public static final GroovyContainer<Aether> AETHER = new InternalModContainer<>("aether_legacy", "Aether Legacy", Aether::new, "aether");
    public static final GroovyContainer<Alchemistry> ALCHEMISTRY = new InternalModContainer<>("alchemistry", "Alchemistry", Alchemistry::new);
    public static final GroovyContainer<AppliedEnergistics2> APPLIED_ENERGISTICS_2 = new InternalModContainer<>("appliedenergistics2", "Applied Energistics 2", AppliedEnergistics2::new, "ae2");
    public static final GroovyContainer<ArcaneArchives> ARCANE_ARCHIVES = new InternalModContainer<>("arcanearchives", "Arcane Archives", ArcaneArchives::new);
    public static final GroovyContainer<AstralSorcery> ASTRAL_SORCERY = new InternalModContainer<>("astralsorcery", "Astral Sorcery", AstralSorcery::new, "astral");
    public static final GroovyContainer<Avaritia> AVARITIA = new InternalModContainer<>("avaritia", "Avaritia", Avaritia::new);
    public static final GroovyContainer<BetterWithMods> BETTER_WITH_MODS = new InternalModContainer<>("betterwithmods", "Better With Mods", BetterWithMods::new);
    public static final GroovyContainer<BloodMagic> BLOOD_MAGIC = new InternalModContainer<>("bloodmagic", "Blood Magic: Alchemical Wizardry", BloodMagic::new, "bm");
    public static final GroovyContainer<Botania> BOTANIA = new InternalModContainer<>("botania", "Botania", Botania::new);
    public static final GroovyContainer<Calculator> CALCULATOR = new InternalModContainer<>("calculator", "Calculator", Calculator::new);
    public static final GroovyContainer<Chisel> CHISEL = new InternalModContainer<>("chisel", "Chisel", Chisel::new);
    public static final GroovyContainer<CompactMachines> COMPACT_MACHINES = new InternalModContainer<>("compactmachines3", "Compact Machines 3", CompactMachines::new, "compactmachines");
    public static final GroovyContainer<DraconicEvolution> DRACONIC_EVOLUTION = new InternalModContainer<>("draconicevolution", "Draconic Evolution", DraconicEvolution::new, "de");
    public static final GroovyContainer<EnderIO> ENDER_IO = new InternalModContainer<>("enderio", "Ender IO", EnderIO::new, "eio");
    public static final GroovyContainer<EvilCraft> EVILCRAFT = new InternalModContainer<>("evilcraft", "EvilCraft", EvilCraft::new);
    public static final GroovyContainer<ExtendedCrafting> EXTENDED_CRAFTING = new InternalModContainer<>("extendedcrafting", "Extended Crafting", ExtendedCrafting::new);
    public static final GroovyContainer<ExtraUtils2> EXTRA_UTILITIES_2 = new InternalModContainer<>("extrautils2", "Extra Utilities 2", ExtraUtils2::new, "extrautilities2");
    public static final GroovyContainer<Forestry> FORESTRY = new InternalModContainer<>("forestry", "Forestry", Forestry::new);
    public static final GroovyContainer<ImmersiveEngineering> IMMERSIVE_ENGINEERING = new InternalModContainer<>("immersiveengineering", "Immersive Engineering", ImmersiveEngineering::new, "ie");
    public static final GroovyContainer<IC2> INDUSTRIALCRAFT = new InternalModContainer<>("ic2", "Industrial Craft 2", IC2::new, "industrialcraft");
    public static final GroovyContainer<Inspirations> INSPIRATIONS = new InternalModContainer<>("inspirations", "Inspirations", Inspirations::new);
    public static final GroovyContainer<IntegratedDynamics> INTEGRATED_DYNAMICS = new InternalModContainer<>("integrateddynamics", "Integrated Dynamics", IntegratedDynamics::new, "id");
    public static final GroovyContainer<JustEnoughItems> JEI = new InternalModContainer<>("jei", "Just Enough Items", JustEnoughItems::new, "hei");
    public static final GroovyContainer<Mekanism> MEKANISM = new InternalModContainer<>("mekanism", "Mekanism", Mekanism::new);
    public static final GroovyContainer<NaturesAura> NATURES_AURA = new InternalModContainer<>("naturesaura", "Nature's Aura", NaturesAura::new);
    public static final GroovyContainer<PyroTech> PYROTECH = new InternalModContainer<>("pyrotech", "Pyrotech", PyroTech::new);
    public static final GroovyContainer<Roots> ROOTS = new InternalModContainer<>("roots", "Roots 3", Roots::new);
    public static final GroovyContainer<Rustic> RUSTIC = new InternalModContainer<>("rustic", "Rustic", Rustic::new);
    public static final GroovyContainer<Thaumcraft> THAUMCRAFT = new InternalModContainer<>("thaumcraft", "Thaumcraft", Thaumcraft::new, "tc", "thaum");
    public static final GroovyContainer<ThermalExpansion> THERMAL_EXPANSION = new InternalModContainer<>("thermalexpansion", "Thermal Expansion", ThermalExpansion::new, "te", "thermal");
    public static final GroovyContainer<TinkersComplement> TINKERS_COMPLEMENT = new InternalModContainer<>("tcomplement", "Tinkers Complement", TinkersComplement::new, "tcomp", "tinkerscomplement");
    public static final GroovyContainer<TinkersConstruct> TINKERS_CONSTRUCT = new InternalModContainer<>("tconstruct", "Tinkers' Construct", TinkersConstruct::new, "ticon", "tinkersconstruct");
    public static final GroovyContainer<Woot> WOOT = new InternalModContainer<>("woot", "Woot", Woot::new);
    public static Collection<GroovyContainer<? extends ModPropertyContainer>> getAllContainers() {
        return Collections.unmodifiableList(containerList);
    }

    private ModSupport() {
    }

    @ApiStatus.Internal
    public void setup(ASMDataTable dataTable) {
        for (ASMDataTable.ASMData data : dataTable.getAll(GroovyPlugin.class.getName().replace('.', '/'))) {
            try {
                Class<?> clazz = Class.forName(data.getClassName().replace('/', '.'));
                if (!externalPluginClasses.contains(clazz)) {
                    registerContainer((GroovyPlugin) clazz.newInstance());
                }
            } catch (ClassNotFoundException | InstantiationException e) {
                GroovyScript.LOGGER.error("Could not initialize Groovy Plugin '{}'", data.getClassName());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void registerContainer(GroovyPlugin container) {
        if (container instanceof GroovyContainer) {
            GroovyScript.LOGGER.error("GroovyPlugin must not extend {}", GroovyContainer.class.getSimpleName());
            return;
        }
        if (ModSupport.isFrozen()) {
            throw new RuntimeException("Groovy mod containers must be registered at construction event! Tried to register '" + container.getContainerName() + "' too late.");
        }
        if (!Loader.isModLoaded(container.getModId())) return;
        if (hasCompatFor(container.getModId())) {
            GroovyContainer<?> current = getContainer(container.getModId());
            if (current.getOverridePriority().ordinal() >= container.getOverridePriority().ordinal()) {
                // the existing container has a higher priority, keep it
                GroovyScript.LOGGER.info("Overriding GroovyScript compat plugin '{}' by plugin '{}'", container.getContainerName(), current.getContainerName());
                return;
            }
            // the existing container has a lower priority, yeet it
            GroovyScript.LOGGER.info("Overriding GroovyScript compat plugin '{}' by plugin '{}'", current.getContainerName(), container.getContainerName());
            containers.values().removeIf(c -> c == current);
            containerList.removeIf(c -> c == current);
        }

        ModPropertyContainer modPropertyContainer = container.createModPropertyContainer();
        if (modPropertyContainer == null) {
            modPropertyContainer = new ModPropertyContainer();
        }
        registerContainer(new ExternalModContainer(container, modPropertyContainer));
        externalPluginClasses.add(container.getClass());
    }

    void registerContainer(GroovyContainer<?> container) {
        if (containerList.contains(container) || containers.containsKey(container.getModId())) {
            throw new IllegalStateException("Container already present!");
        }
        containerList.add(container);
        for (String alias : container.getAliases()) {
            GroovyContainer<?> container2 = containers.put(alias, container);
            if (container2 != null) {
                throw new IllegalArgumentException("Alias already exists for: " + container.getModId() + " mod.");
            }
        }
    }

    @Override
    @Nullable
    public Object getProperty(String name) {
        GroovyContainer<?> container = containers.get(name);
        return container != null ? container.get() : null;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        for (var entry : containers.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().get());
        }
        return properties;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void init() {
        frozen = true;
        for (GroovyContainer<?> container : containerList) {
            if (container.isLoaded()) {
                container.onCompatLoaded(container);
                container.get().initialize();
            }
        }
    }

    @NotNull
    public GroovyContainer<?> getContainer(String mod) {
        if (!containers.containsKey(mod)) {
            throw new IllegalStateException("There is no compat registered for '" + mod + "'!");
        }
        return containers.get(mod);
    }

    public boolean hasCompatFor(String mod) {
        return containers.containsKey(mod);
    }

    public static boolean isFrozen() {
        return frozen;
    }
}
