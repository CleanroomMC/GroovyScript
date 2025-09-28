package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.core.mixin.CreativeTabsAccessor;
import com.cleanroommc.groovyscript.core.mixin.VillagerProfessionAccessor;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static com.cleanroommc.groovyscript.mapper.ObjectMapperManager.SPLITTER;
import static com.cleanroommc.groovyscript.mapper.ObjectMapperManager.WILDCARD;

public class ObjectMappers {

    private static final String COMMA = ",";
    private static final String EQUALS = "=";

    public static @NotNull Result<ResourceLocation> parseResourceLocation(String mainArg, Object... args) {
        String[] parts = mainArg.split(SPLITTER);
        if (parts.length > 1) {
            if (parts.length > 2) {
                return Result.error("Resource location must only contain one ':' to separate mod and path.");
            }
            if (args.length > 0) {
                return Result.error("If ':' is used in the resource location, no other arguments are allowed.");
            }
            return Result.some(new ResourceLocation(parts[0], parts[1]));
        }

        if (args.length > 0) {
            if (args.length > 1 || !(args[0] instanceof String s)) {
                return Result.error("Arguments not valid for object mapper. Use 'resource(String)' or 'resource(String mod, String path)'");
            }
            return Result.some(new ResourceLocation(mainArg, s));
        }
        return Result.some(new ResourceLocation(GroovyScript.getRunConfig().getPackId(), mainArg));
    }

    public static @NotNull Result<IIngredient> parseOreDict(String mainArg, Object... args) {
        if (args.length > 0) {
            return Result.error("Arguments not valid for object mapper. Use 'ore(String)'");
        }
        if ("Unknown".equals(mainArg)) {
            return Result.error("Unknown cannot be an OreDict");
        }
        // TODO: remove this warning in a later update
        if (mainArg.contains("*")) {
            GroovyLog.msg("ore Object Mapper '{}' contained '*'", mainArg)
                    .add("if this is supposed to be an OreDictWildcardIngredient, use 'oredict.getOres(name)' instead")
                    .warn()
                    .post();
        }
        return Result.some(new OreDictIngredient(mainArg));
    }

    public static @NotNull Result<ItemStack> parseItemStack(String mainArg, Object... args) {
        if (args.length > 1 || (args.length == 1 && !(args[0] instanceof Integer))) {
            return Result.error("Arguments not valid for bracket handler. Use 'item(String)' or 'item(String, int meta)'");
        }
        String[] parts = mainArg.split(SPLITTER);
        if (parts.length < 2) {
            return Result.error("must contain a ':' to separate mod and path");
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item == null) {
            return Result.error();
        }
        int meta = 0;
        if (parts.length > 2) {
            if (WILDCARD.equals(parts[2])) {
                meta = Short.MAX_VALUE;
            } else {
                try {
                    meta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (args.length == 1) {
            if (meta != 0) {
                return Result.error("Defined meta value twice for item mapper");
            }
            meta = (int) args[0];
        }
        return Result.some(new ItemStack(item, 1, meta));
    }

    public static Result<FluidStack> parseFluidStack(String s, Object... args) {
        if (args.length > 0) return Result.error("No extra arguments are allowed.");
        Fluid fluid = FluidRegistry.getFluid(s);
        if (fluid == null) return Result.error();
        return Result.some(new FluidStack(fluid, 1));
    }

    public static @NotNull Result<IBlockState> parseBlockState(String mainArg, Object... args) {
        Result<IBlockState> blockStateResult = parseBlockState(mainArg);
        if (blockStateResult.hasError()) return blockStateResult;
        IBlockState blockState = blockStateResult.getValue();
        if (args.length > 0) {
            if (args.length == 1 && args[0] instanceof Integer) {
                try {
                    return Result.some(blockState.getBlock().getStateFromMeta((Integer) args[0]));
                } catch (Exception e) {
                    return Result.error("could not get block state from meta");
                }
            }
            for (Object arg : args) {
                if (!(arg instanceof String)) {
                    return Result.error("All arguments must be strings!");
                }
            }
            String[] stringArgs = Arrays.stream(args).map(Object::toString).toArray(String[]::new);
            return parseBlockStates(blockState, Iterators.forArray(stringArgs));
        }
        return blockStateResult;
    }

    public static Result<IBlockState> parseBlockState(String arg) {
        String[] parts = arg.split(SPLITTER);
        if (parts.length < 2) {
            return Result.error("Can't find block for '{}'", arg);
        }
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (block == null) {
            return Result.error("Can't find block for '{}'", arg);
        }
        IBlockState blockState = block.getDefaultState();
        if (parts.length > 2) {
            String[] states = parts[2].split(COMMA);
            if (states.length == 1) {
                try {
                    int meta = Integer.parseInt(states[0]);
                    return Result.some(blockState.getBlock().getStateFromMeta(meta));
                } catch (NumberFormatException ignored) {
                } catch (Exception e) {
                    return Result.error("could not get block state from meta");
                }
            }
            return parseBlockStates(blockState, Iterators.forArray(states));
        }
        return Result.some(blockState);
    }

    @SuppressWarnings("all")
    private static Result<IBlockState> parseBlockStates(IBlockState defaultState, Iterator<String> iterable) {
        for (Iterator<String> it = iterable; it.hasNext();) {
            String state = it.next();
            String[] prop = state.split(EQUALS, 2);
            IProperty property = defaultState.getBlock().getBlockState().getProperty(prop[0]);
            if (property == null) {
                return Result.error("Invalid property name '{}' for block '{}'", prop[0], defaultState.getBlock().getRegistryName());
            }
            Optional<? extends Comparable> value = property.parseValue(prop[1]);
            if (value.isPresent()) {
                defaultState = defaultState.withProperty(property, value.get());
            } else {
                return Result.error("Invalid property value '{}' for block '{}:{}'", prop[1], defaultState.getBlock().getRegistryName());
            }
        }
        return Result.some(defaultState);
    }

    public static Result<VillagerRegistry.VillagerCareer> parseVillagerCareer(String mainArg, Object... args) {
        for (var profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
            if (profession != null) {
                for (var career : ((VillagerProfessionAccessor) (profession)).getCareers()) {
                    if (career != null && mainArg.equals(career.getName())) {
                        return Result.some(career);
                    }
                }
            }
        }
        return Result.error();
    }

    public static Result<CreativeTabs> parseCreativeTab(String mainArg, Object... args) {
        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (tab != null && mainArg.equals(((CreativeTabsAccessor) tab).getTabLabel2())) {
                return Result.some(tab);
            }
        }
        return Result.error();
    }

    public static Result<TextFormatting> parseTextFormatting(String mainArg, Object... args) {
        TextFormatting textformat = TextFormatting.getValueByName(mainArg);
        if (textformat == null) {
            try {
                textformat = TextFormatting.fromColorIndex(Integer.parseInt(mainArg));
            } catch (NumberFormatException e) {
                return Result.error("argument is not a number and not a valid text formatting name");
            }
        }
        return textformat == null ? Result.error() : Result.some(textformat);
    }

    private static Map<String, Material> materials;

    public static Result<Material> parseBlockMaterial(String mainArg, Object... args) {
        if (materials == null) {
            materials = new Object2ObjectOpenHashMap<>();
            for (Field field : Material.class.getFields()) {
                if ((field.getModifiers() & Modifier.STATIC) != 0 && field.getType() == Material.class) {
                    try {
                        Material material = (Material) field.get(null);
                        materials.put(field.getName(), material);
                        materials.put(field.getName().toLowerCase(Locale.ROOT), material);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        Material material = materials.get(mainArg);
        return material == null ? Result.error() : Result.some(material);
    }

    public static @NotNull Result<NBTTagCompound> parseNBT(String mainArg, Object... args) {
        try {
            return Result.some(JsonToNBT.getTagFromJson(mainArg));
        } catch (NBTException e) {
            return Result.error("unable to parse provided nbt string");
        }
    }
}
