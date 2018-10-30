/******************************************************************************
 *
 * Jacksum version 1.7.0 - checksum utility in Java
 * Copyright (C) 2001-2006 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: jonelo@jonelo.de
 *
 *****************************************************************************/

package org.mmocore.commons.crypt.algorithm;

import org.mmocore.commons.crypt.Service;
import org.mmocore.commons.crypt.utils.Base32;
import org.mmocore.commons.crypt.utils.EncodingException;
import org.mmocore.commons.crypt.utils.GeneralString;

import java.io.File;
import java.math.BigInteger;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Checksum;

/**
 * An abstract class that is actually the parent of all algorithms.
 */
public abstract class AbstractChecksum implements Checksum {
    public static final String BIN = "bin";
    public static final String DEC = "dec";
    public static final String OCT = "oct";
    public static final String HEX = "hex";
    public static final String HEX_UPPERCASE = "hexup";
    public static final String BASE16 = "base16";
    public static final String BASE32 = "base32";
    public static final String BASE64 = "base64";

    protected long value;
    protected long length;
    protected String separator;
    protected String filename;
    protected String encoding;
    protected int group; // grouping of hex digits
    protected char groupChar; // group char, blank by default

    protected String timestampFormat;
    protected DateTimeFormatter timestampFormatter;
    protected long timestamp;
    protected String name;


    /**
     * Creates an AbstractChecksum.
     */
    public AbstractChecksum() {
        value = 0;
        length = 0;
        separator = "\t";
        filename = null;
        encoding = "";
        timestampFormat = null;
        timestampFormatter = null;
        timestamp = 0;
        group = 0;
        groupChar = ' ';
        name = null;
    }

    public String getName() {
        return name;
    }

    /**
     * Set the name of the algorithm
     *
     * @param name the name of the algorithm
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Resets the checksum to its initial value for further use.
     */
    // from the Checksum interface
    @Override
    public void reset() {
        value = 0;
        length = 0;
    }

    /**
     * Updates the checksum with the specified byte.
     */
    // from the Checksum interface
    @Override
    public void update(final int b) {
        length++;
    }

    /**
     * Updates the checksum with the specified byte.
     */
    public void update(final byte b) {
        update(b & 0xFF);
    }

    /**
     * Updates the current checksum with the specified array of bytes.
     *
     * @param bytes  the byte array to update the checksum with
     * @param offset the start offset of the data
     * @param length the number of bytes to use for the update
     */
    // from the Checksum interface
    @Override
    public void update(final byte[] bytes, final int offset, final int length) {
        for (int i = offset; i < length + offset; i++) {
            update(bytes[i]);
        }
    }

    /**
     * Updates the current checksum with the specified array of bytes.
     */
    public void update(final byte[] bytes) {
        update(bytes, 0, bytes.length);
    }

    /**
     * Returns the value of the checksum.
     *
     * @see #getByteArray()
     */
    @Override
    public long getValue() {
        return value;
    }

    /**
     * Returns the length of the processed bytes.
     */
    public long getLength() {
        return length;
    }

    /**
     * Gets the separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Sets the separator for the tokens.
     */
    public void setSeparator(final String separator) {
        this.separator = separator;
    }

    /**
     * Returns the result of the computation as byte array.
     *
     * @since Jacksum 1.6
     */
    public byte[] getByteArray() {
        return new byte[]{(byte) (value & 0xff)};
    }

    /**
     * The toString() method.
     */
    public String toString() {
        return getFormattedValue() + separator +
                length + separator +
                (isTimestampWanted() ? getTimestampFormatted() + separator : "") +
                filename;
    }

    /**
     * Returns true only if the specified checksum is equal to this object.
     */
    public boolean equals(final Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof AbstractChecksum) {
            final AbstractChecksum abstractChecksum = (AbstractChecksum) anObject;
            return Arrays.equals(getByteArray(), abstractChecksum.getByteArray());
        }
        return false;
    }

    public int hashCode() {
        // let's do a very primitive hash rather than just a sum
        // let's also avoid circular dependencies among classes
        // let's also avoid casts, let's use shifts for performance
        // and prims for better security
        final byte[] b = getByteArray();
        int s = 0;
        for (int i = 0; i < b.length; i++) {
            s = ((s << 8) + (b[i] & 0xFF)) % 0x7FFFF1; // is prim
        }
        return s;
    }

    /**
     * Returns the checksum formatted.
     *
     * @since Jacksum 1.6
     */
    public String getFormattedValue() {
        if (encoding.equalsIgnoreCase(HEX)) {
            //return getHexValue();
            return Service.format(getByteArray(), false, group, groupChar);
        } else if (encoding.equalsIgnoreCase(HEX_UPPERCASE)) {
            return Service.format(getByteArray(), true, group, groupChar);
        } else if (encoding.equalsIgnoreCase(BASE16)) {
            return Service.format(getByteArray(), true, 0, groupChar);
        } else if (encoding.equalsIgnoreCase(BASE32)) {
            return Base32.encode(getByteArray());
        } else if (encoding.equalsIgnoreCase(BASE64)) {
            return Base64.getEncoder().encodeToString(getByteArray());
        } else if (encoding.equalsIgnoreCase(DEC)) {
            final BigInteger big = new BigInteger(1, getByteArray());
            return big.toString();
        } else if (encoding.equalsIgnoreCase(BIN)) {
            return Service.formatAsBits(getByteArray());
        } else if (encoding.equalsIgnoreCase(OCT)) {
            final BigInteger big = new BigInteger(1, getByteArray());
            return big.toString(8);
        } else
        // default
        {
            return Long.toString(getValue()); // String.valueOf(checksum.getValue())
        }
    }

    // with this method, the format() method can be customized
    public void firstFormat(final StringBuilder format) {
        // checksum
        GeneralString.replaceAllStrings(format, "#FINGERPRINT", "#CHECKSUM");
    }

    // will be launched by the CLI option called -F
    public String format(final String format) {
        final StringBuilder temp = new StringBuilder(format);
        firstFormat(temp);
        GeneralString.replaceAllStrings(temp, "#CHECKSUM{i}", "#CHECKSUM");
        GeneralString.replaceAllStrings(temp, "#ALGONAME{i}", "#ALGONAME");

        GeneralString.replaceAllStrings(temp, "#ALGONAME", getName());
        // counter
        // temp = GeneralString.replaceAllStrings(temp, "#COUNTER", getCounter() );
        GeneralString.replaceAllStrings(temp, "#CHECKSUM", getFormattedValue());
        // filesize
        GeneralString.replaceAllStrings(temp, "#FILESIZE", Long.toString(length));
        // filename
        if (temp.toString().contains("#FILENAME{")) { // comatibility to 1.3
            final File filetemp = new File(filename);
            GeneralString.replaceAllStrings(temp, "#FILENAME{NAME}", filetemp.getName());
            String parent = filetemp.getParent();
            if (parent == null) {
                parent = "";
            } else if (!parent.endsWith(File.separator) &&
                    // for files on a different drive where the working dir has changed
                    (!parent.endsWith(":") && System.getProperty("os.name").toLowerCase().startsWith("windows"))) {
                parent += File.separator;
            }
            GeneralString.replaceAllStrings(temp, "#FILENAME{PATH}", parent);
        }
        GeneralString.replaceAllStrings(temp, "#FILENAME", filename);
        // timestamp
        if (isTimestampWanted()) {
            GeneralString.replaceAllStrings(temp, "#TIMESTAMP", getTimestampFormatted());
        }
        // sepcial chars
        GeneralString.replaceAllStrings(temp, "#SEPARATOR", separator);
        GeneralString.replaceAllStrings(temp, "#QUOTE", "\"");
        return temp.toString();
    }

    /**
     * Gets the filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename.
     *
     * @param filename the filename.
     */
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    /**
     * Gets the encoding of the checksum.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding of the checksum.
     *
     * @param encoding the encoding of the checksum.
     * @since 1.6
     */
    public void setEncoding(final String encoding) throws EncodingException {
        if (encoding == null) {
            this.encoding = ""; // default
        } else if ((encoding.isEmpty()) || // empty string
                encoding.equalsIgnoreCase(HEX) ||
                encoding.equalsIgnoreCase(HEX_UPPERCASE) ||
                encoding.equalsIgnoreCase(DEC) ||
                encoding.equalsIgnoreCase(BIN) ||
                encoding.equalsIgnoreCase(OCT) ||
                encoding.equalsIgnoreCase(BASE16) ||
                encoding.equalsIgnoreCase(BASE32) ||
                encoding.equalsIgnoreCase(BASE64)) {
            this.encoding = encoding;
        } else

        {
            throw new EncodingException("Encoding is not supported");
        }
    }

    /**
     * Gets the number of groups (make sense only if encoding is HEX or HEXUP).
     */
    public int getGroup() {
        return group;
    }

    /**
     * Sets the number of groups (make sense only if encoding is HEX or HEXUP).
     */
    public void setGroup(final int group) {
        this.group = group;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param filename the file from which the timestamp should be gathered.
     */
    public void setTimestamp(final String filename) {
        final File file = new File(filename);
        this.timestamp = file.lastModified();
    }

    /**
     * Gets the timestamp, formatted.
     */
    public String getTimestampFormatted() {
        if (timestampFormatter == null)
            timestampFormatter = DateTimeFormatter.ofPattern(timestampFormat);

        final Instant instant = Instant.ofEpochMilli(timestamp);
        return timestampFormatter.format(instant);
    }

    /**
     * Determines if a timestamp is wanted.
     */
    public boolean isTimestampWanted() {
        return timestampFormat != null;
    }
}
