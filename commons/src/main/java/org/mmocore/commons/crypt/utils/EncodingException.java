/**
 * Sugar for Java 1.3.0
 * Copyright (C) 2001-2005  Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author jonelo@jonelo.de
 * <p>
 * <p>
 * EncodingException.java
 */

/**
 * EncodingException.java
 */

package org.mmocore.commons.crypt.utils;

/**
 * Thrown to indicate that the application has attempted to specify
 * a non supported encoding
 */
public class EncodingException extends IllegalArgumentException {
    /**
     * Constructs a <code>EncodingException</code> with the
     * specified detail message.
     *
     * @param s
     * 		the detail message.
     */
    public EncodingException(final String s) {
        super(s);
    }

}
