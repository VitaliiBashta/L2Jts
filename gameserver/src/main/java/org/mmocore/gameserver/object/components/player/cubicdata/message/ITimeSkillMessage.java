package org.mmocore.gameserver.object.components.player.cubicdata.message;

import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;

/**
 * Create by Mangol on 23.12.2015.
 */
public interface ITimeSkillMessage {
    default Optional<SystemMessage> hours() {
        return Optional.empty();
    }

    default Optional<SystemMessage> minutes() {
        return Optional.empty();
    }

    default Optional<SystemMessage> seconds() {
        return Optional.empty();
    }

    default void sendMessage(final Player player, final long[] time) {
        final long hours = time[0];
        final long minutes = time[1];
        final long seconds = time[2];
        final Optional<SystemMessage> message;
        if (hours > 0) {
            message = hours();
            player.sendPacket(message.get().addNumber(hours).addNumber(minutes).addNumber(seconds));
        } else if (minutes > 0) {
            message = minutes();
            player.sendPacket(message.get().addNumber(minutes).addNumber(seconds));
        } else if (seconds > 0) {
            message = seconds();
            player.sendPacket(message.get().addNumber(seconds));
        }
    }
}
