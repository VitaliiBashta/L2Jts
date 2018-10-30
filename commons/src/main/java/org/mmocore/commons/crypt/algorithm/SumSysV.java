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

// implemented in Java from original GNU C source
public class SumSysV extends AbstractChecksum {

    public SumSysV() {
        super();
        separator = " ";
    }

    @Override
    public void update(final int b) {
        value += b & 0xFF;
        length++;
    }

    @Override
    public void update(final byte b) {
        value += b & 0xFF;
        length++;
    }

    @Override
    public long getValue() {
        final long r = (value & 0xffff) + (((value) >> 16) & 0xffff);
        value = (r & 0xffff) + (r >> 16);
        return value;
    }

    public String toString() {
        final long kb = (length + 511) / 512;
        return getFormattedValue() + separator + kb + separator +
                (isTimestampWanted() ? getTimestampFormatted() + separator : "") +
                filename;
    }

    @Override
    public byte[] getByteArray() {
        final long val = getValue();
        return new byte[]{(byte) ((val >> 8) & 0xff), (byte) (val & 0xff)};
    }

}
