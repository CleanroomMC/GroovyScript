package net.prominic.groovyls.util;

import java.net.URI;

public final class URIUtils {

    public static URI toUri(String uriString) {
        // for some reason vscode like to output garbage like file:///c%3A/Users/..
        // we cant decode here since paths with spaces will cause an error in URI.create()
        var decodedUriString = uriString;//URLDecoder.decode(uriString, "UTF-8");

        if (decodedUriString.matches("^file:///[a-z]:/.*$")) {
            decodedUriString = "file:///" + decodedUriString.substring(8, 9).toUpperCase() + decodedUriString.substring(9);
        }
        return URI.create(decodedUriString);
    }
}
