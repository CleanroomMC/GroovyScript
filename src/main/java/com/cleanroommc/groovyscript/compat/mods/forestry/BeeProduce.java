package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.forestry.recipe.BeeProduct;
import com.cleanroommc.groovyscript.core.mixin.forestry.AlleleRegistryAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import forestry.api.core.ForestryAPI;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import forestry.modules.ForestryModuleUids;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public class BeeProduce extends ForestryRegistry<BeeProduct> {

    public BeeProduce() {
        super(Alias.generateOfClassAnd(BeeProduce.class, "Produce"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(this::removeFromBee);
        restoreFromBackup().forEach(this::addToBee);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public void addToBee(BeeProduct product) {
        (product.special ? product.species.getSpecialtyChances() : product.species.getProductChances()).put(product.item, product.chance);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public boolean removeFromBee(BeeProduct product) {
        return (product.special ? product.species.getSpecialtyChances() : product.species.getProductChances()).remove(product.item, product.chance);
    }

    @Override
    @GroovyBlacklist
    public boolean isEnabled() {
        return ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.APICULTURE);
    }

    public BeeProduct add(AlleleBeeSpecies species, ItemStack output, float chance, boolean specialty) {
        BeeProduct product = new BeeProduct(species, output, chance, specialty);
        add(product);
        return product;
    }

    public BeeProduct add(AlleleBeeSpecies species, ItemStack output, float chance) {
        return add(species, output, chance, false);
    }

    public void add(BeeProduct recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        addToBee(recipe);
    }

    public boolean remove(BeeProduct recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return removeFromBee(recipe);
    }

    public boolean removeProduct(AlleleBeeSpecies species, IIngredient output) {
        if (species.getProductChances().entrySet().removeIf(entry -> {
            boolean found = output.test(entry.getKey());
            if (found) addBackup(new BeeProduct(species, entry.getKey(), entry.getValue(), false));
            return found;
        })) return true;

        GroovyLog.msg("Error removing product for bee")
                .add("could not find product {} for species {}", output, species)
                .error()
                .post();
        return false;
    }

    public boolean removeSpecialty(AlleleBeeSpecies species, IIngredient output) {
        if (species.getSpecialtyChances().entrySet().removeIf(entry -> {
            boolean found = output.test(entry.getKey());
            if (found) addBackup(new BeeProduct(species, entry.getKey(), entry.getValue(), true));
            return found;
        })) return true;

        GroovyLog.msg("Error removing specialty product for bee")
                .add("could not find specialty product {} for species {}", output, species)
                .error()
                .post();
        return false;
    }

    public void removeAll(AlleleBeeSpecies species) {
        species.getProductChances().entrySet().removeIf(entry -> {
            BeeProduct product = new BeeProduct(species, entry.getKey(), entry.getValue(), false);
            addBackup(product);
            return true;
        });
        species.getSpecialtyChances().entrySet().removeIf(entry -> {
            BeeProduct product = new BeeProduct(species, entry.getKey(), entry.getValue(), true);
            addBackup(product);
            return true;
        });
    }

    public void removeAll() {
        ((AlleleRegistryAccessor) AlleleManager.alleleRegistry).getAlleleMap().forEach((uid, allele) -> {
            if (allele instanceof AlleleBeeSpecies alleleBeeSpecies) removeAll(alleleBeeSpecies);
        });
    }
}
