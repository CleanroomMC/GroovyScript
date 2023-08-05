package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.event.GroovyReloadEvent;
import com.cleanroommc.groovyscript.event.ScriptRunEvent;
import com.cleanroommc.groovyscript.helper.GroovyHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

public class GroovyScriptSandbox extends GroovySandbox {

    private final ImportCustomizer importCustomizer = new ImportCustomizer();

    private LoadStage currentLoadStage;
    private boolean checkSyntaxMode = false;

    public GroovyScriptSandbox(URL... scriptEnvironment) {
        super(scriptEnvironment);
        registerBinding("Mods", ModSupport.INSTANCE);
        registerBinding("Log", GroovyLog.get());
        registerBinding("EventManager", GroovyEventManager.INSTANCE);
        this.importCustomizer.addStaticStars(GroovyHelper.class.getName(), MathHelper.class.getName());
        this.importCustomizer.addImports("net.minecraft.world.World",
                                    "net.minecraft.block.state.IBlockState",
                                    "net.minecraft.block.Block",
                                    "net.minecraft.block.SoundType",
                                    "net.minecraft.enchantment.Enchantment",
                                    "net.minecraft.entity.Entity",
                                    "net.minecraft.entity.player.EntityPlayer",
                                    "net.minecraft.init.Biomes",
                                    "net.minecraft.init.Blocks",
                                    "net.minecraft.init.Enchantments",
                                    "net.minecraft.init.Items",
                                    "net.minecraft.init.MobEffects",
                                    "net.minecraft.init.PotionTypes",
                                    "net.minecraft.init.SoundEvents",
                                    "net.minecraft.item.EnumRarity",
                                    "net.minecraft.item.Item",
                                    "net.minecraft.item.ItemStack",
                                    "net.minecraft.nbt.NBTTagCompound",
                                    "net.minecraft.nbt.NBTTagList",
                                    "net.minecraft.tileentity.TileEntity",
                                    "net.minecraft.util.math.BlockPos",
                                    "net.minecraft.util.DamageSource",
                                    "net.minecraft.util.EnumHand",
                                    "net.minecraft.util.EnumHandSide",
                                    "net.minecraft.util.EnumFacing",
                                    "net.minecraft.util.ResourceLocation",
                                    "net.minecraftforge.fml.common.eventhandler.EventPriority",
                                    "com.cleanroommc.groovyscript.event.EventBusType");
    }

    public void checkSyntax() {
        load(LoadStage.PRE_INIT, false, true);
        for (LoadStage loadStage : LoadStage.getLoadStages()) {
            if (loadStage != LoadStage.PRE_INIT) {
                load(loadStage, false, false);
            }
        }
    }

    public void checkSyntax(LoadStage loadStage) {
        load(loadStage, false, true);
    }

    public void run(LoadStage currentLoadStage) {
        load(currentLoadStage, true, true);
    }

    public void load(LoadStage currentLoadStage, boolean run, boolean loadClasses) {
        this.checkSyntaxMode = !run;
        this.currentLoadStage = Objects.requireNonNull(currentLoadStage);
        try {
            super.load(run, loadClasses);
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.get().errorMC("An Exception occurred trying to run groovy!");
            GroovyScript.LOGGER.throwing(e);
        } catch (Exception e) {
            GroovyLog.get().exception(e);
        } finally {
            this.currentLoadStage = null;
            this.checkSyntaxMode = false;
        }
    }

    @ApiStatus.Internal
    @Override
    public void load(boolean run, boolean loadClasses) throws Exception {
        throw new UnsupportedOperationException("Use run(Loader loader) instead!");
    }

    @Override
    public <T> T runClosure(Closure<T> closure, Object... args) {
        startRunning();
        T result = null;
        try {
            result = closure.call(args);
        } catch (Exception e) {
            GroovyLog.get().error("An exception occurred while running a closure!");
            GroovyLog.get().exception(e);
        } finally {
            stopRunning();
        }
        return result;
    }

    @Override
    protected void postInitBindings(Binding binding) {
        binding.setProperty("out", GroovyLog.get().getWriter());
        binding.setVariable("globals", getBindings());
    }

    @Override
    protected void initEngine(GroovyScriptEngine engine, CompilerConfiguration config) {
        config.addCompilationCustomizers(GroovyScriptCompiler.transformer());
        config.addCompilationCustomizers(this.importCustomizer);
    }

    @Override
    protected void preRun() {
        if (this.checkSyntaxMode) {
            GroovyLog.get().info("Checking syntax in loader '{}'", this.currentLoadStage);
        } else {
            GroovyLog.get().info("Running scripts in loader '{}'", this.currentLoadStage);
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
        if (this.currentLoadStage.isReloadable() && !ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.onReload();
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyEventManager.INSTANCE.reset();
    }

    @Override
    protected boolean shouldRunFile(File file) {
        if (!this.checkSyntaxMode) {
            GroovyLog.get().info(" - executing {}", file.toString());
        }
        return true;
    }

    @Override
    protected void postRun() {
        if (this.currentLoadStage == LoadStage.POST_INIT) {
            ReloadableRegistryManager.afterScriptRun();
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
        if (this.currentLoadStage == LoadStage.POST_INIT && ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }

    @Override
    public Collection<File> getClassFiles() {
        return GroovyScript.getRunConfig().getClassFiles(this.currentLoadStage.getName());
    }

    @Override
    public Collection<File> getScriptFiles() {
        return GroovyScript.getRunConfig().getSortedFiles(this.currentLoadStage.getName());
    }

    @Nullable
    public LoadStage getCurrentLoader() {
        return currentLoadStage;
    }

    public ImportCustomizer getImportCustomizer() {
        return importCustomizer;
    }
}
