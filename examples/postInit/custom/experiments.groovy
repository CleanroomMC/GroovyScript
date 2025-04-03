
// no_run

def ore_iron = ore('ingotIron')
def item_iron = item('minecraft:iron_ingot')
log.info(item_iron in ore_iron) // true
log.info(item_iron in item_iron) // true
log.info(ore_iron in item_iron) // false
log.info(item_iron << ore_iron) // true
log.info((item_iron * 3) << ore_iron) // false
log.info(ore_iron >> item_iron) // true
log.info(ore_iron >> (item_iron * 3)) // false

file('config/').eachFile { file ->
    println file.path
}
