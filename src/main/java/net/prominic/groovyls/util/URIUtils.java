package net.prominic.groovyls.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

public final class URIUtils {

    public static URI toUri(String uri) {
        try {
            // for some reason vscode like to output garbage like file:///c%3A/Users/..
            return URI.create(URLDecoder.decode(uri, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
