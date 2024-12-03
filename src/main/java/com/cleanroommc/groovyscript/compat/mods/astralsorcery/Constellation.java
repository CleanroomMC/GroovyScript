package com.cleanroommc.groovyscript.compat.mods.astralsorcery;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.util.*;
import java.util.List;

@RegistryDescription(
        category = RegistryDescription.Category.ENTRIES
)
public class Constellation extends VirtualizedRegistry<IConstellation> {

    private final Map<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsAdded = new HashMap<>();
    private final Map<IConstellation, ConstellationMapEffectRegistry.MapEffect> constellationMapEffectsRemoved = new HashMap<>();
    private final Map<IConstellation, List<ItemHandle>> signatureItemsAdded = new HashMap<>();
    private final Map<IConstellation, List<ItemHandle>> signatureItemsRemoved = new HashMap<>();

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

    @Override
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

    @MethodDescription(example = @Example("constellation('bootes')"))
    public boolean remove(IConstellation constellation) {
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

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IConstellation> streamConstellations() {
        return new SimpleObjectStream<>(ConstellationRegistryAccessor.getConstellationList())
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
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

    @MethodDescription(example = @Example("constellation('discidia')"))
    public void removeConstellationMapEffect(IConstellation constellation) {
        if (ConstellationMapEffectRegistryAccessor.getEffectRegistry() == null) return;
        if (ConstellationMapEffectRegistryAccessor.getEffectRegistry().containsKey(constellation)) {
            this.constellationMapEffectsRemoved.put(constellation, ConstellationMapEffectRegistryAccessor.getEffectRegistry().get(constellation));
            ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(constellation);
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAllConstellationMapEffect() {
        ConstellationRegistryAccessor.getConstellationList().forEach(constellation -> {
            this.constellationMapEffectsRemoved.put(constellation, ConstellationMapEffectRegistryAccessor.getEffectRegistry().get(constellation));
            ConstellationMapEffectRegistryAccessor.getEffectRegistry().remove(constellation);
        });
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    private void addSignatureItem(IConstellation constellation, ItemHandle item) {
        constellation.addSignatureItem(item);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addSignatureItem(IConstellation constellation, IIngredient ing) {
        if (!this.signatureItemsAdded.containsKey(constellation)) {
            this.signatureItemsAdded.put(constellation, new ArrayList<>());
        }
        this.signatureItemsAdded.get(constellation).add(AstralSorcery.toItemHandle(ing));
        constellation.addSignatureItem(AstralSorcery.toItemHandle(ing));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addSignatureItem(IConstellation constellation, IIngredient... ings) {
        for (IIngredient ing : ings) {
            this.addSignatureItem(constellation, ing);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addSignatureItem(IConstellation constellation, Collection<IIngredient> ings) {
        ings.forEach(ing -> this.addSignatureItem(constellation, ing));
    }

    @MethodDescription(example = @Example("constellation('discidia')"))
    public void removeSignatureItems(IConstellation constellation) {
        this.signatureItemsRemoved.put(constellation, ((ConstellationBaseAccessor) constellation).getSignatureItems());
        ((ConstellationBaseAccessor) constellation).getSignatureItems().clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
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

    @RecipeBuilderDescription(example = {
            @Example(".major().name('square').color(0xE01903).connection(12, 2, 2, 2).connection(12, 12, 12, 2).connection(2, 12, 12, 12).connection(2, 2, 2, 12)"),
            @Example(value = ".minor().name('slow').connection(10, 5, 5, 5).connection(5, 10, 5, 5).connection(3, 3, 3, 3).phase(MoonPhase.FULL)", imports = "hellfirepvp.astralsorcery.common.constellation.MoonPhase")
    })
    public ConstellationBuilder constellationBuilder() {
        return new ConstellationBuilder();
    }

    @RecipeBuilderDescription(example = @Example(".constellation(constellation('square')).enchantmentEffect(enchantment('minecraft:luck_of_the_sea'), 1, 3).potionEffect(potion('minecraft:luck'), 1, 2)"))
    public ConstellationMapEffectBuilder constellationMapEffectBuilder() {
        return new ConstellationMapEffectBuilder();
    }

    @RecipeBuilderDescription(example = @Example(".constellation(constellation('square')).addItem(ore('gemDiamond')).addItem(item('minecraft:water_bucket')).addItem(item('minecraft:rabbit_foot')).addItem(item('minecraft:fish'))"))
    public SignatureItemsHelper signatureItems() {
        return new SignatureItemsHelper();
    }

    public static class ConstellationBuilder {

        @Property(comp = @Comp(gte = 0))
        private final List<Point2PointConnection> connections = new ArrayList<>();
        @Property(comp = @Comp(gte = 0)) // TODO note that this is only if type is MINOR
        private final List<MoonPhase> phases = new ArrayList<>();
        @Property(ignoresInheritedMethods = true, comp = @Comp(not = "null"))
        private String name;
        @Property(defaultValue = "Major: #2843CC, Weak: #432CB0, Minor: #5D197F")
        private Color color;
        @Property
        private ConstellationBuilder.Type type;

        @RecipeBuilderMethodDescription
        public ConstellationBuilder name(String name) {
            this.name = name;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ConstellationBuilder color(int color) {
            this.color = new Color(color);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public ConstellationBuilder major() {
            this.type = Type.MAJOR;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public ConstellationBuilder minor() {
            this.type = Type.MINOR;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "type")
        public ConstellationBuilder weak() {
            this.type = Type.WEAK;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "connections")
        public ConstellationBuilder connection(int x1, int y1, int x2, int y2) {
            Point2PointConnection connection = new Point2PointConnection(x1, y1, x2, y2);
            if (!connections.contains(connection)) {
                connections.add(connection);
            }
            return this;
        }

        @RecipeBuilderMethodDescription(field = "phases")
        public ConstellationBuilder phase(MoonPhase moonPhase) {
            this.phases.add(moonPhase);
            return this;
        }

        @RecipeBuilderMethodDescription(field = "phases")
        public ConstellationBuilder phase(MoonPhase... moonPhases) {
            this.phases.addAll(Arrays.asList(moonPhases));
            return this;
        }

        @RecipeBuilderMethodDescription(field = "phases")
        public ConstellationBuilder phase(Collection<MoonPhase> moonPhases) {
            this.phases.addAll(moonPhases);
            return this;
        }

        private boolean validate() {
            ArrayList<String> errors = new ArrayList<>();

            if (this.name == null || this.name.isEmpty())
                errors.add("name must be provided");
            if (this.connections.isEmpty())
                errors.add("connections must not be empty");
            if (type == Type.MINOR && this.phases.isEmpty())
                errors.add("minor constellations require at least one moon phase");

            if (!errors.isEmpty()) {
                GroovyLog.Msg errorOut = GroovyLog.msg("Error adding Astral Sorcery Constellation: ");
                errors.forEach(errorOut::add);
                errorOut.error().post();
                return false;
            }

            if (this.color == null) {
                this.color = switch (this.type) {
                    case MAJOR -> new Color(40, 67, 204);
                    case WEAK -> new Color(67, 44, 176);
                    case MINOR -> new Color(93, 25, 127);
                };
            }

            return true;
        }

        @RecipeBuilderRegistrationMethod
        public void register() {
            if (!validate()) return;
            IConstellation constellation = switch (this.type) {
                case MAJOR -> new ConstellationBase.Major(name, color);
                case WEAK -> new ConstellationBase.Minor(name, color, phases.toArray(new MoonPhase[0]));
                case MINOR -> new ConstellationBase.Weak(name, color);
            };
            Map<Point, StarLocation> addedStars = new Object2ObjectOpenHashMap<>();
            this.connections.forEach(connection -> {
                StarLocation s1;
                StarLocation s2;
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

        @Property(comp = @Comp(gte = 0)) // TODO note that this is only if other is empty
        private final List<ConstellationMapEffectRegistry.EnchantmentMapEffect> enchantmentEffect = new ArrayList<>();
        @Property(comp = @Comp(gte = 0)) // TODO note that this is only if other is empty
        private final List<ConstellationMapEffectRegistry.PotionMapEffect> potionEffect = new ArrayList<>();
        @Property(comp = @Comp(not = "null"))
        private IConstellation constellation;

        @RecipeBuilderMethodDescription
        public ConstellationMapEffectBuilder constellation(IConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        @RecipeBuilderMethodDescription
        public ConstellationMapEffectBuilder enchantmentEffect(Enchantment ench, int min, int max) {
            this.enchantmentEffect.add(new ConstellationMapEffectRegistry.EnchantmentMapEffect(ench, min, max));
            return this;
        }

        @RecipeBuilderMethodDescription
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

        @RecipeBuilderRegistrationMethod
        public void register() {
            if (!validate()) return;
            ModSupport.ASTRAL_SORCERY.get().constellation.addConstellationMapEffect(constellation, enchantmentEffect, potionEffect);
        }
    }

    public static class SignatureItemsHelper {

        @Property
        private final List<IIngredient> items = new ArrayList<>();
        @Property(comp = @Comp(not = "null"))
        private IConstellation constellation;
        @Property
        private boolean doStrip;

        @RecipeBuilderMethodDescription
        public SignatureItemsHelper constellation(IConstellation constellation) {
            this.constellation = constellation;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "doStrip")
        public SignatureItemsHelper stripItems() {
            this.doStrip = true;
            return this;
        }

        @RecipeBuilderMethodDescription(field = "items")
        public SignatureItemsHelper addItem(IIngredient ing) {
            this.items.add(ing);
            return this;
        }

        @RecipeBuilderRegistrationMethod
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
            if (!(o instanceof Point2PointConnection other)) return false;
            if (o == this) return true;
            return ((p1.equals(other.p1) && p2.equals(other.p2)) || (p2.equals(other.p1) && p1.equals(other.p2)));
        }
    }
}
