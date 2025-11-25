package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import groovy.lang.Closure;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@RegistryDescription(location = LoadStage.PRE_INIT, category = RegistryDescription.Category.CUSTOM, priority = 500)
public class Content extends NamedRegistry {

    public CreativeTabs defaultTab;

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = """
            'snack', (new ItemFood(20, 10, false) {
                protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
                    if (!worldIn.isRemote) {
                        player.addPotionEffect(new PotionEffect(potion('minecraft:regeneration'), 240000, 3, false, false))
                        player.addPotionEffect(new PotionEffect(potion('minecraft:resistance'), 240000, 3, false, false))
                    }
                }
            }).setAlwaysEdible()""", imports = {
            "net.minecraft.item.ItemFood",
            "net.minecraft.potion.PotionEffect",
            "net.minecraft.entity.player.EntityPlayer"
    }))
    public void registerItem(@Nullable String name, Item item) {
        if (name != null) {
            item.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        } else if (item.getRegistryName() == null) {
            GroovyLog.get().errorMC("Can't register item without a name!");
            return;
        }
        GroovyItem.registerItem(item);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void registerBlock(String name, Block block, ItemBlock item) {
        block.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        item.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        GroovyBlock.register(block, item);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = """
            'dragon_egg_lamp', (new Block(blockMaterial('redstone_light')) {
                protected static final AxisAlignedBB DRAGON_EGG_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D)

                AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
                    return DRAGON_EGG_AABB
                }

                boolean isOpaqueCube(IBlockState state) {
                    return false
                }

                boolean isFullCube(IBlockState state) {
                    return false
                }

                boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
                    return true
                }

                BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
                    return BlockFaceShape.UNDEFINED
                }
            }).setLightLevel(1.0F)""", imports = {
            "net.minecraft.block.state.BlockFaceShape",
            "net.minecraft.util.math.AxisAlignedBB",
            "net.minecraft.world.IBlockAccess"
    }))
    public void registerBlock(String name, Block block) {
        registerBlock(name, block, new ItemBlock(block));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void registerFluid(Fluid fluid) {
        FluidRegistry.registerFluid(fluid);
    }

    @RecipeBuilderDescription(example = {
            @Example("('heartofauniverse').setRarity(EnumRarity.EPIC).setMaxStackSize(1)"),
            @Example("('clay_2').setMaxStackSize(5).setRarity(EnumRarity.RARE)"),
            @Example("('clay_3').setCreativeTab(creativeTab('misc')).setEnchantedEffect()")
    })
    public GroovyItem createItem(String name) {
        return new GroovyItem(name);
    }

    @RecipeBuilderDescription(description = "groovyscript.wiki.minecraft.content.createBlock.description0")
    public GroovyBlock createBlock(String name, Material material) {
        return new GroovyBlock(name, material);
    }

    @RecipeBuilderDescription(description = "groovyscript.wiki.minecraft.content.createBlock.description1", example = @Example("('generic_block')"))
    public GroovyBlock createBlock(String name) {
        return new GroovyBlock(name, Material.ROCK);
    }

    @RecipeBuilderDescription(example = @Example("('amongium').setMetalTexture().setColor(0x00FF00)"))
    public GroovyFluid.Builder createFluid(String name) {
        return new GroovyFluid.Builder(name);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500)
    public CreativeTabs createCreativeTab(String name, ItemStack icon) {
        return new CreativeTabs(name) {

            @Override
            public @NotNull ItemStack createIcon() {
                return icon.copy();
            }
        };
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500)
    public CreativeTabs createCreativeTab(String name, Supplier<ItemStack> icon) {
        return new CreativeTabs(name) {

            @Override
            public @NotNull ItemStack createIcon() {
                return icon.get().copy();
            }
        };
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500, example = @Example("'groovyscript.other_tab_clay', _ -> item('minecraft:clay')"))
    public CreativeTabs createCreativeTab(String name, Closure<ItemStack> icon) {
        return new CreativeTabs(name) {

            @Override
            public @NotNull ItemStack createIcon() {
                return icon.call().copy();
            }
        };
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, priority = 500)
    public CreativeTabs createCreativeTab(String name, Item icon) {
        return createCreativeTab(name, new ItemStack(icon));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public CreativeTabs getDefaultTab() {
        return defaultTab;
    }

    // Set as VALUE so it is the first in the file
    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example("content.createCreativeTab('groovyscript.example_creative_tab', _ -> item('groovyscriptdev:heartofauniverse'))"))
    public void setDefaultCreativeTab(CreativeTabs tab) {
        this.defaultTab = tab;
    }
}
