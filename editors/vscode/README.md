# GroovyScript

This is the official VSC extension for the Minecraft 1.12.2 mod [GroovyScript](https://github.com/CleanroomMC/GroovyScript).
It allows VSC to connect to a running minecraft instance with a groovy folder and provide various tools for writing
scripts like auto-completion and hover info.

## Getting Started
First you need to start the language server. Then you can connect VSC.
### Starting the language Server
There are two ways to do that.
1. Adding `-Dgroovyscript.run_ls=true` to the JVM arguments in your preferred launcher and start Minecraft with GroovyScript.
   You will be able to connect VSC as soon as GroovyScript start running `preInit` scripts.
2. By starting Minecraft and GroovyScript and running `/grs runLS` command. Obviously you need to load into a world for that.

You can check if the server started by checking for a `Starting Language server` message in the groovy log.

By default, the language server is started with a port of `25564`. It must be the same as the port in the VSC extension,
which is configurable.

### Connect VSC
1. Open VSC (skip if already open)
2. Install GroovyScript extension (skip if already installed)
3. Open the instance folder or groovy folder of a modpack in VSC
4. If you just opened VSC, it should auto connect. Otherwise, run the `GroovyScript: Reconnect` command.
5. Done

At the bottom right in the status bar is a thumbs up/down emoji. This indicates the server connection status.

That's it. The important part is that Minecraft needs to be running and the language server is started.
Note that minecraft has higher memory and cpu usage while the language server is running.

Happy scripting :)
