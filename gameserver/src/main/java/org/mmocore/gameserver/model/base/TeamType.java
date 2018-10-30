package org.mmocore.gameserver.model.base;

import java.util.Arrays;

/**
 * @author VISTALL
 * @date 1:14/16.06.2011
 */
public enum TeamType {
    NONE,
    BLUE,
    RED;

    public static final TeamType[] VALUES = Arrays.copyOfRange(values(), 1, 3);
    private int points = 0;

    public int ordinalWithoutNone() {
        return ordinal() - 1;
    }

    public TeamType revert() {
        return this == BLUE ? RED : this == RED ? BLUE : NONE;
    }

    public int ordinal2() {
        return ordinal() - 1;
    }

    public int getPoints() {
        return points;
    }

    public void incrementPoints() {
        points++;
    }
}
