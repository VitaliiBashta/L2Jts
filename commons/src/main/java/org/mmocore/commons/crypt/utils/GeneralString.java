/******************************************************************************
 *
 * Sugar for Java 1.3.0
 * Copyright (C) 2001-2005  Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author jonelo@jonelo.de
 *
 * 01-May-2002: initial release
 *
 * 06-Jul-2002: bug fixed (replaceString and replaceAllString do not work if
 *              oldString starts at pos 0)
 *
 * 09-Mar-2003: bug fixed (endless loop in replaceAllStrings, if oldString is
 *              part of newString), testcases:
 *              replaceAllStrings("aaa","a","abc") => abcabcabc
 *              replaceAllStrings("abbbabbbabbbb","bb","") => ababa
 *              replaceAllStrings("aaa","","b") => bababa
 *              new method: removeAllStrings()
 *
 * 08-May-2004: added encodeUnicode() and decodeEncodedUnicode()
 *
 * 26-May-2005: split
 *
 *****************************************************************************/
package org.mmocore.commons.crypt.utils;

public class GeneralString {
    /**
     * Creates new GeneralString
     */
    public GeneralString() {
    }

    /**
     * Replaces all oldStrings found within source by newString
     */
    public static void replaceAllStrings(final StringBuilder source, final String oldString, final String newString) {
        int idx = source.length();
        final int offset = oldString.length();

        while ((idx = source.toString().lastIndexOf(oldString, idx - 1)) > -1) {
            source.replace(idx, idx + offset, newString);
        }
    }
}
