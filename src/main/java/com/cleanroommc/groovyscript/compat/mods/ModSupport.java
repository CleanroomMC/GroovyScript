package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
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
import com.cleanroommc.groovyscript.compat.mods.inspirations.Inspirations;
import com.cleanroommc.groovyscript.compat.mods.integrateddynamics.IntegratedDynamics;
import com.cleanroommc.groovyscript.compat.mods.jei.JustEnoughItems;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.roots.Roots;
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

public class ModSupport implements IDynamicGroovyProperty {

    private static final Map<String, GroovyContainer<? extends ModPropertyContainer>> containers = new Object2ObjectOpenHashMap<>();
    private static final List<GroovyContainer<? extends ModPropertyContainer>> containerList = new ArrayList<>();
    private static final Set<Class<?>> externalPluginClasses = new ObjectOpenHashSet<>();
    private static boolean frozen = false;

    public static final ModSupport INSTANCE = new ModSupport(); // Just for Binding purposes

    public static final GroovyContainer<ActuallyAdditions> ACTUALLY_ADDITIONS = new InternalModContainer<>("actuallyadditions", "Actually Additions", ActuallyAdditions::new, "aa");
    public static final GroovyContainer<AdvancedMortars> ADVANCED_MORTARS = new InternalModContainer<>("advancedmortars", "Advanced Mortars", AdvancedMortars::new);
    public static final GroovyContainer<AppliedEnergistics2> APPLIED_ENERGISTICS_2 = new InternalModContainer<>("appliedenergistics2", "Applied Energistics 2", AppliedEnergistics2::new, "ae2");
    public static final GroovyContainer<AstralSorcery> ASTRAL_SORCERY = new InternalModContainer<>("astralsorcery", "Astral Sorcery", AstralSorcery::new, "astral", "astral_sorcery", "as");
    public static final GroovyContainer<Avaritia> AVARITIA = new InternalModContainer<>("avaritia", "Avaritia", Avaritia::new);
    public static final GroovyContainer<BloodMagic> BLOOD_MAGIC = new InternalModContainer<>("bloodmagic", "Blood Magic: Alchemical Wizardry", BloodMagic::new, "bm");
    public static final GroovyContainer<Botania> BOTANIA = new InternalModContainer<>("botania", "Botania", Botania::new);
    public static final GroovyContainer<Chisel> CHISEL = new InternalModContainer<>("chisel", "Chisel", Chisel::new);
    public static final GroovyContainer<CompactMachines> COMPACT_MACHINES = new InternalModContainer<>("compactmachines3", "Compact Machines 3", CompactMachines::new, "compactmachines");
    public static final GroovyContainer<DraconicEvolution> DRACONIC_EVOLUTION = new InternalModContainer<>("draconicevolution", "Draconic Evolution", DraconicEvolution::new, "de");
    public static final GroovyContainer<EnderIO> ENDER_IO = new InternalModContainer<>("enderio", "Ender IO", EnderIO::new, "eio");
    public static final GroovyContainer<EvilCraft> EVILCRAFT = new InternalModContainer<>("evilcraft", "EvilCraft", EvilCraft::new);
    public static final GroovyContainer<ExtendedCrafting> EXTENDED_CRAFTING = new InternalModContainer<>("extendedcrafting", "Extended Crafting", ExtendedCrafting::new);
    public static final GroovyContainer<Forestry> FORESTRY = new InternalModContainer<>("forestry", "Forestry", Forestry::new);
    public static final GroovyContainer<ImmersiveEngineering> IMMERSIVE_ENGINEERING = new InternalModContainer<>("immersiveengineering", "Immersive Engineering", ImmersiveEngineering::new, "ie");
    public static final GroovyContainer<IC2> INDUSTRIALCRAFT = new InternalModContainer<>("ic2", "Industrial Craft 2", IC2::new, "industrialcraft");
    public static final GroovyContainer<Inspirations> INSPIRATIONS = new InternalModContainer<>("inspirations", "Inspirations", Inspirations::new);
    public static final GroovyContainer<IntegratedDynamics> INTEGRATED_DYNAMICS = new InternalModContainer<>("integrateddynamics", "Integrated Dynamics", IntegratedDynamics::new, "id");
    public static final GroovyContainer<JustEnoughItems> JEI = new InternalModContainer<>("jei", "Just Enough Items", JustEnoughItems::new, "hei");
    public static final GroovyContainer<Mekanism> MEKANISM = new InternalModContainer<>("mekanism", "Mekanism", Mekanism::new);
    public static final GroovyContainer<Roots> ROOTS = new InternalModContainer<>("roots", "Roots 3", Roots::new);
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
        if (!Loader.isModLoaded(container.getModId())) return;
        ModPropertyContainer modPropertyContainer = container.createModPropertyContainer();
        if (modPropertyContainer == null) {
            modPropertyContainer = new ModPropertyContainer();
        }
        new ExternalModContainer(container, modPropertyContainer);
        externalPluginClasses.add(container.getClass());
    }

    void registerContainer(GroovyContainer<?> container) {
        if (containerList.contains(container) || containers.containsKey(container.getModId())) {
            throw new IllegalStateException("Container already present!");
        }
        containerList.add(container);
        containers.put(container.getModId(), container);
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
