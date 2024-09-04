package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.actuallyadditions.ActuallyAdditions;
import com.cleanroommc.groovyscript.compat.mods.advancedmortars.AdvancedMortars;
import com.cleanroommc.groovyscript.compat.mods.advancedrocketry.AdvancedRocketry;
import com.cleanroommc.groovyscript.compat.mods.aetherlegacy.Aether;
import com.cleanroommc.groovyscript.compat.mods.alchemistry.Alchemistry;
import com.cleanroommc.groovyscript.compat.mods.appliedenergistics2.AppliedEnergistics2;
import com.cleanroommc.groovyscript.compat.mods.arcanearchives.ArcaneArchives;
import com.cleanroommc.groovyscript.compat.mods.artisanworktables.ArtisanWorktables;
import com.cleanroommc.groovyscript.compat.mods.astralsorcery.AstralSorcery;
import com.cleanroommc.groovyscript.compat.mods.atum.Atum;
import com.cleanroommc.groovyscript.compat.mods.theaurorian.TheAurorian;
import com.cleanroommc.groovyscript.compat.mods.avaritia.Avaritia;
import com.cleanroommc.groovyscript.compat.mods.betterwithmods.BetterWithMods;
import com.cleanroommc.groovyscript.compat.mods.bloodmagic.BloodMagic;
import com.cleanroommc.groovyscript.compat.mods.botania.Botania;
import com.cleanroommc.groovyscript.compat.mods.botanicadditions.BotanicAdditions;
import com.cleanroommc.groovyscript.compat.mods.calculator.Calculator;
import com.cleanroommc.groovyscript.compat.mods.chisel.Chisel;
import com.cleanroommc.groovyscript.compat.mods.cyclic.Cyclic;
import com.cleanroommc.groovyscript.compat.mods.compactmachines.CompactMachines;
import com.cleanroommc.groovyscript.compat.mods.draconicevolution.DraconicEvolution;
import com.cleanroommc.groovyscript.compat.mods.enderio.EnderIO;
import com.cleanroommc.groovyscript.compat.mods.essentialcraft.EssentialCraft;
import com.cleanroommc.groovyscript.compat.mods.evilcraft.EvilCraft;
import com.cleanroommc.groovyscript.compat.mods.extendedcrafting.ExtendedCrafting;
import com.cleanroommc.groovyscript.compat.mods.extrabotany.ExtraBotany;
import com.cleanroommc.groovyscript.compat.mods.extrautils2.ExtraUtils2;
import com.cleanroommc.groovyscript.compat.mods.forestry.Forestry;
import com.cleanroommc.groovyscript.compat.mods.ic2.IC2;
import com.cleanroommc.groovyscript.compat.mods.immersiveengineering.ImmersiveEngineering;
import com.cleanroommc.groovyscript.compat.mods.immersivepetroleum.ImmersivePetroleum;
import com.cleanroommc.groovyscript.compat.mods.immersivetechnology.ImmersiveTechnology;
import com.cleanroommc.groovyscript.compat.mods.industrialforegoing.IndustrialForegoing;
import com.cleanroommc.groovyscript.compat.mods.inspirations.Inspirations;
import com.cleanroommc.groovyscript.compat.mods.integrateddynamics.IntegratedDynamics;
import com.cleanroommc.groovyscript.compat.mods.jei.JustEnoughItems;
import com.cleanroommc.groovyscript.compat.mods.lazyae2.LazyAE2;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.mysticalagriculture.MysticalAgriculture;
import com.cleanroommc.groovyscript.compat.mods.naturesaura.NaturesAura;
import com.cleanroommc.groovyscript.compat.mods.pneumaticcraft.PneumaticCraft;
import com.cleanroommc.groovyscript.compat.mods.primaltech.PrimalTech;
import com.cleanroommc.groovyscript.compat.mods.prodigytech.ProdigyTech;
import com.cleanroommc.groovyscript.compat.mods.projecte.ProjectE;
import com.cleanroommc.groovyscript.compat.mods.pyrotech.PyroTech;
import com.cleanroommc.groovyscript.compat.mods.roots.Roots;
import com.cleanroommc.groovyscript.compat.mods.rustic.Rustic;
import com.cleanroommc.groovyscript.compat.mods.tcomplement.TinkersComplement;
import com.cleanroommc.groovyscript.compat.mods.techreborn.TechReborn;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.Thaumcraft;
import com.cleanroommc.groovyscript.compat.mods.thermalexpansion.ThermalExpansion;
import com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.TinkersConstruct;
import com.cleanroommc.groovyscript.compat.mods.woot.Woot;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class ModSupport {

    private static final Map<String, GroovyContainer<? extends GroovyPropertyContainer>> containers = new Object2ObjectOpenHashMap<>();
    private static final Map<String, GroovyContainer<? extends GroovyPropertyContainer>> containersView = Collections.unmodifiableMap(containers);
    private static final List<GroovyContainer<? extends GroovyPropertyContainer>> containerList = new ArrayList<>();
    private static final Set<Class<?>> externalPluginClasses = new ObjectOpenHashSet<>();
    private static boolean frozen;

    public static final ModSupport INSTANCE = new ModSupport(); // Just for Binding purposes

    public static final GroovyContainer<ActuallyAdditions> ACTUALLY_ADDITIONS = new InternalModContainer<>("actuallyadditions", "Actually Additions", ActuallyAdditions::new, "aa");
    public static final GroovyContainer<AdvancedMortars> ADVANCED_MORTARS = new InternalModContainer<>("advancedmortars", "Advanced Mortars", AdvancedMortars::new);
    public static final GroovyContainer<AdvancedRocketry> ADVANCED_ROCKETRY = new InternalModContainer<>("advancedrocketry", "Advanced Rocketry", AdvancedRocketry::new);
    public static final GroovyContainer<Aether> AETHER = new InternalModContainer<>("aether_legacy", "Aether Legacy", Aether::new, "aether");
    public static final GroovyContainer<Alchemistry> ALCHEMISTRY = new InternalModContainer<>("alchemistry", "Alchemistry", Alchemistry::new);
    public static final GroovyContainer<AppliedEnergistics2> APPLIED_ENERGISTICS_2 = new InternalModContainer<>("appliedenergistics2", "Applied Energistics 2", AppliedEnergistics2::new, "ae2");
    public static final GroovyContainer<ArcaneArchives> ARCANE_ARCHIVES = new InternalModContainer<>("arcanearchives", "Arcane Archives", ArcaneArchives::new);
    public static final GroovyContainer<ArtisanWorktables> ARTISAN_WORKTABLES = new InternalModContainer<>("artisanworktables", "Artisan Worktables", ArtisanWorktables::new);
    public static final GroovyContainer<AstralSorcery> ASTRAL_SORCERY = new InternalModContainer<>("astralsorcery", "Astral Sorcery", AstralSorcery::new, "astral");
    public static final GroovyContainer<Atum> ATUM = new InternalModContainer<>("atum", "Atum 2", Atum::new);
    public static final GroovyContainer<Avaritia> AVARITIA = new InternalModContainer<>("avaritia", "Avaritia", Avaritia::new);
    public static final GroovyContainer<BetterWithMods> BETTER_WITH_MODS = new InternalModContainer<>("betterwithmods", "Better With Mods", BetterWithMods::new);
    public static final GroovyContainer<BloodMagic> BLOOD_MAGIC = new InternalModContainer<>("bloodmagic", "Blood Magic: Alchemical Wizardry", BloodMagic::new, "bm");
    public static final GroovyContainer<Botania> BOTANIA = new InternalModContainer<>("botania", "Botania", Botania::new);
    public static final GroovyContainer<BotanicAdditions> BOTANIC_ADDITIONS = new InternalModContainer<>("botanicadds", "Botanic Additions", BotanicAdditions::new);
    public static final GroovyContainer<Calculator> CALCULATOR = new InternalModContainer<>("calculator", "Calculator", Calculator::new);
    public static final GroovyContainer<Chisel> CHISEL = new InternalModContainer<>("chisel", "Chisel", Chisel::new);
    public static final GroovyContainer<CompactMachines> COMPACT_MACHINES = new InternalModContainer<>("compactmachines3", "Compact Machines 3", CompactMachines::new, "compactmachines");
    public static final GroovyContainer<Cyclic> CYCLIC = new InternalModContainer<>("cyclicmagic", "Cyclic", Cyclic::new, "cyclic");
    public static final GroovyContainer<DraconicEvolution> DRACONIC_EVOLUTION = new InternalModContainer<>("draconicevolution", "Draconic Evolution", DraconicEvolution::new, "de");
    public static final GroovyContainer<EnderIO> ENDER_IO = new InternalModContainer<>("enderio", "Ender IO", EnderIO::new, "eio");
    public static final GroovyContainer<EssentialCraft> ESSENTIALCRAFT = new InternalModContainer<>("essentialcraft", "EssentialCraft 4", EssentialCraft::new, "ec4");
    public static final GroovyContainer<EvilCraft> EVILCRAFT = new InternalModContainer<>("evilcraft", "EvilCraft", EvilCraft::new);
    public static final GroovyContainer<ExtendedCrafting> EXTENDED_CRAFTING = new InternalModContainer<>("extendedcrafting", "Extended Crafting", ExtendedCrafting::new);
    public static final GroovyContainer<ExtraBotany> EXTRA_BOTANY = new InternalModContainer<>("extrabotany", "Extra Botany", ExtraBotany::new);
    public static final GroovyContainer<ExtraUtils2> EXTRA_UTILITIES_2 = new InternalModContainer<>("extrautils2", "Extra Utilities 2", ExtraUtils2::new, "extrautilities2");
    public static final GroovyContainer<Forestry> FORESTRY = new InternalModContainer<>("forestry", "Forestry", Forestry::new);
    public static final GroovyContainer<ImmersiveEngineering> IMMERSIVE_ENGINEERING = new InternalModContainer<>("immersiveengineering", "Immersive Engineering", ImmersiveEngineering::new, "ie");
    public static final GroovyContainer<ImmersivePetroleum> IMMERSIVE_PETROLEUM = new InternalModContainer<>("immersivepetroleum", "Immersive Petroleum", ImmersivePetroleum::new);
    public static final GroovyContainer<ImmersiveTechnology> IMMERSIVE_TECHNOLOGY = new InternalModContainer<>("immersivetech", "Immersive Technology", ImmersiveTechnology::new);
    public static final GroovyContainer<IC2> INDUSTRIALCRAFT = new InternalModContainer<>("ic2", "Industrial Craft 2", IC2::new, "industrialcraft");
    public static final GroovyContainer<IndustrialForegoing> INDUSTRIAL_FOREGOING = new InternalModContainer<>("industrialforegoing", "Industrial Foregoing", IndustrialForegoing::new);
    public static final GroovyContainer<Inspirations> INSPIRATIONS = new InternalModContainer<>("inspirations", "Inspirations", Inspirations::new);
    public static final GroovyContainer<IntegratedDynamics> INTEGRATED_DYNAMICS = new InternalModContainer<>("integrateddynamics", "Integrated Dynamics", IntegratedDynamics::new, "id");
    public static final GroovyContainer<JustEnoughItems> JEI = new InternalModContainer<>("jei", "Just Enough Items", JustEnoughItems::new, "hei");
    public static final GroovyContainer<Mekanism> MEKANISM = new InternalModContainer<>("mekanism", "Mekanism", Mekanism::new);
    public static final GroovyContainer<MysticalAgriculture> MYSTICAL_AGRICULTURE = new InternalModContainer<>("mysticalagriculture", "Mystical Agriculture", MysticalAgriculture::new);
    public static final GroovyContainer<LazyAE2> LAZYAE2 = new InternalModContainer<>("threng", "LazyAE2", LazyAE2::new, "lazyae2");
    public static final GroovyContainer<NaturesAura> NATURES_AURA = new InternalModContainer<>("naturesaura", "Nature's Aura", NaturesAura::new);
    public static final GroovyContainer<PneumaticCraft> PNEUMATIC_CRAFT = new InternalModContainer<>("pneumaticcraft", "PneumaticCraft: Repressurized", PneumaticCraft::new);
    public static final GroovyContainer<PrimalTech> PRIMAL_TECH = new InternalModContainer<>("primal_tech", "Primal Tech", PrimalTech::new, "primaltech");
    public static final GroovyContainer<ProdigyTech> PRODIGY_TECH = new InternalModContainer<>("prodigytech", "Prodigy Tech", ProdigyTech::new);
    public static final GroovyContainer<ProjectE> PROJECT_E = new InternalModContainer<>("projecte", "ProjectE", ProjectE::new);
    public static final GroovyContainer<PyroTech> PYROTECH = new InternalModContainer<>("pyrotech", "Pyrotech", PyroTech::new);
    public static final GroovyContainer<Roots> ROOTS = new InternalModContainer<>("roots", "Roots 3", Roots::new);
    public static final GroovyContainer<Rustic> RUSTIC = new InternalModContainer<>("rustic", "Rustic", Rustic::new);
    public static final GroovyContainer<TechReborn> TECH_REBORN = new InternalModContainer<>("techreborn", "Tech Reborn", TechReborn::new);
    public static final GroovyContainer<Thaumcraft> THAUMCRAFT = new InternalModContainer<>("thaumcraft", "Thaumcraft", Thaumcraft::new, "tc", "thaum");
    public static final GroovyContainer<TheAurorian> THE_AURORIAN = new InternalModContainer<>("theaurorian", "The Aurorian", TheAurorian::new, "aurorian");
    public static final GroovyContainer<ThermalExpansion> THERMAL_EXPANSION = new InternalModContainer<>("thermalexpansion", "Thermal Expansion", ThermalExpansion::new, "thermal");
    public static final GroovyContainer<TinkersComplement> TINKERS_COMPLEMENT = new InternalModContainer<>("tcomplement", "Tinkers Complement", TinkersComplement::new, "tcomp", "tinkerscomplement");
    public static final GroovyContainer<TinkersConstruct> TINKERS_CONSTRUCT = new InternalModContainer<>("tconstruct", "Tinkers' Construct", TinkersConstruct::new, "ticon", "tinkersconstruct");
    public static final GroovyContainer<Woot> WOOT = new InternalModContainer<>("woot", "Woot", Woot::new);

    public static Collection<GroovyContainer<? extends GroovyPropertyContainer>> getAllContainers() {
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

        GroovyPropertyContainer groovyPropertyContainer = container.createGroovyPropertyContainer();
        if (groovyPropertyContainer == null) {
            groovyPropertyContainer = new GroovyPropertyContainer();
        }
        registerContainer(new ExternalModContainer(container, groovyPropertyContainer));
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

    @Deprecated
    @Nullable
    public Object getProperty(String name) {
        GroovyContainer<?> container = containers.get(name);
        return container != null ? container.get() : null;
    }


    @Deprecated
    public @UnmodifiableView Map<String, ? extends GroovyContainer<?>> getProperties() {
        return containersView;
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void init() {
        frozen = true;
        for (GroovyContainer<?> container : containerList) {
            if (container.isLoaded()) {
                container.onCompatLoaded(container);
                container.get().initialize(container);
                ExpansionHelper.mixinConstProperty(ModSupport.class, container.getModId(), container.get(), false);
                for (String s : container.getAliases()) {
                    if (!container.getModId().equals(s)) {
                        ExpansionHelper.mixinConstProperty(ModSupport.class, s, container.get(), true);
                    }
                }
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
