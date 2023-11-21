package com.cleanroommc.groovyscript.compat.mods.woot;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.woot.CustomDropAccessor;
import com.cleanroommc.groovyscript.core.mixin.woot.CustomDropsRepositoryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ipsis.Woot;
import ipsis.woot.util.WootMobName;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Drops extends VirtualizedRegistry<Object> {

    public Drops() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        restoreFromBackup().forEach(drop -> ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops().add(drop));
        removeScripted().forEach(drop -> ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops()
                .removeIf(d -> areCustomDropsEqual((CustomDropAccessor) d, (CustomDropAccessor) drop))
        );
    }

    public void add(WootMobName wootMobName, ItemStack itemStack, List<Integer> chances, List<Integer> sizes) {
        Woot.customDropsRepository.addDrop(wootMobName, itemStack, chances, sizes);
        // get the drop we just added, but painfully
        Optional<Object> recipe = ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops()
                .stream()
                .filter(drop -> areCustomDropsEqual((CustomDropAccessor) drop, wootMobName, itemStack, chances, sizes))
                .findFirst();
        if (!recipe.isPresent()) {
            GroovyLog.msg("Error adding entry to Woot Custom Drops Repository with name {}", wootMobName).error().post();
            return;
        }
        addScripted(recipe.get());
    }

    public boolean remove(Object drop) {
        return ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops().removeIf(d -> {
            if (areCustomDropsEqual((CustomDropAccessor) d, (CustomDropAccessor) drop)) {
                addBackup(d);
                return true;
            }
            return false;
        });
    }

    public boolean removeByEntity(WootMobName name) {
        return ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops().removeIf(d -> {
            if (((CustomDropAccessor) d).getWootMobName().equals(name)) {
                addBackup(d);
                return true;
            }
            return false;
        });
    }

    public boolean removeByEntity(EntityEntry entity) {
        return removeByEntity(new WootMobName(entity.getName()));
    }

    public boolean removeByEntity(String name) {
        return removeByEntity(new WootMobName(name));
    }

    public boolean removeByEntity(String name, String tag) {
        return removeByEntity(new WootMobName(name, tag));
    }

    public boolean removeByOutput(ItemStack output) {
        return ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops().removeIf(d -> {
            if (ItemStack.areItemStacksEqual(((CustomDropAccessor) d).getItemStack(), output)) {
                addBackup(d);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops().forEach(this::addBackup);
        ((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops().clear();
    }

    public SimpleObjectStream<Object> streamRecipes() {
        return new SimpleObjectStream<>(((CustomDropsRepositoryAccessor) Woot.customDropsRepository).getDrops())
                .setRemover(this::remove);
    }

    private boolean areCustomDropsEqual(CustomDropAccessor target, CustomDropAccessor other) {
        return target.getWootMobName().equals(other.getWootMobName()) &&
               ItemStack.areItemStacksEqual(target.getItemStack(), other.getItemStack()) &&
               target.getChanceMap().equals(other.getChanceMap()) &&
               target.getSizeMap().equals(other.getSizeMap());
    }

    private boolean areCustomDropsEqual(CustomDropAccessor target, WootMobName wootMobName, ItemStack itemStack, List<Integer> chances, List<Integer> sizes) {
        return target.getWootMobName().equals(wootMobName) &&
               ItemStack.areItemStacksEqual(target.getItemStack(), itemStack) &&
               target.getChanceMap().values().containsAll(chances) &&
               target.getSizeMap().values().containsAll(sizes);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<ItemStack> {

        private WootMobName name;
        private final List<Integer> chance = new ArrayList<>();
        private final List<Integer> size = new ArrayList<>();

        public RecipeBuilder name(WootMobName name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder name(EntityEntry entity) {
            this.name = new WootMobName(entity.getName());
            return this;
        }

        public RecipeBuilder name(String name) {
            this.name = new WootMobName(name);
            return this;
        }

        public RecipeBuilder name(String name, String tag) {
            this.name = new WootMobName(name, tag);
            return this;
        }

        public RecipeBuilder chance(int chance) {
            this.chance.add(chance);
            return this;
        }

        public RecipeBuilder chance(int... chances) {
            for (int chance : chances) {
                chance(chance);
            }
            return this;
        }

        public RecipeBuilder chance(Collection<Integer> chances) {
            for (int chance : chances) {
                chance(chance);
            }
            return this;
        }

        public RecipeBuilder size(int size) {
            this.size.add(size);
            return this;
        }

        public RecipeBuilder size(int... sizes) {
            for (int size : sizes) {
                size(size);
            }
            return this;
        }

        public RecipeBuilder size(Collection<Integer> sizes) {
            for (int size : sizes) {
                size(size);
            }
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Woot custom drops";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            msg.add(name == null || !name.isValid(), "name must be defined and a valid name, yet it was {}", name);
            msg.add(chance.size() != 4, "chance must have exactly 4 entries, but found {}", chance.size());
            msg.add(size.size() != 4, "size must have exactly 4 entries, but found {}", size.size());
        }

        @Override
        public @Nullable ItemStack register() {
            if (!validate()) return null;
            ModSupport.WOOT.get().drops.add(name, output.get(0), chance, size);
            return null;
        }
    }

}
