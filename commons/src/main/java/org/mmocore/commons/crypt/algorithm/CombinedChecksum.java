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

import org.mmocore.commons.crypt.JacksumAPI;
import org.mmocore.commons.crypt.utils.EncodingException;
import org.mmocore.commons.crypt.utils.GeneralString;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jonelo
 */
public class CombinedChecksum extends AbstractChecksum {
    private CopyOnWriteArrayList<AbstractChecksum> algorithms;

    /**
     * Creates a new instance of CombinedChecksum
     */
    public CombinedChecksum() {
        init();
    }

    public CombinedChecksum(final String[] algos, final boolean alternate) throws NoSuchAlgorithmException {
        init();
        setAlgorithms(algos, alternate);
    }

    private void init() {
        algorithms = new CopyOnWriteArrayList<>();
        length = 0;
        filename = null;
        separator = " ";
        encoding = HEX;
    }

    public void addAlgorithm(final AbstractChecksum checksum, final String algorithm) {
        checksum.setName(algorithm);
        algorithms.add(checksum);
    }

    public void setAlgorithms(final String[] algos, final boolean alternate) throws NoSuchAlgorithmException {
        for (final String algo : algos) {
            addAlgorithm(JacksumAPI.getChecksumInstance(algo, alternate), algo);
        }
    }

    @Override
    public void reset() {
        // for all algorithms
        for (final AbstractChecksum algorithm : algorithms) {
            algorithm.reset();
        }
        length = 0;
    }

    /**
     * Updates all checksums with the specified byte.
     */
    @Override
    public void update(final int b) {
        for (int i = 0; i < algorithms.size(); i++) {
            algorithms.get(i).update(i);
        }
        length++;
    }

    /**
     * Updates all checksums with the specified byte.
     */
    @Override
    public void update(final byte b) {
        for (final AbstractChecksum algorithm : algorithms) {
            algorithm.update(b);
        }
        length++;
    }

    /**
     * Updates all checksums with the specified array of bytes.
     */
    @Override
    public void update(final byte[] bytes, final int offset, final int length) {
        for (final AbstractChecksum algorithm : algorithms) {
            algorithm.update(bytes, offset, length);
        }
        this.length += length;
    }

    /**
     * Updates all checksums with the specified array of bytes.
     */
    @Override
    public void update(final byte[] bytes) {
        for (final AbstractChecksum algorithm : algorithms) {
            algorithm.update(bytes);
        }
        this.length += bytes.length;
    }

    /**
     * Returns the result of the computation as byte array.
     */
    @Override
    public byte[] getByteArray() {
        final CopyOnWriteArrayList<byte[]> v = new CopyOnWriteArrayList<>();
        int size = 0;
        for (final AbstractChecksum algorithm : algorithms) {
            final byte[] tmp = algorithm.getByteArray();
            v.add(tmp);
            size += tmp.length;
        }
        final byte[] ret = new byte[size];
        int offset = 0;
        for (final byte[] src : v) {
            System.arraycopy(src, 0, ret, offset, src.length);
            offset += src.length;
        }
        return ret;
    }

    /**
     * with this method the format() method can be customized, it will be launched at the beginning of format()
     */
    @Override
    public void firstFormat(final StringBuilder formatBuf) {
        // normalize the checksum code token
        GeneralString.replaceAllStrings(formatBuf, "#FINGERPRINT", "#CHECKSUM");

        // normalize the output of every algorithm
        setEncoding(encoding);

        final StringBuilder buf = new StringBuilder();
        final String format = formatBuf.toString();

        if (format.contains("#CHECKSUM{i}") || format.contains("#ALGONAME{i}")) {
            for (int i = 0; i < algorithms.size(); i++) {
                final StringBuilder line = new StringBuilder(format);
                GeneralString.replaceAllStrings(line, "#CHECKSUM{i}", algorithms.get(i).getFormattedValue());
                GeneralString.replaceAllStrings(line, "#ALGONAME{i}", algorithms.get(i).getName());
                buf.append(line);
                if (algorithms.size() > 1) {
                    buf.append('\n');
                }
            }
        } else {
            buf.append(format);
        }

        // are there still tokens to be transformed ?
        if (buf.toString().contains("#CHECKSUM{")) {
            // replace CHECKSUM{1} to {CHECKSUM{n}
            for (int i = 0; i < algorithms.size(); i++) {
                GeneralString.replaceAllStrings(buf, "#CHECKSUM{" + i + '}', algorithms.get(i).getFormattedValue());
            }
        }

        if (buf.toString().contains("#ALGONAME{")) {
            // replace ALGONAME{1} to {ALGONAME{n}
            for (int i = 0; i < algorithms.size(); i++) {
                GeneralString.replaceAllStrings(buf, "#ALGONAME{" + i + '}', algorithms.get(i).getName());
            }
        }
        formatBuf.setLength(0);
        formatBuf.append(buf.toString());
    }

    /**
     * Sets the encoding of the checksum.
     *
     * @param encoding the encoding of the checksum.
     */
    @Override
    public void setEncoding(final String encoding) throws EncodingException {
        for (final AbstractChecksum algorithm : algorithms) {
            algorithm.setEncoding(encoding);
        }
        this.encoding = algorithms.get(0).getEncoding();
    }

}
