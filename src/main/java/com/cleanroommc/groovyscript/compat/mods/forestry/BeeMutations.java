package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.forestry.BeeRootAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.IMutationBuilder;
import forestry.apiculture.genetics.BeeMutation;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import forestry.modules.ForestryModuleUids;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class BeeMutations extends ForestryRegistry<IBeeMutation> {

    public BeeMutations() {
        super(Alias.generateOfClassAnd(BeeMutations.class, "Mutations"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(BeeRootAccessor.getBeeMutations()::remove);
        restoreFromBackup().forEach(BeeRootAccessor.getBeeMutations()::add);
    }

    @Override
    @GroovyBlacklist
    public boolean isEnabled() {
        return ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.APICULTURE);
    }

    public IBeeMutation add(AlleleBeeSpecies output, AlleleBeeSpecies a, AlleleBeeSpecies b, double chance,
                            @Nullable Function<IBeeMutationBuilder, IMutationBuilder> requirement) {
        BeeMutation mutation = new BeeMutation(a, b, Objects.requireNonNull(BeeManager.beeRoot).getTemplate(output), (int) Math.round(100 * chance));
        if (requirement != null) mutation = (BeeMutation) requirement.apply(mutation);
        add(mutation);
        return mutation;
    }

    public IBeeMutation add(AlleleBeeSpecies output, AlleleBeeSpecies a, AlleleBeeSpecies b, double chance) {
        return add(output, a, b, chance, null);
    }

    public void add(IBeeMutation mutation) {
        if (mutation == null) return;
        addScripted(mutation);
        BeeRootAccessor.getBeeMutations().add(mutation);
    }

    public boolean remove(IBeeMutation mutation) {
        if (mutation == null) return false;
        addBackup(mutation);
        return BeeRootAccessor.getBeeMutations().remove(mutation);
    }

    public boolean removeByOutput(AlleleBeeSpecies species) {
        if (BeeRootAccessor.getBeeMutations().removeIf(mutation -> {
            boolean found = Arrays.equals(mutation.getTemplate(), Objects.requireNonNull(BeeManager.beeRoot).getTemplate(species));
            if (found) addBackup(mutation);
            return found;
        })) return true;

        GroovyLog.msg("Error removing bee mutation")
                .add("could not find bee mutation with output {}", species)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        BeeRootAccessor.getBeeMutations().forEach(this::addBackup);
        BeeRootAccessor.getBeeMutations().clear();
    }

    public SimpleObjectStream<IBeeMutation> streamMutations() {
        return new SimpleObjectStream<>(BeeRootAccessor.getBeeMutations()).setRemover(this::remove);
    }
}
