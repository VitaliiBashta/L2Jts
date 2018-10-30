package org.mmocore.commons.crypt.adapt.gnu.crypto;

// ----------------------------------------------------------------------------
// $Id: Registry.java,v 1.17 2003/06/14 14:44:38 raif Exp $
//
// Copyright (C) 2001, 2002, 2003 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to the
//
//    Free Software Foundation Inc.,
//    59 Temple Place - Suite 330,
//    Boston, MA 02111-1307
//    USA
//
// Linking this library statically or dynamically with other modules is
// making a combined work based on this library.  Thus, the terms and
// conditions of the GNU General Public License cover the whole
// combination.
//
// As a special exception, the copyright holders of this library give
// you permission to link this library with independent modules to
// produce an executable, regardless of the license terms of these
// independent modules, and to copy and distribute the resulting
// executable under terms of your choice, provided that you also meet,
// for each linked independent module, the terms and conditions of the
// license of that module.  An independent module is a module which is
// not derived from or based on this library.  If you modify this
// library, you may extend this exception to your version of the
// library, but you are not obligated to do so.  If you do not wish to
// do so, delete this exception statement from your version.
// ----------------------------------------------------------------------------

/**
 * A placeholder for <i>names</i> and <i>literals</i> used throughout this
 * library.
 *
 * @version $Revision: 1.17 $
 */
public interface Registry {
    // Names of properties to use in Maps when initialising primitives .........

    // Message digest algorithms and synonyms...................................

    String WHIRLPOOL_HASH = "whirlpool"; // 2001
    String WHIRLPOOL2000_HASH = "whirlpool_2000";
    String WHIRLPOOL2003_HASH = "whirlpool_2003";
    // added by jonelo
    String SHA224_HASH = "sha-224";
    String TIGER_HASH = "tiger";
    String TIGER2_HASH = "tiger2";
    String TIGER160_HASH = "tiger-160";
    String TIGER128_HASH = "tiger-128";
    String HAVAL_HASH = "haval";
    String HAS160_HASH = "has-160";

    // added by jonelo
    String SHA0_HASH = "sha-0";
    String HAVAL_HASH_128_3 = "haval_128_3";
    String HAVAL_HASH_128_4 = "haval_128_4";
    String HAVAL_HASH_128_5 = "haval_128_5";
    String HAVAL_HASH_160_3 = "haval_160_3";
    String HAVAL_HASH_160_4 = "haval_160_4";
    String HAVAL_HASH_160_5 = "haval_160_5";
    String HAVAL_HASH_192_3 = "haval_192_3";
    String HAVAL_HASH_192_4 = "haval_192_4";
    String HAVAL_HASH_192_5 = "haval_192_5";
    String HAVAL_HASH_224_3 = "haval_224_3";
    String HAVAL_HASH_224_4 = "haval_224_4";
    String HAVAL_HASH_224_5 = "haval_224_5";
    String HAVAL_HASH_256_3 = "haval_256_3";
    String HAVAL_HASH_256_4 = "haval_256_4";
    String HAVAL_HASH_256_5 = "haval_256_5";
    // end

    String MD4_HASH = "md4";
    String MD2_HASH = "md2";
}
