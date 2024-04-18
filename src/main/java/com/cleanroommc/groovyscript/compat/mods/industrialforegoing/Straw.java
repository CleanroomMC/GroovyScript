package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.straw.StrawHandler;
import com.buuz135.industrial.registry.IFRegistries;
import com.buuz135.industrial.utils.apihandlers.straw.PotionStrawHandler;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@RegistryDescription
public class Straw extends ForgeRegistryWrapper<StrawHandler> {

    public Straw() {
        super(IFRegistries.STRAW_HANDLER_REGISTRY);
    }

    @RecipeBuilderDescription(example = @Example(value = ".fluidInput(fluid('if.pink_slime')).effect(new PotionEffect(potion('minecraft:strength'), 1800, 3))", imports = "net.minecraft.potion.PotionEffect"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.straw.add0", type = MethodDescription.Type.ADDITION)
    public StrawHandler add(FluidStack fluidInput, Collection<PotionEffect> effect) {
        return recipeBuilder()
                .effect(effect)
                .fluidInput(fluidInput)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.industrialforegoing.straw.add1", type = MethodDescription.Type.ADDITION)
    public StrawHandler add(String name, FluidStack fluidInput, Collection<PotionEffect> effect) {
        return recipeBuilder()
                .effect(effect)
                .name(name)
                .fluidInput(fluidInput)
                .register();
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "name")
    public static class RecipeBuilder extends AbstractRecipeBuilder<StrawHandler> {

        @Property(valid = @Comp(type = Comp.Type.GTE, value = "1"))
        private final Collection<PotionEffect> effect = new ArrayList<>();

        @RecipeBuilderMethodDescription
        public RecipeBuilder effect(PotionEffect effect) {
            this.effect.add(effect);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder effect(PotionEffect... effects) {
            for (PotionEffect ingredient : effects) {
                effect(ingredient);
            }
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder effect(Collection<PotionEffect> effects) {
            for (PotionEffect ingredient : effects) {
                effect(ingredient);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Industrial Foregoing Straw Entry";
        }

        @Override
        public String getRecipeNamePrefix() {
            return "groovyscript_straw_entry_";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(effect.isEmpty(), "effect must have entries, yet it was empty");
        }

        @RecipeBuilderRegistrationMethod
        @Override
        public @Nullable StrawHandler register() {
            if (!validate()) return null;
            PotionStrawHandler recipe = new PotionStrawHandler(fluidInput.get(0).getFluid());
            effect.forEach(recipe::addPotion);
            recipe.setRegistryName(this.name);
            ModSupport.INDUSTRIAL_FOREGOING.get().straw.add(recipe);
            return recipe;
        }
    }
}
