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

package org.mmocore.commons.crypt;

import org.mmocore.commons.crypt.adapt.gnu.crypto.Registry;
import org.mmocore.commons.crypt.algorithm.*;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * This is the Jacksum Application Program Interface (API).
 * Use this API to get an instance of an algorithm and to
 * determine both the available algorithms and available encodings
 * for the checksum.
 */
public class JacksumAPI {
    /**
     * Gets all available algorithms.
     *
     * @return a Map with key and value pairs, both are Strings
     * (the key can be used to feed the method getChecksumInstance(),
     * the value of the pair is the name of the algorithm
     * which can be used in a GUI for example)
     */
    public static Map<String, String> getAvailableAlgorithms() {
        final Map<String, String> map = new TreeMap<>();
        map.put("adler32", "Adler 32");
        map.put("cksum", "cksum (Unix)");
        map.put("crc8", "CRC-8 (FLAC)");
        map.put("crc16", "CRC-16 (LHA/ARC)");
        map.put("crc24", "CRC-24 (Open PGP)");
        map.put("crc64", "CRC-64 (ISO 3309)");
        map.put("crc32", "CRC-32 (FCS-32)");
        map.put("crc32_mpeg2", "CRC-32 (MPEG-2)");
        map.put("crc32_bzip2", "CRC-32 (BZIP2)");
        map.put("ed2k", "ed2k");
        map.put("elf", "Elf");
        map.put("fcs16", "FCS-16");
        map.put("has160", "HAS-160");
        map.put("haval_128_3", "HAVAL 128 (3 rounds)");
        map.put("haval_128_4", "HAVAL 128 (4 rounds)");
        map.put("haval_128_5", "HAVAL 128 (5 rounds)");
        map.put("haval_160_3", "HAVAL 160 (3 rounds)");
        map.put("haval_160_4", "HAVAL 160 (4 rounds)");
        map.put("haval_160_5", "HAVAL 160 (5 rounds)");
        map.put("haval_192_3", "HAVAL 192 (3 rounds)");
        map.put("haval_192_4", "HAVAL 192 (4 rounds)");
        map.put("haval_192_5", "HAVAL 192 (5 rounds)");
        map.put("haval_224_3", "HAVAL 224 (3 rounds)");
        map.put("haval_224_4", "HAVAL 224 (4 rounds)");
        map.put("haval_224_5", "HAVAL 224 (5 rounds)");
        map.put("haval_256_3", "HAVAL 256 (3 rounds)");
        map.put("haval_256_4", "HAVAL 256 (4 rounds)");
        map.put("haval_256_5", "HAVAL 256 (5 rounds)");
        map.put("md2", "MD2");
        map.put("md4", "MD4");
        map.put("md5", "MD5");
        map.put("sha0", "SHA-0");
        map.put("sha1", "SHA-1 (SHA-160)");
        map.put("sha224", "SHA-2 (SHA-224)");
        map.put("sha256", "SHA-2 (SHA-256)");
        map.put("sha384", "SHA-2 (SHA-384)");
        map.put("sha512", "SHA-2 (SHA-512)");
        map.put("sumbsd", "sum (BSD Unix)");
        map.put("sumsysv", "sum (System V Unix)");
        map.put("sum8", "sum 8");
        map.put("sum16", "sum 16");
        map.put("sum24", "sum 24");
        map.put("sum32", "sum 32");
        map.put("tiger128", "Tiger/128");
        map.put("tiger160", "Tiger/160");
        map.put("tiger", "Tiger (Tiger/192)");
        map.put("tiger2", "Tiger2");
        map.put("tree:tiger", "Tiger Tree Hash");
        map.put("tree:tiger2", "Tiger2 Tree Hash");
        map.put("whirlpool0", "Whirlpool-0");
        map.put("whirlpool1", "Whirlpool-1");
        map.put("whirlpool2", "Whirlpool");
        map.put("xor8", "XOR 8");
        return map;
    }


    /**
     * Gets an object of a checksum algorithm.
     * It always tries to use implementations from the Java API
     *
     * @param algorithm code for the checksum algorithm
     * @return a checksum algorithm object
     * @throws java.security.NoSuchAlgorithmException if algorithm is unknown
     */
    public static AbstractChecksum getChecksumInstance(final String algorithm) throws NoSuchAlgorithmException {
        return getChecksumInstance(algorithm, false);
    }


    /**
     * Gets an object of a checksum algorithm.
     *
     * @param algorithm code for the checksum algorithm
     * @param alternate a pure Java implementation is preferred
     * @return a checksum algorithm object
     * @throws java.security.NoSuchAlgorithmException if algorithm is unknown
     */
    public static AbstractChecksum getChecksumInstance(String algorithm, final boolean alternate) throws NoSuchAlgorithmException {
        final AbstractChecksum checksum;

        // a combined hash algorithm (must be the first if clause)
        if (algorithm.contains("+")) {
            final String[] codes = algorithm.split("+"); // we need compatibility with JRE 1.3
            checksum = new CombinedChecksum(codes, alternate);

            // most popular algorithms first
        } else if ("sha1".equals(algorithm) || "sha".equals(algorithm) || "sha-1".equals(algorithm) ||
                "sha160".equals(algorithm) || "sha-160".equals(algorithm)) {
            checksum = new MD("SHA-1");
        } else if ("crc32".equals(algorithm) || "crc-32".equals(algorithm) ||
                "fcs32".equals(algorithm) || "fcs-32".equals(algorithm)) {
            checksum = new Crc32();
        } else if ("md5".equals(algorithm) || "md5sum".equals(algorithm)) {
            checksum = new MD("MD5");
        } else if ("cksum".equals(algorithm)) {
            checksum = new Cksum();
        } else if ("sumbsd".equals(algorithm) || "bsd".equals(algorithm) || "bsdsum".equals(algorithm)) {
            checksum = new SumBSD();
        } else if ("sumsysv".equals(algorithm) || "sysv".equals(algorithm) || "sysvsum".equals(algorithm)) {
            checksum = new SumSysV();
        } else if ("adler32".equals(algorithm) || "adler-32".equals(algorithm)) {
            checksum = new Adler32();
        } else if ("crc32_mpeg2".equals(algorithm) || "crc-32_mpeg-2".equals(algorithm)) {
            checksum = new Crc32Mpeg2();
        }

        /* we use versions provided by the JRE (supported since 1.4.2) if possible
           see http://java.sun.com/j2se/1.4.2/changes.html#security
           and http://java.sun.com/j2se/1.4.2/docs/guide/security/CryptoSpec.html#AppA
         */
        else if ("sha256".equals(algorithm) || "sha-256".equals(algorithm)) {
            checksum = new MD("SHA-256");
        } else if ("sha384".equals(algorithm) || "sha-384".equals(algorithm)) {
            checksum = new MD("SHA-384");
        } else if ("sha512".equals(algorithm) || "sha-512".equals(algorithm)) {
            checksum = new MD("SHA-512");
        } else if ("sha224".equals(algorithm) || "sha-224".equals(algorithm)) {
            checksum = new MDgnu(Registry.SHA224_HASH);
        } else if ("tiger".equals(algorithm) || "tiger192".equals(algorithm) || "tiger-192".equals(algorithm)) {
            checksum = new MDgnu(Registry.TIGER_HASH);
        } else if ("tree:tiger".equals(algorithm)) {
            checksum = new MDTree("tiger");
        } else if ("tree:tiger2".equals(algorithm)) {
            checksum = new MDTree("tiger2");
        } else if ("tiger160".equals(algorithm) || "tiger-160".equals(algorithm)) {
            checksum = new MDgnu(Registry.TIGER160_HASH);
        } else if ("tiger128".equals(algorithm) || "tiger-128".equals(algorithm)) {
            checksum = new MDgnu(Registry.TIGER128_HASH);
        } else if ("tiger2".equals(algorithm)) {
            checksum = new MDgnu(Registry.TIGER2_HASH);
        } else if (algorithm.startsWith("haval")) {
            checksum = new MDgnu(algorithm);
        } else if ("crc16".equals(algorithm) || "crc-16".equals(algorithm)) {
            checksum = new Crc16();
        } else if ("whirlpool0".equals(algorithm) || "whirlpool-0".equals(algorithm)) {
            checksum = new MDgnu(Registry.WHIRLPOOL2000_HASH);
        } else if ("whirlpool1".equals(algorithm) || "whirlpool-1".equals(algorithm)) {
            checksum = new MDgnu(Registry.WHIRLPOOL_HASH);
        } else if ("whirlpool2".equals(algorithm) || "whirlpool-2".equals(algorithm) || "whirlpool".equals(algorithm)) {
            checksum = new MDgnu(Registry.WHIRLPOOL2003_HASH);

        } else if ("crc64".equals(algorithm) || "crc-64".equals(algorithm)) {
            checksum = new Crc64();
        } else if ("ed2k".equals(algorithm) || "emule".equals(algorithm) || "edonkey".equals(algorithm)) {
            checksum = new Edonkey();
        } else if ("md4".equals(algorithm) || "md4sum".equals(algorithm)) {
            checksum = new MDgnu(Registry.MD4_HASH);
        } else if ("md2".equals(algorithm) || "md2sum".equals(algorithm)) {
            checksum = new MDgnu(Registry.MD2_HASH);
        } else if ("sha0".equals(algorithm) || "sha-0".equals(algorithm)) {
            checksum = new MDgnu(Registry.SHA0_HASH);
        } else if ("elf".equals(algorithm) || "elf32".equals(algorithm) || "elf-32".equals(algorithm)) {
            checksum = new Elf();
        } else if ("fcs16".equals(algorithm) || "fcs-16".equals(algorithm) ||
                "crc16_x25".equals(algorithm) || "crc-16_x-25".equals(algorithm)) {
            checksum = new FCS16();
        } else if ("crc8".equals(algorithm) || "crc-8".equals(algorithm)) {
            checksum = new Crc8();
        } else if ("crc24".equals(algorithm) || "crc-24".equals(algorithm)) {
            checksum = new CrcGeneric(24, 0x864CFB, 0xB704CEL, false, false, 0);
        } else if ("sum8".equals(algorithm) || "sum-8".equals(algorithm)) {
            checksum = new Sum8();
        } else if ("sum16".equals(algorithm) || "sum-16".equals(algorithm)) {
            checksum = new Sum16();
        } else if ("sum24".equals(algorithm) || "sum-24".equals(algorithm)) {
            checksum = new Sum24();
        } else if ("sum32".equals(algorithm) || "sum-32".equals(algorithm)) {
            checksum = new Sum32();
        } else if ("xor8".equals(algorithm) || "xor-8".equals(algorithm)) {
            checksum = new Xor8();
        } else if ("crc32_bzip2".equals(algorithm) || "crc-32_bzip-2".equals(algorithm)) {
            checksum = new CrcGeneric(32, 0x04C11DB7, 0xFFFFFFFFL, false, false, 0xFFFFFFFFL);
        } else if ("has160".equals(algorithm) || "has-160".equals(algorithm)) {
            checksum = new MDgnu(Registry.HAS160_HASH);
            // special algorithms
        } else if ("none".equals(algorithm)) {
            checksum = new None();
        } else if ("read".equals(algorithm)) {
            checksum = new Read();

            // the generic CRC
        } else if (algorithm.startsWith("crc:")) {
            checksum = new CrcGeneric(algorithm.substring(4));
            // all algorithms
        } else if ("all".equals(algorithm)) {

            final Map<String, String> map = getAvailableAlgorithms();
            final Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            final String[] codes = new String[map.entrySet().size()];
            int i = 0;
            final StringBuilder allNames = new StringBuilder();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> entry = iterator.next();
                // String description = (String)entry.getValue();
                final String name = entry.getKey();
                allNames.append(name);
                allNames.append('+');
                codes[i++] = name;
            }
            checksum = new CombinedChecksum(codes, alternate);
            allNames.deleteCharAt(allNames.length() - 1);
            algorithm = allNames.toString();
        } else { // unknown
            throw new NoSuchAlgorithmException(algorithm + " is an unknown algorithm.");
        }
        checksum.setName(algorithm);
        return checksum;
    }

}
