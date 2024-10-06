package com.cleanroommc.groovyscript.compat.vanilla.command.infoparser;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserPackage;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InfoParserFluid extends GenericInfoParser<FluidStack> {

    public static final InfoParserFluid instance = new InfoParserFluid();

    @Override
    public String id() {
        return "fluid";
    }

    @Override
    public String name() {
        return "Fluid";
    }

    @Override
    public String text(@NotNull FluidStack entry, boolean colored, boolean prettyNbt) {
        return GroovyScriptCodeConverter.asGroovyCode(entry, colored, prettyNbt);
    }

    @Override
    public void parse(InfoParserPackage info) {
        if (info.getStack().isEmpty()) {
            // if the item holds fluids, add that info
            Fluid fluid = FluidRegistry.lookupFluidForBlock(info.getBlock());
            if (fluid == null) return;
            FluidStack fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
            instance.add(info.getMessages(), fluidStack, info.isPrettyNbt());
            InfoParserTranslationKey.instance.add(info.getMessages(), fluidStack.getUnlocalizedName(), info.isPrettyNbt());
            return;
        }
        // if the item holds fluids, add that info
        if (info.getStack().hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            IFluidHandler handler = info.getStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (handler != null) {
                List<FluidStack> fluids = Arrays.stream(handler.getTankProperties())
                        .map(IFluidTankProperties::getContents)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                instance.add(info.getMessages(), fluids, info.isPrettyNbt());

                List<String> keys = fluids.stream()
                        .map(FluidStack::getUnlocalizedName)
                        .collect(Collectors.toList());
                InfoParserTranslationKey.instance.add(info.getMessages(), keys, info.isPrettyNbt());
            }
        }
    }
}
