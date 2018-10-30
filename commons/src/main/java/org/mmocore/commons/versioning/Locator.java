package org.mmocore.commons.versioning;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * The Locator is a utility class which is used to find certain items in the
 * environment.
 *
 * @since Ant 1.6
 */
public final class Locator {
    /**
     * Not instantiable
     */
    private Locator() {
    }

    /**
     * Find the directory or jar file the class has been loaded from.
     *
     * @param c the class whose location is required.
     * @return the file or jar with the class or null if we cannot determine the
     * location.
     * @since Ant 1.6
     */
    public static File getClassSource(final Class<?> c) {
        final String classResource = c.getName().replace('.', '/') + ".class";
        return getResourceSource(c.getClassLoader(), classResource);
    }

    /**
     * Find the directory or jar a given resource has been loaded from.
     *
     * @param c        the classloader to be consulted for the source.
     * @param resource the resource whose location is required.
     * @return the file with the resource source or null if we cannot determine
     * the location.
     * @since Ant 1.6
     */
    public static File getResourceSource(ClassLoader c, final String resource) {
        if (c == null) {
            c = Locator.class.getClassLoader();
        }
        final URL url;
        if (c == null) {
            url = ClassLoader.getSystemResource(resource);
        } else {
            url = c.getResource(resource);
        }
        if (url != null) {
            final String u = url.toString();
            if (u.startsWith("jar:file:")) {
                final int pling = u.indexOf('!');
                final String jarName = u.substring(4, pling);
                return new File(fromURI(jarName));
            } else if (u.startsWith("file:")) {
                final int tail = u.indexOf(resource);
                final String dirName = u.substring(0, tail);
                return new File(fromURI(dirName));
            }
        }
        return null;
    }

    /**
     * Constructs a file path from a <code>file:</code> URI.
     * <p/>
     * <p>
     * Will be an absolute path if the given URI is absolute.
     * </p>
     * <p/>
     * <p>
     * Swallows '%' that are not followed by two characters, doesn't deal with
     * non-ASCII characters.
     * </p>
     *
     * @param uri the URI designating a file in the local filesystem.
     * @return the local file system path for the file.
     * @since Ant 1.6
     */
    public static String fromURI(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException emYouEarlEx) {
            // Ignore malformed exception
        }
        if (url == null || !"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("Can only handle valid file: URIs");
        }
        final StringBuilder buf = new StringBuilder(url.getHost());
        if (buf.length() > 0) {
            buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
        }
        final String file = url.getFile();
        final int queryPos = file.indexOf('?');
        buf.append(queryPos < 0 ? file : file.substring(0, queryPos));

        uri = buf.toString().replace('/', File.separatorChar);

        if (File.pathSeparatorChar == ';' && uri.startsWith("\\") && uri.length() > 2 && Character.isLetter(uri.charAt(1)) && uri.lastIndexOf(':') > -1) {
            uri = uri.substring(1);
        }
        return decodeUri(uri);
    }

    /**
     * Decodes an Uri with % characters.
     *
     * @param uri String with the uri possibly containing % characters.
     * @return The decoded Uri
     */
    private static String decodeUri(final String uri) {
        if (uri.indexOf('%') == -1) {
            return uri;
        }
        final StringBuilder sb = new StringBuilder();
        final CharacterIterator iter = new StringCharacterIterator(uri);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            if (c == '%') {
                final char c1 = iter.next();
                if (c1 != CharacterIterator.DONE) {
                    final int i1 = Character.digit(c1, 16);
                    final char c2 = iter.next();
                    if (c2 != CharacterIterator.DONE) {
                        final int i2 = Character.digit(c2, 16);
                        sb.append((char) ((i1 << 4) + i2));
                    }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
