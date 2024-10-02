package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientRecipeComponent;
import org.cyclops.evilcraft.Configs;
import org.cyclops.evilcraft.block.BloodInfuserConfig;
import org.cyclops.evilcraft.core.recipe.custom.DurationXpRecipeProperties;
import org.cyclops.evilcraft.core.recipe.custom.IngredientFluidStackAndTierRecipeComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class BloodInfuser extends StandardListRegistry<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BloodInfuserConfig.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:clay')).fluidInput(fluid('evilcraftblood') * 1000).tier(3).duration(100).xp(10000)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).fluidInput(100000)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay') * 4).fluidInput(5000).tier(1)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> getRecipes() {
        return org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes();
    }

    @MethodDescription(example = @Example("item('evilcraft:dark_gem')"))
    public boolean removeByInput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:leather')"))
    public boolean removeByOutput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> {

        private static final Fluid bloodFluid = FluidRegistry.getFluid("evilcraftblood");

        @Property(comp = @Comp(gte = 0, lte = 3))
        private int tier;
        @Property(comp = @Comp(gte = 0))
        private int duration;
        @Property(comp = @Comp(gte = 0))
        private float xp;

        @RecipeBuilderMethodDescription
        public RecipeBuilder tier(int tier) {
            this.tier = tier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder xp(float xp) {
            this.xp = xp;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "fluidInput")
        public RecipeBuilder blood(int amount) {
            this.fluidInput.add(new FluidStack(bloodFluid, amount));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder fluidInput(int amount) {
            this.fluidInput.add(new FluidStack(bloodFluid, amount));
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EvilCraft Blood Infuser Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(tier < 0 || tier > 3, "tier must be between 0 and 3, yet it was {}", tier);
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(xp < 0, "xp must be a non negative integer, yet it was {}", xp);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties> register() {
            if (!validate()) return null;
            IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties> recipe =
                    org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().registerRecipe(
                            new IngredientFluidStackAndTierRecipeComponent(input.get(0).toMcIngredient(), fluidInput.get(0), tier),
                            new IngredientRecipeComponent(output.get(0)),
                            new DurationXpRecipeProperties(duration, xp)
                    );
            ModSupport.EVILCRAFT.get().bloodInfuser.addScripted(recipe);
            return recipe;
        }
    }
}
