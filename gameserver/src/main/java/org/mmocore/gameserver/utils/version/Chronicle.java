package org.mmocore.gameserver.utils.version;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

public enum Chronicle {
    FREYA(207, 214, 215, 216), // ct2.5
    HIGH_FIVE(241, 243, 247, 251, 252, 253, 267, 268, 271, 273), // ct2.6 part 1-6
    GOD(389, 400, 401, 402, 403, 404, 405, 406, 415); // Goddess of Destruction

    private final int[] protocols;

    Chronicle(int obfuscationLength, int... range) {
        protocols = range;
    }

    public int[] getProtocols() {
        return protocols;
    }

    public boolean isInRange(int val) {
        return IntStream.of(protocols).anyMatch( a -> a == val);
//        return Arrays.asList(protocols).stream().contains(protocols, val);
    }
}