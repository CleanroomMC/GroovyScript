package com.cleanroommc.groovyscript.compat.mods.alchemistry;

import al132.alchemistry.chemistry.ChemicalCompound;
import al132.alchemistry.chemistry.ChemicalElement;
import al132.alchemistry.chemistry.CompoundRegistry;
import al132.alchemistry.chemistry.ElementRegistry;
import com.cleanroommc.groovyscript.api.Result;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import net.minecraft.item.ItemStack;

public class Alchemistry extends GroovyPropertyContainer {

    public final Atomizer atomizer = new Atomizer();
    public final Combiner combiner = new Combiner();
    public final Dissolver dissolver = new Dissolver();
    public final Electrolyzer electrolyzer = new Electrolyzer();
    public final Evaporator evaporator = new Evaporator();
    public final Liquifier liquifier = new Liquifier();
    // TODO:
    //  Compound Creation and Element Creation

    @Override
    public void initialize(GroovyContainer<?> container) {
        container.objectMapperBuilder("element", ItemStack.class)
                .parser((s, args) -> {
                    String parsedName = s.trim().toLowerCase().replace(" ", "_");
                    ChemicalCompound compound = CompoundRegistry.INSTANCE.get(parsedName);
                    if (compound == null || compound.toItemStack(1).isEmpty()) {
                        ChemicalElement element = ElementRegistry.INSTANCE.get(parsedName);
                        if (element == null || element.toItemStack(1).isEmpty()) {
                            return Result.error();
                        }
                        return Result.some(element.toItemStack(1));
                    }
                    return Result.some(compound.toItemStack(1));
                })
                .defaultValue(() -> ItemStack.EMPTY)
                .completerOfNamed(CompoundRegistry.INSTANCE::compounds, ChemicalCompound::getName)
                .completerOfNamed(ElementRegistry.INSTANCE::getAllElements, ChemicalElement::getName)
                .docOfType("chemical element or compound as item stack")
                .register();
    }
}
