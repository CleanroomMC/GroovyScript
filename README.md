## GroovyScript
A comprehensive scripting sandbox for Minecraft 1.12.2.

For syntax and usage, visit the [official wiki](https://cleanroommc.com/groovy-script/)

#### Features:

1. Groovy: a powerful and tested scripting language
    - Java-compatible syntax + interoperable code
    - Static compilation
    - Optional dynamic typing
    - Metaprogramming
2. Sandboxed: scripts are effectively ran in a sandbox, with sensitive operations blacklisted from being called or referenced
3. Reloading: able to test script changes within the game without restarting
    - Most if not all Forge Registry objects can be reloaded on the fly
    - Most mod registries are supported natively
4. Events: Easily listen to Forge's EventBuses with Groovy Closures
5. Familiarity: Bracket handlers for those that are familiar with CraftTweaker (currently implemented, may be removed in the near future)
6. Informational: commands to display rich information for the item in your hand and more
7. Optimized: Making sure no compromises are being made while delivering features with efficient code
8. API: Great catalogue of code for other mod authors to make their mods compatible with GroovyScript such as package/class/method/field blacklists