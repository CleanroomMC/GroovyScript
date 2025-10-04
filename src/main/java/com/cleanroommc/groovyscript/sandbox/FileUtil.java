package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;
import java.util.regex.Pattern;

public class FileUtil {

    private static final Char2ObjectOpenHashMap<String> encodings = new Char2ObjectOpenHashMap<>();
    private static final Set<String> warnedAboutPackage = new ObjectOpenHashSet<>();
    private static final Pattern pathPattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

    static {
        encodings.put(' ', "%20");
        encodings.put('!', "%21");
        encodings.put('"', "%22");
        encodings.put('#', "%23");
        encodings.put('$', "%24");
        encodings.put('%', "%25");
        encodings.put('&', "%26");
        encodings.put('\'', "%27");
        encodings.put('(', "%28");
        encodings.put(')', "%29");
        encodings.put('+', "%2B");
        encodings.put(',', "%2C");
        //encodings.put(':', "%3F"); // do not encode :
        encodings.put(';', "%3B");
        encodings.put('<', "%3C");
        encodings.put('=', "%3D");
        encodings.put('>', "%3E");
        encodings.put('?', "%3F");
        encodings.put('@', "%40");
        encodings.put('[', "%5B");
        encodings.put(']', "%5D");
        encodings.put('{', "%7B");
        encodings.put('|', "%7C");
        encodings.put('}', "%7D");
        if (File.separatorChar != '/') {
            encodings.put('\\', "/");
        }
    }

    public static void cleanScriptPathWarnedCache() {
        warnedAboutPackage.clear();
    }

    /**
     * Logs an error for every directory in the path if it contains illegal characters.
     *
     * @param path relative script path to check for
     * @return if script path is valid
     */
    public static boolean validateScriptPath(String path) {
        int index = path.lastIndexOf('.');
        if (index < 0) {
            GroovyLog.get().errorMC("Script path '{}' doesn't have a file ending.", decodeURI(path));
            return false;
        }
        String[] parts = path.substring(0, index).split("/");
        boolean errorInFile = false;
        boolean errorInPath = false;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!pathPattern.matcher(part).matches()) {
                String fullPath = StringUtils.join(parts, '/', 0, i + 1);
                if (i == parts.length - 1) {
                    errorInFile = true;
                } else {
                    errorInPath = true;
                    if (!warnedAboutPackage.contains(fullPath)) {
                        warnedAboutPackage.add(fullPath);
                        GroovyLog.get().errorMC("Script path '{}' contains illegal characters. Only letters, numbers and underscores are allowed for folder and file names. Folders and files must start with a letter!", decodeURI(fullPath));
                    }
                }
            }
        }
        if (errorInFile) {
            GroovyLog.get().errorMC("Skipping script '{}', because path contains illegal characters. Only letters, numbers and underscores are allowed for folder and file names. Folders and files must start with a letter!", decodeURI(path));
        } else if (errorInPath) {
            GroovyLog.get().error("Skipping script '{}' because of illegal characters", decodeURI(path));
        }
        return !errorInFile && !errorInPath;
    }

    public static @NotNull String relativize(String rootPath, String longerThanRootPath) {
        longerThanRootPath = encodeURI(fixPath(decodeURI(longerThanRootPath)));
        rootPath = encodeURI(rootPath);
        return relativizeInternal(rootPath, longerThanRootPath, false);
    }

    public static @Nullable String relativizeNullable(String rootPath, String longerThanRootPath) {
        longerThanRootPath = encodeURI(fixPath(decodeURI(longerThanRootPath)));
        rootPath = encodeURI(rootPath);
        return relativizeInternal(rootPath, longerThanRootPath, true);
    }

    @Contract("_,_,false -> !null")
    private static String relativizeInternal(String rootPath, String longerThanRootPath, boolean nullable) {
        int index = longerThanRootPath.indexOf(rootPath);
        if (index < 0) {
            if (nullable) return null;
            throw new IllegalArgumentException("The path '" + longerThanRootPath + "' does not contain the root path '" + rootPath + "'");
        }
        return longerThanRootPath.substring(index + rootPath.length() + 1);
    }

    public static String getParent(String path) {
        int i = path.lastIndexOf(File.separatorChar);
        if (i <= 0) return StringUtils.EMPTY;
        path = path.substring(0, i);
        if (path.length() == 2 && Character.isLetter(path.charAt(0)) && path.charAt(1) == ':') return StringUtils.EMPTY;
        return path;
    }

    public static String makePath(String... pieces) {
        if (pieces == null || pieces.length == 0) return StringUtils.EMPTY;
        if (pieces.length == 1) return sanitizePath(pieces[0]);
        StringBuilder builder = new StringBuilder();
        for (String piece : pieces) {
            if (piece != null && !piece.isEmpty()) {
                builder.append(sanitizePath(piece)).append(File.separatorChar);
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static String sanitizePath(String path) {
        return path.replace(getOtherSeparatorChar(), File.separatorChar);
    }

    public static char getOtherSeparatorChar() {
        return File.separatorChar == '/' ? '\\' : '/';
    }

    public static File makeFile(String... pieces) {
        return new File(makePath(pieces));
    }

    public static boolean mkdirs(File file) {
        if (file.isDirectory()) {
            return file.mkdirs();
        }
        return file.getParentFile().mkdirs();
    }

    public static boolean mkdirsAndFile(File file) {
        boolean b = mkdirs(file);
        if (file.isFile()) {
            try {
                Files.createFile(file.toPath());
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return b;
    }

    public static URI fixUri(URI uri) {
        String scheme = uri.getScheme();
        return URI.create(scheme + ':' + fixUriString(uri.getRawPath()));
    }

    public static URI fixUri(String uri) {
        return URI.create(fixUriString(uri));
    }

    public static String fixUriString(String uri) {
        return encodeURI(fixPath(decodeURI(uri)));
    }

    public static String fixPath(String uri) {
        int i = 0, s = 0;
        if (uri.startsWith("file:")) i = 5;
        while (uri.charAt(i + s) == '/') s++;
        i += s;
        char c = uri.charAt(i);
        if (uri.length() <= i + 1 || !Character.isLowerCase(c)) return uri;
        int d = 1;
        if (uri.charAt(i + 1) != ':') {
            if (uri.length() <= i + 3 || uri.charAt(i + 1) != '%' || uri.charAt(i + 2) != '3' || uri.charAt(i + 3) != 'F') return uri;
            d = 3;
        }
        StringBuilder builder = new StringBuilder();
        s -= Math.min(s, 3); // max 3 '/' after file:
        if (i > 0) builder.append(uri, 0, i - s);
        builder.append(Character.toUpperCase(uri.charAt(i + d - 1)));
        builder.append(uri, i + d, uri.length());
        return builder.toString();
    }

    public static String decodeURI(String s) {
        Charset charset = StandardCharsets.UTF_8;
        boolean needToChange = false;
        int numChars = s.length();
        StringBuilder sb = new StringBuilder(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;

        char c;
        byte[] bytes = null;
        while (i < numChars) {
            c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    i++;
                    needToChange = true;
                    break;
                case '%':
                    /*
                     * Starting with this instance of %, process all consecutive substrings of the form %xy. Each
                     * substring %xy will yield a byte. Convert all consecutive  bytes obtained this way to whatever
                     * character(s) they represent in the provided encoding.
                     */

                    try {

                        // (numChars-i)/3 is an upper bound for the number
                        // of remaining bytes
                        if (bytes == null) bytes = new byte[(numChars - i) / 3];
                        int pos = 0;

                        while (((i + 2) < numChars) && (c == '%')) {
                            int v = parseFromHex(s.charAt(i + 1), s.charAt(i + 2));//Integer.parseInt(s, i + 1, i + 3, 16);
                            if (v < 0) {
                                throw new IllegalArgumentException(
                                        "URLDecoder: Illegal hex characters in escape " + "(%) pattern - negative value");
                            }
                            bytes[pos++] = (byte) v;
                            i += 3;
                            if (i < numChars) c = s.charAt(i);
                        }

                        // A trailing, incomplete byte encoding such as
                        // "%x" will cause an exception to be thrown

                        if ((i < numChars) && (c == '%')) {
                            throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                        }

                        sb.append(new String(bytes, 0, pos, charset));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + e.getMessage());
                    }
                    needToChange = true;
                    break;
                default:
                    if (c == File.separatorChar && File.separatorChar != '/') {
                        needToChange = true;
                        c = '/';
                    }
                    sb.append(c);
                    i++;
                    break;
            }
        }

        return (needToChange ? sb.toString() : s);
    }

    private static int parseFromHex(char c0, char c1) {
        return Character.digit(c0, 16) * 16 + Character.digit(c1, 16);
    }

    public static String encodeURI(String s) {
        if (s == null || s.isEmpty()) return s;
        boolean needToChange = false;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String rep = encodings.get(c);
            if (rep != null) {
                builder.append(rep);
                needToChange = true;
            } else {
                builder.append(c);
            }
        }
        return needToChange ? builder.toString() : s;
    }
}
