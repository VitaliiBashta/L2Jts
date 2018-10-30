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
 * MDgnu is a wrapper class for accessing MessageDigests from the
 * GNU crypto project http://www.gnu.org/software/classpathx/crypto
 *
 *****************************************************************************/
package org.mmocore.commons.crypt.algorithm;

import org.mmocore.commons.crypt.adapt.gnu.crypto.hash.HashFactory;
import org.mmocore.commons.crypt.adapt.gnu.crypto.hash.IMessageDigest;

import java.security.NoSuchAlgorithmException;


/**
 * A wrapper class that can be used to compute HAVAL, MD2, MD4, MD5, RIPEMD128, RIPEMD160,
 * SHA1, SHA256, SHA384, SHA512, TIGER, WHIRLPOOL-1 (provided by GNU Crypto) and
 * SHA0, SHA224, TIGER128, TIGER160, TIGER2, WHIRLPOOL-0 and WHIRLPOOL (provided by jonelo).
 */
public class MDgnu extends AbstractChecksum {
    private IMessageDigest md = null;
    private boolean virgin = true;
    private byte[] digest = null;

    /**
     * Creates new MDgnu
     */
    public MDgnu(final String arg) throws NoSuchAlgorithmException {
        // value0; we don't use value, we use md
        length = 0;
        filename = null;
        separator = " ";
        encoding = HEX;
        md = HashFactory.getInstance(arg);
        if (md == null) {
            throw new NoSuchAlgorithmException(arg + " is an unknown algorithm.");
        }
        virgin = true;
    }

    @Override
    public void reset() {
        md.reset();
        length = 0;
        virgin = true;
    }

    @Override
    public void update(final byte[] buffer, final int offset, final int len) {
        md.update(buffer, offset, len);
        length += len;
    }

    @Override
    public void update(final byte b) {
        md.update(b);
        length++;
    }

    @Override
    public void update(final int b) {
        update((byte) (b & 0xFF));
    }

    public String toString() {
        return getFormattedValue() + separator +
                (isTimestampWanted() ? getTimestampFormatted() + separator : "") +
                getFilename();
    }

    @Override
    public byte[] getByteArray() {
        if (virgin) {
            digest = md.digest();
            virgin = false;
        }
        // we don't expose internal representations
        final byte[] save = new byte[digest.length];
        System.arraycopy(digest, 0, save, 0, digest.length);
        return save;
    }

}
