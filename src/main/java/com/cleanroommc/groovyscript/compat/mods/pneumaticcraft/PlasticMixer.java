package com.cleanroommc.groovyscript.compat.mods.pneumaticcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.pneumaticcraft.PlasticMixerRecipeAccessor;
import com.cleanroommc.groovyscript.core.mixin.pneumaticcraft.PlasticMixerRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import me.desht.pneumaticcraft.common.recipes.PlasticMixerRegistry;
import org.jetbrains.annotations.Nullable;

@RegistryDescription(
        admonition = {
                @Admonition(value = "groovyscript.wiki.pneumaticcraft.plastic_mixer.note0", type = Admonition.Type.WARNING),
                @Admonition(value = "groovyscript.wiki.pneumaticcraft.plastic_mixer.note1", type = Admonition.Type.DANGER)
        }
)
public class PlasticMixer extends VirtualizedRegistry<PlasticMixerRegistry.PlasticMixerRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).output(item('minecraft:clay')).allowMelting().allowSolidifying().requiredTemperature(323)"),
            @Example(".fluidInput(fluid('water') * 50).output(item('minecraft:sapling')).allowSolidifying().requiredTemperature(298).meta(-1)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    private static PlasticMixerRegistryAccessor getInstance() {
        return (PlasticMixerRegistryAccessor) (Object) PlasticMixerRegistry.INSTANCE;
    }

    @Override
    public void onReload() {
        getInstance().getRecipes().removeAll(removeScripted());
        getInstance().getRecipes().addAll(restoreFromBackup());
    }

    @Override
    public void afterScriptLoad() {
        getInstance().getValidFluids().clear();
        getInstance().getValidItems().clear();
        for (PlasticMixerRegistry.PlasticMixerRecipe recipe : getInstance().getRecipes()) {
            getInstance().getValidFluids().add(recipe.getFluidStack().getFluid().getName());
            getInstance().getValidItems().put(recipe.getItemStack().getItem(), recipe.allowMelting());
        }
    }

    public void add(PlasticMixerRegistry.PlasticMixerRecipe recipe) {
        getInstance().getRecipes().add(recipe);
        addScripted(recipe);
    }

    public boolean remove(PlasticMixerRegistry.PlasticMixerRecipe recipe) {
        addBackup(recipe);
        return getInstance().getRecipes().remove(recipe);
    }

    @MethodDescription(example = @Example(value = "fluid('plastic')", commented = true))
    public boolean removeByFluid(IIngredient fluid) {
        return getInstance().getRecipes().removeIf(entry -> {
            if (fluid.test(entry.getFluidStack())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example(value = "item('pneumaticcraft:plastic')", commented = true))
    public boolean removeByItem(IIngredient item) {
        return getInstance().getRecipes().removeIf(entry -> {
            if (item.test(entry.getItemStack())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        getInstance().getRecipes().forEach(this::addBackup);
        getInstance().getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<PlasticMixerRegistry.PlasticMixerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(getInstance().getRecipes()).setRemover(this::remove);
    }

    @Property(property = "output", valid = @Comp("1"))
    @Property(property = "fluidInput", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<PlasticMixerRegistry.PlasticMixerRecipe> {

        @Property
        private int meta;
        @Property
        private int requiredTemperature;
        @Property
        private boolean allowMelting;
        @Property
        private boolean allowSolidifying;
        @Property
        private boolean useDye;

        @RecipeBuilderMethodDescription
        public RecipeBuilder meta(int meta) {
            this.meta = meta;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder requiredTemperature(int requiredTemperature) {
            this.requiredTemperature = requiredTemperature;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder allowMelting() {
            this.allowMelting = !allowMelting;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder allowMelting(boolean allowMelting) {
            this.allowMelting = allowMelting;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder allowSolidifying() {
            this.allowSolidifying = !allowSolidifying;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder allowSolidifying(boolean allowSolidifying) {
            this.allowSolidifying = allowSolidifying;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder useDye() {
            this.useDye = !useDye;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder useDye(boolean useDye) {
            this.useDye = useDye;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding PneumaticCraft Plastic Mixer recipe";
        }

        @Override
        protected int getMaxInput() {
            // PnC modifies the recipe to only consume 1 item
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(!allowMelting && !allowSolidifying, "neither allowMelting or allowSolidifying are enabled, one of the two must be enabled");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable PlasticMixerRegistry.PlasticMixerRecipe register() {
            if (!validate()) return null;
            PlasticMixerRegistry.PlasticMixerRecipe recipe = PlasticMixerRecipeAccessor.createPlasticMixerRecipe(fluidInput.get(0), output.get(0), requiredTemperature, allowMelting, allowSolidifying, useDye, meta);
            ModSupport.PNEUMATIC_CRAFT.get().plasticMixer.add(recipe);
            return recipe;
        }
    }

}
