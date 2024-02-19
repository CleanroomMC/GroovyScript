package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
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

@RegistryDescription
public class BloodInfuser extends VirtualizedRegistry<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> {

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
    public void onReload() {
        removeScripted().forEach(org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('evilcraft:dark_gem')"))
    public boolean removeByInput(ItemStack input) {
        return org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:leather')"))
    public boolean removeByOutput(ItemStack input) {
        return org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> {

        private static final Fluid bloodFluid = FluidRegistry.getFluid("evilcraftblood");

        @Property(valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "3", type = Comp.Type.LTE)})
        private int tier = 0;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private int duration = 0;
        @Property(valid = @Comp(value = "0", type = Comp.Type.GTE))
        private float xp = 0;

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
            ModSupport.EVILCRAFT.get().bloodInfuser.add(recipe, false);
            return recipe;
        }
    }
}
