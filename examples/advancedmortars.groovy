
mods.advancedmortars.Mortar.add(
        (String[])['stone'],
        item('minecraft:diamond') * 4,
        4,
        [ore('ingotGold')]
)

mods.advancedmortars.Mortar.add(
        (String[])['stone'],
        item('minecraft:tnt'),
        4,
        [ore('ingotGold')]
)

mods.advancedmortars.Mortar.add(
        (String[])['iron'],
        item('minecraft:tnt') * 5,
        4,
        item('minecraft:tnt'),
        0.7,
        [
                ore('ingotIron'),
                ore('ingotIron'),
                ore('ingotIron'),
                ore('ingotIron'),
                ore('ingotIron'),
                ore('ingotIron'),
                ore('ingotIron'),
                ore('ingotIron')
        ]
)
