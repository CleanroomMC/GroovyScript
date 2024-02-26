package net.prominic.groovyls.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

public final class URIUtils {

    public static URI toUri(String uriString) {
        try {
            // for some reason vscode like to output garbage like file:///c%3A/Users/..
            var decodedUriString = URLDecoder.decode(uriString, "UTF-8");

            if (decodedUriString.matches("^file:///[a-z]:/.*$")) {
                decodedUriString = "file:///" + decodedUriString.substring(8, 9).toUpperCase() + decodedUriString.substring(9);
            }
            return URI.create(decodedUriString);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
