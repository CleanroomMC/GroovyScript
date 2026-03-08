
// MODS_LOADED: gregtech

// This script adds the ability to overwrite material components.
// An example can be found at the bottom. The section that adds the methods only needs to run once and can be
// in a separate script.

import gregtech.api.unification.material.Material

Material.metaClass.makePublic('materialInfo')
Material.metaClass.makePublic('chemicalFormula')
Material.MaterialInfo.metaClass.makePublic('componentList')

static def recalcFormula(components) {
    // prevent parenthesis around single component materials
    if (components.size() == 1) {
        def stack = components.get(0);
        if (stack.amount == 1) {
            return stack.material.getChemicalFormula();
        }
    }
    def s = new StringBuilder();
    for (def component : components) s.append(component.toFormatted());
    return s.toString();
}

Material.metaClass.setComponents << { Object[] materialStacks ->
    def l = []
    for (def o : materialStacks) {
        if (o instanceof gregtech.api.unification.stack.MaterialStack) {
            l << o
        } else if (o instanceof Material) {
            l << o * 1
        } else {
            throw new IllegalArgumentException('Only Material and MaterialStack are valid types in setComponents()!')
        }
    }
    materialInfo.componentList = com.google.common.collect.ImmutableList.copyOf(l)
    setFormula(recalcFormula(l))
}

Material.metaClass.updateColor << { ->
    long colorTemp = 0
    int divisor = 0
    for (def stack : materialInfo.componentList) {
        colorTemp += stack.material.getMaterialRGB() * stack.amount
        divisor += stack.amount
    }
    materialInfo.color = (int) (colorTemp / divisor)
}

// Example usage

mods.gregtech.lateMaterialEvent {
    // overwrite components of bronze with gold and 2 uranium 235
    material('bronze').setComponents(material('gold'), material('uranium_235') * 2)
    // many materials use the color of the weighted average color of all materials
    // this method updates the color with the average of the new components
    material('bronze').updateColor()
}
