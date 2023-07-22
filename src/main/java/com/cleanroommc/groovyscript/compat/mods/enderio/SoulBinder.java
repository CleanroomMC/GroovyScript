package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.enderio.SimpleRecipeGroupHolderAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.helper.recipe.RecipeName;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.soul.BasicSoulBinderRecipe;
import crazypants.enderio.base.recipe.soul.ISoulBinderRecipe;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoulBinder extends VirtualizedRegistry<ISoulBinderRecipe> {

    public SoulBinder() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(ISoulBinderRecipe recipe) {
        MachineRecipeRegistry.instance.registerRecipe(recipe);
        addScripted(recipe);
    }

    public boolean remove(ISoulBinderRecipe recipe) {
        if (recipe == null) return false;
        MachineRecipeRegistry.instance.removeRecipe(recipe);
        addBackup(recipe);
        return true;
    }

    public void remove(ItemStack output) {
        List<ISoulBinderRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).values()) {
            if (OreDictionary.itemMatches(output, ((ISoulBinderRecipe) recipe).getOutputStack(), false)) {
                recipes.add((ISoulBinderRecipe) recipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.get().error("No Soul Binder recipe found for " + output.getDisplayName());
        } else {
            for (ISoulBinderRecipe recipe : recipes) {
                MachineRecipeRegistry.instance.removeRecipe(recipe);
                addBackup(recipe);
            }
        }
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(MachineRecipeRegistry.instance::removeRecipe);
        restoreFromBackup().forEach(MachineRecipeRegistry.instance::registerRecipe);
    }

    public SimpleObjectStream<ISoulBinderRecipe> streamRecipes() {
        return new SimpleObjectStream<>((Collection<ISoulBinderRecipe>) MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).values())
                .setRemover(this::remove);
    }

    public void removeAll() {
        MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).forEach((r, l) -> addBackup((ISoulBinderRecipe) l));
        ((SimpleRecipeGroupHolderAccessor) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.SOULBINDER)).getRecipes().clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BasicSoulBinderRecipe> {

        private String name;
        private int xp;
        private int energy;
        private final NNList<ResourceLocation> entities = new NNList<>();
        private final List<String> entityErrors = new ArrayList<>();

        public RecipeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder entitySoul(String entity) {
            ResourceLocation rl = new ResourceLocation(entity);
            if (EntityList.getClass(rl) == null) {
                entityErrors.add(entity);
            } else {
                entities.add(rl);
            }
            return this;
        }

        public RecipeBuilder entitySoul(String... entities) {
            for (String entity : entities) {
                entitySoul(entity);
            }
            return this;
        }

        public RecipeBuilder entitySoul(Collection<String> entities) {
            for (String entity : entities) {
                entitySoul(entity);
            }
            return this;
        }

        public RecipeBuilder entity(EntityEntry entity) {
            entities.add(entity.getRegistryName());
            return this;
        }

        public RecipeBuilder entity(EntityEntry... entities) {
            for (EntityEntry entity : entities) {
                entity(entity);
            }
            return this;
        }

        public RecipeBuilder entity(Collection<EntityEntry> entities) {
            for (EntityEntry entity : entities) {
                entity(entity);
            }
            return this;
        }

        public RecipeBuilder xp(int xp) {
            this.xp = xp;
            return this;
        }

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding EnderIO Soul Binder recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (!entityErrors.isEmpty()) {
                for (String error : entityErrors) {
                    msg.add("could not find entity with name {}", error);
                }
            }
            if (energy <= 0) energy = 5000;
            if (xp <= 0) xp = 2;
            if (name == null || name.isEmpty()) name = RecipeName.generate();
        }

        @Override
        public @Nullable BasicSoulBinderRecipe register() {
            if (!validate()) return null;
            BasicSoulBinderRecipe recipe = new BasicSoulBinderRecipe(
                    input.get(0).getMatchingStacks()[0],
                    output.get(0),
                    energy,
                    xp,
                    name,
                    RecipeLevel.IGNORE,
                    entities,
                    new BasicSoulBinderRecipe.OutputFilter() {
                    });
            ModSupport.ENDER_IO.get().soulBinder.add(recipe);
            return recipe;
        }
    }
}
