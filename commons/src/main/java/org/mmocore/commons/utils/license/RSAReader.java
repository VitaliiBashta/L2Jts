package org.mmocore.commons.utils.license;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Base64;

public class RSAReader extends BufferedReader {
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";

    public RSAReader(final Reader reader) {
        super(reader);
    }

    public byte[] readKey() throws IOException {
        String line = readLine();

        while (line != null && !line.startsWith(BEGIN)) {
            line = readLine();
        }

        if (line != null) {
            line = line.substring(BEGIN.length());
            final int index = line.indexOf('-');
            final String type = line.substring(0, index);

            if (index > 0) {
                return loadKey(type);
            }
        }

        return null;
    }

    private byte[] loadKey(final String type) throws IOException {
        String line;
        final String endMarker = END + type;
        final StringBuilder buf = new StringBuilder();
        while ((line = readLine()) != null) {
            if (line.contains(":")) {
                continue;
            }

            if (line.contains(endMarker)) {
                break;
            }

            buf.append(line.trim());
        }

        if (line == null) {
            throw new IOException(endMarker + " not found");
        }
        return Base64.getDecoder().decode(buf.toString());
    }
}

