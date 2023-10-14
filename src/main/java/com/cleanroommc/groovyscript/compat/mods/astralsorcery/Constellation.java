package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationBaseAccessor;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationMapEffectRegistryAccessor;
import com.cleanroommc.groovyscript.core.mixin.astralsorcery.ConstellationRegistryAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.Iterables;
import hellfirepvp.astralsorcery.common.constellation.*;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CapeAttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationPaperRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Constellation extends VirtualizedRegistry<IConstellation> {

    private final HashMap<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsAdded = new HashMap<>();
    private final HashMap<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsRemoved = new HashMap<>();
    private final HashMap<IConstellation, List<ItemHandle>> signatureItemsAdded = new HashMap<>();
    private final HashMap<IConstellation, List<ItemHandle>> signatureItemsRemoved = new HashMap<>();

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(c -> {
            if (ConstellationMapEffectRegistryAccessor.getEffectRegistry() != null) {
                ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(c);
            }
            ConstellationRegistryAccessor.getConstellationList().removeIf(registeredConstellation -> c.getSimpleName().equals(registeredConstellation.getSimpleName()));
            if (c instanceof IMajorConstellation && ConstellationRegistryAccessor.getMajorConstellations() != null) {
                ConstellationRegistryAccessor.getMajorConstellations().removeIf(registeredConstellation -> c.getSimpleName().equals(registeredConstellation.getSimpleName()));
            }
            if (c instanceof IMinorConstellation && ConstellationRegistryAccessor.getMinorConstellations() != null) {
                ConstellationRegistryAccessor.getMinorConstellations().removeIf(registeredConstellation -> c.getSimpleName().equals(registeredConstellation.getSimpleName()));
            }
            if (c instanceof IWeakConstellation && ConstellationRegistryAccessor.getWeakConstellations() != null) {
                ConstellationRegistryAccessor.getWeakConstellations().removeIf(registeredConstellation -> c.getSimpleName().equals(registeredConstellation.getSimpleName()));
            }
        });
        restoreFromBackup().forEach(ConstellationRegistry::registerConstellation);

        this.constellationMapEffectsRemoved.forEach((constellation, effect) -> ConstellationMapEffectRegistry.registerMapEffect(constellation, effect.enchantmentEffects, effect.potionEffects));
        this.constellationMapEffectsAdded.forEach((constellation, effect) -> ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(constellation));
        this.signatureItemsAdded.forEach((constellation, items) -> items.forEach(item -> ((ConstellationBaseAccessor) constellation).getSignatureItems().remove(item)));
        this.signatureItemsRemoved.forEach((constellation, items) -> items.forEach(constellation::addSignatureItem));

        this.constellationMapEffectsAdded.clear();
        this.constellationMapEffectsRemoved.clear();
        this.signatureItemsAdded.clear();
        this.signatureItemsRemoved.clear();
    }

    public void afterScriptLoad() {
        ConstellationRegistryAccessor.getConstellationList().forEach(constellation -> {
            if (!(constellation instanceof IMinorConstellation)) updateDefaultCapeRecipe(constellation);
            updateDefaultPaperRecipe(constellation);
        });
    }

    private void add(IConstellation constellation) {
        addScripted(constellation);
        ConstellationRegistry.registerConstellation(constellation);
    }

    private boolean remove(IConstellation constellation) {
        if (ConstellationRegistryAccessor.getConstellationList() == null) return false;
        addBackup(constellation);
        this.removeConstellationMapEffect(constellation);
        ConstellationRegistryAccessor.getConstellationList().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        if (constellation instanceof IMajorConstellation && ConstellationRegistryAccessor.getMajorConstellations() != null)
            return ConstellationRegistryAccessor.getMajorConstellations().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        if (constellation instanceof IMinorConstellation && ConstellationRegistryAccessor.getMinorConstellations() != null)
            return ConstellationRegistryAccessor.getMinorConstellations().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        if (constellation instanceof IWeakConstellation && ConstellationRegistryAccessor.getWeakConstellations() != null)
            return ConstellationRegistryAccessor.getWeakConstellations().removeIf(registeredConstellation -> constellation.getSimpleName().equals(registeredConstellation.getSimpleName()));
        return false;
    }

    public SimpleObjectStream<IConstellation> streamConstellations() {
        return new SimpleObjectStream<>(ConstellationRegistryAccessor.getConstellationList())
                .setRemover(this::remove);
    }

    public void removeAll() {
        ConstellationRegistryAccessor.getConstellationList().forEach(this::addBackup);
        ConstellationRegistryAccessor.getConstellationList().clear();
        ConstellationRegistryAccessor.getMajorConstellations().clear();
        ConstellationRegistryAccessor.getMinorConstellations().clear();
        ConstellationRegistryAccessor.getWeakConstellations().clear();
    }

    private void addConstellationMapEffect(IConstellation constellation, List<ConstellationMapEffectRegistry.EnchantmentMapEffect> enchantmentEffectList, List<ConstellationMapEffectRegistry.PotionMapEffect> potionEffectList) {
        this.constellationMapEffectsAdded.put(constellation, ConstellationMapEffectRegistry.registerMapEffect(constellation, enchantmentEffectList, potionEffectList));
    }

    private void removeConstellationMapEffect(IConstellation constellation) {
        if (ConstellationMapEffectRegistryAccessor.getEffectRegistry() == null) return;
        if (ConstellationMapEffectRegistryAccessor.getEffectRegistry().containsKey(constellation)) {
            this.constellationMapEffectsRemoved.put(constellation, ConstellationMapEffectRegistryAccessor.getEffectRegistry().get(constellation));
            ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(constellation);
        }
    }

    public void removeAllConstellationMapEffect() {
        ConstellationRegistryAccessor.getConstellationList().forEach(constellation -> {
            this.constellationMapEffectsRemoved.put(constellation, ConstellationMapEffectRegistryAccessor.getEffectRegistry().get(constellation));
            ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(constellation);
        });
    }

    private void addSignatureItem(IConstellation constellation, ItemHandle item) {
        constellation.addSignatureItem(item);
    }

    public void addSignatureItem(IConstellation constellation, IIngredient ing) {
        if (!this.signatureItemsAdded.containsKey(constellation)) {
            this.signatureItemsAdded.put(constellation, new ArrayList<>());
        }
        this.signatureItemsAdded.get(constellation).add(AstralSorcery.toItemHandle(ing));
        constellation.addSignatureItem(AstralSorcery.toItemHandle(ing));
    }

    public void addSignatureItem(IConstellation constellation, IIngredient... ings) {
        for (IIngredient ing : ings) {
            this.addSignatureItem(constellation, ing);
        }
    }

    public void addSignatureItem(IConstellation constellation, Collection<IIngredient> ings) {
        ings.forEach(ing -> this.addSignatureItem(constellation, ing));
    }

    public void removeSignatureItems(IConstellation constellation) {
        this.signatureItemsRemoved.put(constellation, ((ConstellationBaseAccessor) constellation).getSignatureItems());
        ((ConstellationBaseAccessor) constellation).getSignatureItems().clear();
    }

    public void removeAllSignatureItems() {
        ConstellationRegistryAccessor.getConstellationList().forEach(constellation -> {
            this.signatureItemsRemoved.put(constellation, ((ConstellationBaseAccessor) constellation).getSignatureItems());
            ((ConstellationBaseAccessor) constellation).getSignatureItems().clear();
        });
    }

    private void updateDefaultPaperRecipe(IConstellation constellation) {
        ItemHandle first = (ItemHandle) Iterables.getFirst(constellation.getConstellationSignatureItems(), (Object) null);
        AccessibleRecipeAdapater shapedPaper = ShapedRecipe.Builder.newShapedRecipe("internal/altar/constellationpaper/" + constellation.getSimpleName().toLowerCase(), ItemsAS.constellationPaper)
                .addPart(ItemCraftingComponent.MetaType.PARCHMENT.asStack(), ShapedRecipeSlot.CENTER)
                .addPart(Items.FEATHER, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.getDyeOreDict(EnumDyeColor.BLACK), ShapedRecipeSlot.LOWER_CENTER)
                .addPart(OreDictAlias.ITEM_STARMETAL_DUST, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT)
                .unregisteredAccessibleShapedRecipe();
        ConstellationPaperRecipe recipe = new ConstellationPaperRecipe(shapedPaper, constellation);
        recipe.setInnerTraitItem(first, TraitRecipe.TraitRecipeSlot.values());

        for (ItemHandle s : constellation.getConstellationSignatureItems()) {
            recipe.addOuterTraitItem(s);
        }

        AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.TRAIT_CRAFT)
                .removeIf(r -> new ResourceLocation("astralsorcery:shaped/internal/altar/constellationpaper/" + constellation.getSimpleName().toLowerCase()).equals(r.getNativeRecipe().getRegistryName()));
        AltarRecipeRegistry.registerAltarRecipe(recipe);
        RecipesAS.paperCraftingRecipes.put(constellation, recipe);
    }

    private void updateDefaultCapeRecipe(IConstellation constellation) {
        ItemHandle first = (ItemHandle) Iterables.getFirst(constellation.getConstellationSignatureItems(), (Object) null);
        AccessibleRecipeAdapater ar = ShapedRecipe.Builder.newShapedRecipe("internal/cape/att/" + constellation.getSimpleName().toLowerCase(), ItemsAS.armorImbuedCape)
                .addPart(ItemsAS.armorImbuedCape, ShapedRecipeSlot.CENTER)
                .addPart(first, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LOWER_CENTER)
                .unregisteredAccessibleShapedRecipe();
        CapeAttunementRecipe recipe = new CapeAttunementRecipe(constellation, ar);
        recipe.setInnerTraitItem(OreDictAlias.ITEM_STARMETAL_DUST, TraitRecipe.TraitRecipeSlot.values());

        for (ItemHandle s : constellation.getConstellationSignatureItems()) {
            recipe.addOuterTraitItem(s);
        }

        AltarRecipeRegistry.recipes.get(TileAltar.AltarLevel.TRAIT_CRAFT)
                .removeIf(r -> new ResourceLocation("astralsorcery:shaped/internal/cape/att/" + constellation.getSimpleName().toLowerCase()).equals(r.getNativeRecipe().getRegistryName()));
        AltarRecipeRegistry.registerAltarRecipe(recipe);
        RecipesAS.capeCraftingRecipes.put(constellation, recipe);
    }

    public ConstellationBuilder constellationBuilder() {
        return new ConstellationBuilder();
    }

    public ConstellationMapEffectBuilder constellationMapEffectBuilder() {
        return new ConstellationMapEffectBuilder();
    }

    public SignatureItemsHelper signatureItems() {
        return new SignatureItemsHelper();
    }

    public static class ConstellationBuilder {

        private final ArrayList<Point2PointConnection> connections = new ArrayList<>();
        private final ArrayList<MoonPhase> phases = new ArrayList<>();
        private String name;
        private Color color = null;
        private ConstellationBuilder.Type type;

        public ConstellationBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ConstellationBuilder color(int color) {
            this.color = new Color(color);
            return this;
        }

        public ConstellationBuilder major() {
            this.type = Type.MAJOR;
            return this;
        }

        public ConstellationBuilder minor() {
            this.type = Type.MINOR;
            return this;
        }

        public ConstellationBuilder weak() {
            this.type = Type.WEAK;
            return this;
        }

        public ConstellationBuilder connection(int x1, int y1, int x2, int y2) {
            Point2PointConnection connection = new Point2PointConnection(x1, y1, x2, y2);
            if (!connections.contains(connection)) {
                connections.add(connection);
            }
            return this;
        }

        public ConstellationBuilder phase(MoonPhase moonPhase) {
            this.phases.add(moonPhase);
            return this;
        }

        public ConstellationBuilder phase(MoonPhase... moonPhases) {
            this.phases.addAll(Arrays.asList(moonPhases));
            return this;
        }

        public ConstellationBuilder phase(Collection<MoonPhase> moonPhases) {
            this.phases.addAll(moonPhases);
            return this;
        }

        private boolean validate() {
            ArrayList<String> errors = new ArrayList<>();

            if (this.name == null || this.name.equals(""))
                errors.add("name must be provided");
            if (this.connections.equals(new ArrayList<>()))
                errors.add("connections must not be empty");
            if (this.type.equals(Type.MINOR) && this.phases.size() == 0)
                errors.add("minor constellations require at least one moon phase");

            if (!errors.isEmpty()) {
                GroovyLog.Msg errorOut = GroovyLog.msg("Error adding Astral Sorcery Constellation: ");
                errors.forEach(errorOut::add);
                errorOut.error().post();
                return false;
            }

            if (this.color == null) {
                switch (this.type) {
                    case MAJOR:
                        this.color = new Color(40, 67, 204);
                        break;
                    case WEAK:
                        this.color = new Color(67, 44, 176);
                        break;
                    case MINOR:
                        this.color = new Color(93, 25, 127);
                }
            }

            return true;
        }

        public void register() {
            if (!validate()) return;
            IConstellation constellation;
            switch (this.type) {
                case MAJOR:
                    constellation = new ConstellationBase.Major(name, color);
                    break;
                case WEAK:
                    constellation = new ConstellationBase.Minor(name, color, phases.toArray(new MoonPhase[0]));
                    break;
                case MINOR:
                    constellation = new ConstellationBase.Weak(name, color);
                    break;
                default:
                    return;
            }
            HashMap<Point, StarLocation> addedStars = new HashMap<>();
            this.connections.forEach(connection -> {
                StarLocation s1, s2;
                if (addedStars.containsKey(connection.p1)) {
                    s1 = addedStars.get(connection.p1);
                } else {
                    s1 = constellation.addStar(connection.p1.x, connection.p1.y);
                    addedStars.put(connection.p1, s1);
                }
                if (addedStars.containsKey(connection.p2)) {
                    s2 = addedStars.get(connection.p2);
                } else {
                    s2 = constellation.addStar(connection.p2.x, connection.p2.y);
                    addedStars.put(connection.p2, s2);
                }
                constellation.addConnection(s1, s2);
            });

            ModSupport.ASTRAL_SORCERY.get().constellation.add(constellation);
        }

        private enum Type {
            MAJOR,
            MINOR,
            WEAK
        }
    }

    public static class ConstellationMapEffectBuilder {

        private final List<ConstellationMapEffectRegistry.EnchantmentMapEffect> enchantmentEffect = new ArrayList<>();
        private final List<ConstellationMapEffectRegistry.PotionMapEffect> potionEffect = new ArrayList<>();
        private IConstellation constellation = null;

        public ConstellationMapEffectBuilder constellation(IConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        public ConstellationMapEffectBuilder enchantmentEffect(Enchantment ench, int min, int max) {
            this.enchantmentEffect.add(new ConstellationMapEffectRegistry.EnchantmentMapEffect(ench, min, max));
            return this;
        }

        public ConstellationMapEffectBuilder potionEffect(Potion potion, int min, int max) {
            this.potionEffect.add(new ConstellationMapEffectRegistry.PotionMapEffect(potion, min, max));
            return this;
        }

        private boolean validate() {
            GroovyLog.Msg out = GroovyLog.msg("Error adding Astral Sorcery Constellation Map Effect").error();

            out.add(this.constellation == null, "No constellation provided.");
            out.add(enchantmentEffect.isEmpty() && potionEffect.isEmpty(), "Either enchantmentEffect or potionEffect must be provided, neither were found.");

            return !out.postIfNotEmpty();
        }

        public void register() {
            if (!validate()) return;
            ModSupport.ASTRAL_SORCERY.get().constellation.addConstellationMapEffect(constellation, enchantmentEffect, potionEffect);
        }

    }

    public static class SignatureItemsHelper {

        private final ArrayList<IIngredient> items = new ArrayList<>();
        private IConstellation constellation = null;
        private boolean doStrip = false;

        public SignatureItemsHelper constellation(IConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        public SignatureItemsHelper stripItems() {
            this.doStrip = true;
            return this;
        }

        public SignatureItemsHelper addItem(IIngredient ing) {
            this.items.add(ing);
            return this;
        }

        public void register() {
            if (this.constellation != null) {
                if (this.doStrip) ModSupport.ASTRAL_SORCERY.get().constellation.removeSignatureItems(this.constellation);
                ModSupport.ASTRAL_SORCERY.get().constellation.addSignatureItem(this.constellation, this.items);
            } else {
                GroovyLog.msg("Error modifying Astral Sorcery constellation signature items").add("No constellation specified.").error().post();
            }
        }

    }

    public static class Point2PointConnection {

        public Point p1;
        public Point p2;

        public Point2PointConnection(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public Point2PointConnection(int x1, int y1, int x2, int y2) {
            this.p1 = new Point(x1, y1);
            this.p2 = new Point(x2, y2);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point2PointConnection)) return false;
            if (o == this) return true;
            Point2PointConnection other = (Point2PointConnection) o;
            return ((p1.equals(other.p1) && p2.equals(other.p2)) || (p2.equals(other.p1) && p1.equals(other.p2)));
        }

    }
}
