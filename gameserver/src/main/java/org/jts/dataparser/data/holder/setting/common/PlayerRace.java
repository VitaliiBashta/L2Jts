package org.jts.dataparser.data.holder.setting.common;

import java.util.Objects;
import java.util.Optional;

/**
 * This class defines all races (human, elf, darkelf, orc, dwarf) that a player can chose.<BR><BR>
 */
public enum PlayerRace {
    human,
    elf,
    darkelf,
    orc,
    dwarf,
    kamael;

    public static Optional<PlayerRace> value(final String name) {
        for (final PlayerRace playerRace : PlayerRace.values()) {
            if (Objects.equals(playerRace.name(), name)) {
                return Optional.of(playerRace);
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return ordinal();
    }
}
