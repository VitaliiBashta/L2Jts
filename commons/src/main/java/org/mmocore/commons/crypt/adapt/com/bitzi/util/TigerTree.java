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

// This public domain class has been modified by jonelo for Jacksum (GPL)
// - changed the package name
// - removed the main method
// - removed System.exit(0)
// - provided a named constructor to be able to calculate both
//   tiger and tiger2
// - replaced the cryptix Tiger provider with GNU's implementation
// - replaced MessageDigest with the AbstractChecksum
// - minor bug in blockUpdate() fixed
// - code reformatted

/* (PD) 2003 The Bitzi Corporation
 * Please see http://bitzi.com/publicdomain for more info.
 *
 * $Id: TigerTree.java,v 1.2 2003/04/13 03:22:15 gojomo Exp $
 */
package org.mmocore.commons.crypt.adapt.com.bitzi.util;

import org.mmocore.commons.crypt.JacksumAPI;
import org.mmocore.commons.crypt.algorithm.AbstractChecksum;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of THEX tree hash algorithm, with Tiger
 * as the internal algorithm (using the approach as revised
 * in December 2002, to add unique prefixes to leaf and node
 * operations)
 * <p/>
 * For simplicity, calculates one entire generation before
 * starting on the next. A more space-efficient approach
 * would use a stack, and calculate each node as soon as
 * its children ara available.
 */
public class TigerTree extends MessageDigest {
    private static final int BLOCKSIZE = 1024;
    private static final int HASHSIZE = 24;

    /**
     * 1024 byte buffer
     */
    private final byte[] buffer;

    /**
     * Buffer offset
     */
    private int bufferOffset;

    /**
     * Internal Tiger MD instance
     */
    private AbstractChecksum tiger;

    /**
     * Interim tree node hash values
     */
    private CopyOnWriteArrayList<byte[]> nodes;

    /**
     * Constructor
     */
    public TigerTree(final String name) throws NoSuchAlgorithmException {
        super(name);
        buffer = new byte[BLOCKSIZE];
        bufferOffset = 0;
        tiger = JacksumAPI.getChecksumInstance(name);
        nodes = new CopyOnWriteArrayList<>();
    }

    @Override
    protected int engineGetDigestLength() {
        return HASHSIZE;
    }

    @Override
    protected void engineUpdate(final byte in) {
        buffer[bufferOffset++] = in;
        if (bufferOffset == BLOCKSIZE) {
            blockUpdate();
            bufferOffset = 0;
        }
    }

    @Override
    protected void engineUpdate(final byte[] in, int offset, int length) {

        int remaining;
        while (length >= (remaining = BLOCKSIZE - bufferOffset)) {
            System.arraycopy(in, offset, buffer, bufferOffset, remaining);
            bufferOffset += remaining;
            blockUpdate();
            length -= remaining;
            offset += remaining;
            bufferOffset = 0;
        }

        System.arraycopy(in, offset, buffer, bufferOffset, length);
        bufferOffset += length;
    }


    @Override
    protected byte[] engineDigest() {
        final byte[] hash = new byte[HASHSIZE];
        try {
            engineDigest(hash, 0, HASHSIZE);
        } catch (DigestException e) {
            return null;
        }
        return hash;
    }


    @Override
    protected int engineDigest(final byte[] buf, final int offset, final int len)
            throws DigestException {
        if (len < HASHSIZE) {
            throw new DigestException();
        }

        // hash any remaining fragments
        blockUpdate();

        // composite neighboring nodes together up to top value
        while (nodes.size() > 1) {
            final CopyOnWriteArrayList<byte[]> newNodes = new CopyOnWriteArrayList<>();
            final Iterator<byte[]> iterator = nodes.iterator();
            while (iterator.hasNext()) {
                final byte[] left = iterator.next();
                if (iterator.hasNext()) {
                    final byte[] right = iterator.next();
                    tiger.reset();
                    tiger.update((byte) 1); // node prefix
                    tiger.update(left);
                    tiger.update(right);
                    newNodes.add(tiger.getByteArray());
                } else {
                    newNodes.add(left);
                }
            }
            nodes = newNodes;
        }
        System.arraycopy(nodes.get(0), 0, buf, offset, HASHSIZE);
        engineReset();
        return HASHSIZE;
    }


    @Override
    protected void engineReset() {
        bufferOffset = 0;
        nodes = new CopyOnWriteArrayList<>();
        tiger.reset();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Update the internal state with a single block of size 1024
     * (or less, in final block) from the internal buffer.
     */
    protected void blockUpdate() {
        tiger.reset();
        tiger.update((byte) 0); // leaf prefix
        tiger.update(buffer, 0, bufferOffset);
        if ((bufferOffset == 0) && (nodes.size() > 0))  // fix by jonelo
        {
            return; // don't remember a zero-size hash except at very beginning
        }
        nodes.add(tiger.getByteArray());
    }

}
