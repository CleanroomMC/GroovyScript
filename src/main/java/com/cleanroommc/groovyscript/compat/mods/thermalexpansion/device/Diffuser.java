package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.inventory.ComparableItemStack;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.DiffuserManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class Diffuser extends VirtualizedRegistry<Diffuser.DiffuserRecipe> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> {
            DiffuserManagerAccessor.getReagentAmpMap().keySet().removeIf(r -> r.equals(recipe.stack()));
            DiffuserManagerAccessor.getReagentDurMap().keySet().removeIf(r -> r.equals(recipe.stack()));
        });
        restoreFromBackup().forEach(r -> {
            DiffuserManagerAccessor.getReagentAmpMap().put(r.stack(), r.amplifier());
            DiffuserManagerAccessor.getReagentDurMap().put(r.stack(), r.duration());
        });
    }

    public void add(DiffuserRecipe recipe) {
        DiffuserManagerAccessor.getReagentAmpMap().put(recipe.stack(), recipe.amplifier());
        DiffuserManagerAccessor.getReagentDurMap().put(recipe.stack(), recipe.duration());
        addScripted(recipe);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:clay'), 2, 30"))
    public void add(ItemStack stack, int amplifier, int duration) {
        add(new DiffuserRecipe(new ComparableItemStack(stack), amplifier, duration));
    }

    public boolean remove(ComparableItemStack stack) {
        if (DiffuserManagerAccessor.getReagentAmpMap().containsKey(stack) && DiffuserManagerAccessor.getReagentDurMap().containsKey(stack)) {
            addBackup(new DiffuserRecipe(stack));
            DiffuserManagerAccessor.getReagentAmpMap().remove(stack);
            DiffuserManagerAccessor.getReagentDurMap().remove(stack);
            return true;
        }
        return false;
    }

    public boolean remove(DiffuserRecipe recipe) {
        if (DiffuserManagerAccessor.getReagentAmpMap().containsKey(recipe.stack()) && DiffuserManagerAccessor.getReagentDurMap().containsKey(recipe.stack())) {
            addBackup(new DiffuserRecipe(recipe.stack()));
            DiffuserManagerAccessor.getReagentAmpMap().remove(recipe.stack());
            DiffuserManagerAccessor.getReagentDurMap().remove(recipe.stack());
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:redstone')"))
    public boolean remove(ItemStack input) {
        return remove(new ComparableItemStack(input));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DiffuserRecipe> streamRecipes() {
        List<DiffuserRecipe> list = DiffuserManagerAccessor.getReagentAmpMap()
                .keySet()
                .stream()
                .filter(DiffuserManagerAccessor.getReagentDurMap()::containsKey)
                .map(DiffuserRecipe::new)
                .collect(Collectors.toList());
        return new SimpleObjectStream<>(list).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        DiffuserManagerAccessor.getReagentAmpMap()
                .keySet()
                .stream()
                .filter(DiffuserManagerAccessor.getReagentDurMap()::containsKey)
                .map(DiffuserRecipe::new)
                .forEach(this::addBackup);
        DiffuserManagerAccessor.getReagentAmpMap().clear();
        DiffuserManagerAccessor.getReagentDurMap().clear();
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static final class DiffuserRecipe {

        private final ComparableItemStack stack;
        private final int amplifier;
        private final int duration;

        public DiffuserRecipe(ComparableItemStack stack, int amplifier, int duration) {
            this.stack = stack;
            this.amplifier = amplifier;
            this.duration = duration;
        }

        public DiffuserRecipe(ComparableItemStack stack) {
            this(stack, DiffuserManagerAccessor.getReagentAmpMap().get(stack), DiffuserManagerAccessor.getReagentDurMap().get(stack));
        }

        public ComparableItemStack stack() {
            return stack;
        }

        public int amplifier() {
            return amplifier;
        }

        public int duration() {
            return duration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DiffuserRecipe) obj;
            return Objects.equals(this.stack, that.stack) && this.amplifier == that.amplifier && this.duration == that.duration;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack, amplifier, duration);
        }

        @Override
        public String toString() {
            return "DiffuserRecipe[" + "stack=" + stack + ", " + "amplifier=" + amplifier + ", " + "duration=" + duration + ']';
        }

    }
}
