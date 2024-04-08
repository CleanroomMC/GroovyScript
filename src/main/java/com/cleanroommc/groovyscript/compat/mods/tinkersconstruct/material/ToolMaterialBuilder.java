package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material;

import c4.conarm.lib.materials.ArmorMaterialType;
import c4.conarm.lib.materials.CoreMaterialStats;
import c4.conarm.lib.materials.PlatesMaterialStats;
import c4.conarm.lib.materials.TrimMaterialStats;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Property;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.*;
import slimeknights.tconstruct.library.traits.ITrait;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ToolMaterialBuilder {

    public static final List<GroovyMaterial> addedMaterials = new ArrayList<>();

    public final String name;
    @Property
    public int color = 0xFFFFFF;
    @Property
    public boolean hidden = false;
    @Property
    public boolean craftable = false;
    @Property(defaultValue = "true")
    public boolean castable = true;
    @Property
    public String displayName;
    @Property
    public FluidStack fluid;
    @Property
    public IIngredient representativeItem;
    @Property
    public IIngredient shard;
    @Property
    public BiFunction<Material, String, String> localizer;
    @Property
    protected Map<String, List<String>> traits = new HashMap<>();
    @Property
    protected List<MaterialRepairIngredient> repairIngredients = new ArrayList<>();
    @Property
    protected final Map<String, IMaterialStats> stats = new HashMap<>(8);

    public ToolMaterialBuilder(String name) {
        this.name = name;
    }

    @RecipeBuilderMethodDescription(field = "traits")
    public ToolMaterialBuilder addTrait(String trait, @Nullable String dependency) {
        String key = dependency != null ? dependency : "all";
        this.traits.computeIfAbsent(key, k -> new ArrayList<>());
        this.traits.get(key).add(trait);
        return this;
    }

    @RecipeBuilderMethodDescription(field = "traits")
    public ToolMaterialBuilder addTrait(String trait) {
        return addTrait(trait, null);
    }

    @RecipeBuilderMethodDescription(field = "traits")
    public ToolMaterialBuilder addTrait(ITrait trait, @Nullable String dependency) {
        return addTrait(trait.getIdentifier(), dependency);
    }

    @RecipeBuilderMethodDescription(field = "traits")
    public ToolMaterialBuilder addTrait(ITrait trait) {
        return addTrait(trait, null);
    }

    @RecipeBuilderMethodDescription(field = "traits")
    public ToolMaterialBuilder addArmorTrait(String trait) {
        String name = trait.endsWith("_armor") ? trait : trait + "_armor";
        addTrait(name, "core");
        addTrait(name, "plates");
        addTrait(name, "trim");
        return this;
    }

    @RecipeBuilderMethodDescription(field = "traits")
    public ToolMaterialBuilder addArmorTrait(ITrait trait) {
        return addArmorTrait(trait.getIdentifier());
    }

    @RecipeBuilderMethodDescription(field = "shard")
    public ToolMaterialBuilder setShardItem(IIngredient shard) {
        this.shard = shard;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "repairIngredients")
    public ToolMaterialBuilder addRepairItem(IIngredient repair, int amountNeeded, int amountMatched) {
        this.repairIngredients.add(new MaterialRepairIngredient(repair, amountNeeded, amountMatched));
        return this;
    }

    @RecipeBuilderMethodDescription(field = "representativeItem")
    public ToolMaterialBuilder setRepresentativeItem(IIngredient item) {
        this.representativeItem = item;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "fluid")
    public ToolMaterialBuilder setFluid(FluidStack fluid) {
        this.fluid = fluid;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "displayName")
    public ToolMaterialBuilder setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "displayName")
    public ToolMaterialBuilder setLocalizedName(String name) {
        return setDisplayName(name);
    }

    @RecipeBuilderMethodDescription(field = "color")
    public ToolMaterialBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "color")
    public ToolMaterialBuilder setColor(Color color) {
        return setColor(color.getRGB());
    }

    @RecipeBuilderMethodDescription(field = "color")
    public ToolMaterialBuilder setColor(int r, int g, int b) {
        return setColor(new Color(r, g, b));
    }

    @RecipeBuilderMethodDescription(field = "localizer")
    public ToolMaterialBuilder setLocalizer(BiFunction<Material, String, String> localizer) {
        this.localizer = localizer;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "isHidden")
    public ToolMaterialBuilder isHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "isCraftable")
    public ToolMaterialBuilder isCraftable(boolean craftable) {
        this.craftable = craftable;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "isCastable")
    public ToolMaterialBuilder isCastable(boolean castable) {
        this.castable = castable;
        return this;
    }

    @RecipeBuilderMethodDescription(field = "isHidden")
    public ToolMaterialBuilder isHidden() {
        return isHidden(!hidden);
    }

    @RecipeBuilderMethodDescription(field = "isCraftable")
    public ToolMaterialBuilder isCraftable() {
        return isCraftable(!craftable);
    }

    @RecipeBuilderMethodDescription(field = "isCastable")
    public ToolMaterialBuilder isCastable() {
        return isCastable(!castable);
    }

    public ToolMaterialBuilder addHeadStats(int durability, float miningSpeed, float attackDamage, int harvestLevel) {
        this.stats.put(MaterialTypes.HEAD, new HeadMaterialStats(durability, miningSpeed, attackDamage, harvestLevel));
        return this;
    }

    public ToolMaterialBuilder addHandleStats(float modifier, int durability) {
        this.stats.put(MaterialTypes.HANDLE, new HandleMaterialStats(modifier, durability));
        return this;
    }

    public ToolMaterialBuilder addExtraStats(int durability) {
        this.stats.put(MaterialTypes.EXTRA, new ExtraMaterialStats(durability));
        return this;
    }

    public ToolMaterialBuilder addBowStats(float drawSpeed, float range, float bonusDamage) {
        this.stats.put(MaterialTypes.BOW, new BowMaterialStats(drawSpeed, range, bonusDamage));
        return this;
    }

    public ToolMaterialBuilder addBowStringStats(float modifier) {
        this.stats.put(MaterialTypes.BOWSTRING, new BowStringMaterialStats(modifier));
        return this;
    }

    public ToolMaterialBuilder addProjectileStats() {
        this.stats.put(MaterialTypes.PROJECTILE, new ProjectileMaterialStats());
        return this;
    }

    public ToolMaterialBuilder addArrowShaftStats(float modifier, int bonusAmmo) {
        this.stats.put(MaterialTypes.SHAFT, new ArrowShaftMaterialStats(modifier, bonusAmmo));
        return this;
    }

    public ToolMaterialBuilder addFletchingStats(float accuracy, float modifier) {
        this.stats.put(MaterialTypes.FLETCHING, new FletchingMaterialStats(accuracy, modifier));
        return this;
    }

    public ToolMaterialBuilder addCoreStats(float durability, float defense) {
        if (Loader.isModLoaded("conarm")) this.stats.put(ArmorMaterialType.CORE, new CoreMaterialStats(durability, defense));
        return this;
    }

    public ToolMaterialBuilder addPlatesStats(float modifier, float durability, float toughness) {
        if (Loader.isModLoaded("conarm")) this.stats.put(ArmorMaterialType.PLATES, new PlatesMaterialStats(modifier, durability, toughness));
        return this;
    }

    public ToolMaterialBuilder addTrimStats(float durability) {
        if (Loader.isModLoaded("conarm")) this.stats.put(ArmorMaterialType.TRIM, new TrimMaterialStats(durability));
        return this;
    }

    public String getErrorMsg() {
        return "Error adding Tinkers' Construct Material";
    }

    public boolean validate() {
        GroovyLog.Msg msg = GroovyLog.msg(this.getErrorMsg()).error();
        this.validate(msg);
        return !msg.postIfNotEmpty();
    }

    public void validate(GroovyLog.Msg msg) {
        msg.add(stats.isEmpty(), "Tool material must have at least one stat type, but found none!");
        msg.add(representativeItem == null, "Tool material must have a representative item, but found none!");
        msg.add(displayName == null || displayName.isEmpty(), "Expected a localized material name, got " + displayName);
    }

    @Nullable
    public Material register() {
        if (!validate()) return null;
        GroovyMaterial material = new GroovyMaterial(name, color, traits);
        addedMaterials.add(material);

        material.fluidStack = fluid;
        material.displayName = displayName;
        material.localizer = localizer;
        material.hidden = hidden;

        material.setCastable(castable);
        material.setCraftable(craftable);

        stats.forEach((name, stat) -> {
            if (stat != null) material.addStats(stat);
        });
        repairIngredients.forEach(repair -> {
            if (repair.ingredient instanceof OreDictIngredient)
                material.addItem(((OreDictIngredient) repair.ingredient).getOreDict(), repair.amountNeeded, repair.amountMatched * 144);
            else material.addItem(repair.ingredient.getMatchingStacks()[0], repair.amountNeeded, repair.amountMatched * 144);
        });

        if (representativeItem != null) {
            if (representativeItem instanceof OreDictIngredient) material.setRepresentativeItem(((OreDictIngredient) representativeItem).getOreDict());
            else material.representativeItem = representativeItem;
        }
        material.shard = shard;

        TinkerRegistry.addMaterial(material);
        TinkerRegistry.integrate(new GroovyMaterialIntegration(material));
        return material;
    }
}
