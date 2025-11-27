package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroovyFluid extends Fluid {

    public static final ResourceLocation DEFAULT_STILL = new ResourceLocation(GroovyScript.ID, "blocks/fluid");
    public static final ResourceLocation DEFAULT_FLOWING = new ResourceLocation(GroovyScript.ID, "blocks/fluid_flow");
    public static final ResourceLocation METAL_STILL = new ResourceLocation(GroovyScript.ID, "blocks/molten_metal");
    public static final ResourceLocation METAL_FLOWING = new ResourceLocation(GroovyScript.ID, "blocks/molten_metal_flow");

    private static final List<BlockFluidBase> fluidBlocks = new ArrayList<>();
    private static final List<GroovyFluid> fluids = new ArrayList<>();

    public GroovyFluid(String fluidName, ResourceLocation still, ResourceLocation flowing, @Nullable ResourceLocation overlay, int color) {
        super(fluidName, still, flowing, overlay, color);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public static void initBlocks(IForgeRegistry<Block> registry) {
        for (BlockFluidBase block : fluidBlocks) {
            registry.register(block);
            checkBlockState(block.getRegistryName());
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    @SideOnly(Side.CLIENT)
    public static void initTextures(TextureMap map) {
        for (GroovyFluid fluid : fluids) {
            map.registerSprite(fluid.flowing);
            map.registerSprite(fluid.still);
            if (fluid.overlay != null) {
                map.registerSprite(fluid.overlay);
            }
        }
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (BlockFluidBase block : fluidBlocks) {
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFluidClassic.LEVEL).build());
        }
    }

    private static void checkBlockState(ResourceLocation loc) {
        File file = FileUtil.makeFile(GroovyScript.getResourcesFile().getPath(), loc.getNamespace(), "blockstates", loc.getPath() + ".json");
        if (!file.exists()) {
            JsonObject stateJson = new JsonObject();
            stateJson.addProperty("forge_marker", 1);
            JsonObject defaultsJson = new JsonObject();
            defaultsJson.addProperty("model", "forge:fluid");
            defaultsJson.add("textures", new JsonObject());
            stateJson.add("defaults", defaultsJson);
            JsonObject variantsJson = new JsonObject();
            JsonObject modelJson = new JsonObject();
            modelJson.addProperty("transform", "forge:default-item");
            JsonObject customJson = new JsonObject();
            customJson.addProperty("fluid", loc.getPath());
            modelJson.add("custom", customJson);
            variantsJson.add("normal", modelJson);
            stateJson.add("variants", variantsJson);
            JsonHelper.saveJson(file, stateJson);
        }
    }

    public static class Builder {

        private final String name;
        @Property(defaultValue = "DEFAULT_STILL")
        private ResourceLocation still = DEFAULT_STILL;
        @Property(defaultValue = "DEFAULT_FLOWING")
        private ResourceLocation flowing = DEFAULT_FLOWING;
        @Property
        private ResourceLocation overlay;
        @Property(defaultValue = "0xFFFFFF")
        private int color = 0xFFFFFF;

        @Property
        private SoundEvent fillSound;
        @Property
        private SoundEvent emptySound;

        @Property
        private int luminosity;
        @Property(defaultValue = "1000")
        private int density = 1000;
        @Property(defaultValue = "300")
        private int temperature = 300;
        @Property(defaultValue = "1000")
        private int viscosity = 1000;
        @Property
        private boolean isGaseous;
        @Property(defaultValue = "EnumRarity.COMMON")
        private EnumRarity rarity = EnumRarity.COMMON;

        @Property(defaultValue = "blockMaterial('water')")
        private Material material = Material.WATER;
        @Property(defaultValue = "true")
        private boolean createBlock = true;
        @Property
        private boolean finiteFluidBlock;

        public Builder(String name) {
            this.name = name;
        }

        @RecipeBuilderMethodDescription(field = "still")
        public Builder setStill(ResourceLocation still) {
            this.still = still;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "flowing")
        public Builder setFlowing(ResourceLocation flowing) {
            this.flowing = flowing;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "overlay")
        public Builder setOverlay(ResourceLocation overlay) {
            this.overlay = overlay;
            return this;
        }

        @RecipeBuilderMethodDescription(description = "groovyscript.wiki.minecraft.content.fluid.setTexture0.description", field = {
                "still", "flowing"
        })
        public Builder setTexture(ResourceLocation still, ResourceLocation flowing) {
            return setStill(still).setFlowing(flowing);
        }

        @RecipeBuilderMethodDescription(description = "groovyscript.wiki.minecraft.content.fluid.setTexture1.description", field = {
                "still", "flowing", "overlay"
        })
        public Builder setTexture(ResourceLocation still, ResourceLocation flowing, @Nullable ResourceLocation overlay) {
            return setStill(still).setFlowing(flowing).setOverlay(overlay);
        }

        @RecipeBuilderMethodDescription(priority = 100, field = {
                "still", "flowing"
        })
        public Builder setDefaultTexture() {
            return setTexture(DEFAULT_STILL, DEFAULT_FLOWING);
        }

        @RecipeBuilderMethodDescription(priority = 500, field = {
                "still", "flowing"
        })
        public Builder setMetalTexture() {
            return setTexture(METAL_STILL, METAL_FLOWING);
        }

        @RecipeBuilderMethodDescription(field = "color")
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        @RecipeBuilderMethodDescription(priority = 500, field = "fillSound")
        public Builder setFillSound(SoundEvent fillSound) {
            this.fillSound = fillSound;
            return this;
        }

        @RecipeBuilderMethodDescription(priority = 500, field = "emptySound")
        public Builder setEmptySound(SoundEvent emptySound) {
            this.emptySound = emptySound;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {
                "fillSound", "emptySound"
        })
        public Builder setSound(SoundEvent fillSound, SoundEvent emptySound) {
            this.fillSound = fillSound;
            this.emptySound = emptySound;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "luminosity")
        public Builder setLuminosity(int luminosity) {
            this.luminosity = luminosity;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "density")
        public Builder setDensity(int density) {
            this.density = density;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "temperature")
        public Builder setTemperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "viscosity")
        public Builder setViscosity(int viscosity) {
            this.viscosity = viscosity;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "isGaseous")
        public Builder setGaseous(boolean gaseous) {
            isGaseous = gaseous;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "rarity")
        public Builder setRarity(EnumRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "material")
        public Builder setWaterMaterial() {
            this.material = Material.WATER;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "material")
        public Builder setLavaMaterial() {
            this.material = Material.LAVA;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "createBlock")
        public Builder noBlock() {
            this.createBlock = false;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "finiteFluidBlock")
        public Builder isFinite() {
            this.finiteFluidBlock = true;
            return this;
        }

        @RecipeBuilderRegistrationMethod(description = "groovyscript.wiki.minecraft.content.fluid.register.description")
        public Fluid register() {
            this.color |= 0xFF << 24;
            String mod = GroovyScript.getRunConfig().getPackId();
            Fluid fluid = new GroovyFluid(this.name, this.still, this.flowing, this.overlay, this.color)
                    .setFillSound(this.fillSound)
                    .setEmptySound(this.emptySound)
                    .setLuminosity(this.luminosity)
                    .setDensity(this.density)
                    .setTemperature(this.temperature)
                    .setViscosity(this.viscosity)
                    .setGaseous(this.isGaseous)
                    .setRarity(this.rarity)
                    .setUnlocalizedName(mod + "." + this.name + ".name");
            FluidRegistry.registerFluid(fluid);
            FluidRegistry.addBucketForFluid(fluid);
            if (this.createBlock) {
                BlockFluidBase block = this.finiteFluidBlock ? new BlockFluidFinite(fluid, this.material) : new BlockFluidClassic(fluid, this.material);
                block.setRegistryName(new ResourceLocation(mod, this.name));
                fluidBlocks.add(block);
            }
            fluids.add((GroovyFluid) fluid);
            return fluid;
        }
    }
}
