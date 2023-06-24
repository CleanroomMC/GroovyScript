package com.cleanroommc.groovyscript.compat.mods.evilcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
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
import org.cyclops.evilcraft.core.recipe.custom.DurationXpRecipeProperties;
import org.cyclops.evilcraft.core.recipe.custom.IngredientFluidStackAndTierRecipeComponent;
import org.jetbrains.annotations.Nullable;

public class BloodInfuser extends VirtualizedRegistry<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> {

    public BloodInfuser() {
        super();
    }

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

    public boolean removeByInput(ItemStack input) {
        return org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public boolean removeByOutput(ItemStack input) {
        return org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    public SimpleObjectStream<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(org.cyclops.evilcraft.block.BloodInfuser.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<IngredientFluidStackAndTierRecipeComponent, IngredientRecipeComponent, DurationXpRecipeProperties>> {

        private static final Fluid bloodFluid = FluidRegistry.getFluid("evilcraftblood");

        private int tier = 0;
        private int duration = 0;
        private float xp = 0;

        public RecipeBuilder tier(int tier) {
            this.tier = tier;
            return this;
        }

        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public RecipeBuilder xp(float xp) {
            this.xp = xp;
            return this;
        }

        public RecipeBuilder blood(int amount) {
            this.fluidInput.add(new FluidStack(bloodFluid, amount));
            return this;
        }

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
