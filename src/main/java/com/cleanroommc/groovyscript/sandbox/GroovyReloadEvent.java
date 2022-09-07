package com.cleanroommc.groovyscript.sandbox;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is invoked right after groovy is reloaded internally and right before scripts are being run.
 * Is not calling on first load!
 */
public class GroovyReloadEvent extends Event {
}
