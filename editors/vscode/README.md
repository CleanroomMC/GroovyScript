# GroovyScript

This is the official VSC extension for the Minecraft 1.12.2 mod [GroovyScript](https://github.com/CleanroomMC/GroovyScript).
It allows VSC to connect to a running minecraft instance with a groovy folder and provide various tools for writing
scripts like auto-completion and hover info.

## Getting Started
1. In your preferred minecraft launcher add `-Dgroovyscript.run_ls=true` to the JVM arguments.
2. Run a Minecraft instance with the GroovyScript mod installed from your launcher.
3. Open VSC with this plugin installed (or run the 'GroovyScript: Reconnect' command in VSC).

That's it. The important part is that Minecraft needs to run with the `-Dgroovyscript.run_ls=true` option.
Note that minecraft has higher memory and cpu usage while the language server is running.
