package com.cleanroommc.groovyscript;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Checks that the Java version being used can be used by the Groovy that GroovyScript uses.
 * Our version of Groovy is currently not compatible with java versions above 21.
 */
public class JavaVersionCheck {

    private static final int MAXIMUM_VERSION = 21;

    /**
     * Checks that the Java version being used can run GroovyScript scripts.
     */
    public static void validateJavaVersion(Side side) {
        int version = getJavaVersion();
        if (version > MAXIMUM_VERSION) handleJavaVersionException(version, side);
    }

    private static void handleJavaVersionException(int version, Side side) {
        String msg1 = "GroovyScript's version of Groovy does not work with Java versions greater than " + MAXIMUM_VERSION + " currently.";
        String msg2 = "Please downgrade to Java " + MAXIMUM_VERSION + " or lower. Your current Java version is " + version + ".";
        if (side.isClient()) {
            throw new IncompatibleJavaException(msg1 + "\n" + msg2);
        } else {
            throw new IllegalStateException(msg1 + " " + msg2);
        }
    }

    /**
     * Gets the major version of Java currently running.
     * <table>
     *  <tr>
     *   <th>1.8.0_51</th>
     *   <th>=</th>
     *   <th>8</th>
     *  </tr>
     *  <tr>
     *   <th>21.0.6</th>
     *   <th>=</th>
     *   <th>21</th>
     *  </tr>
     * </table>
     * Code comes from
     * <a href="https://stackoverflow.com/questions/2591083/getting-java-version-at-runtime">Stack Overflow</a>.
     */
    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) version = version.substring(0, dot);
        }
        return Integer.parseInt(version);
    }
}
