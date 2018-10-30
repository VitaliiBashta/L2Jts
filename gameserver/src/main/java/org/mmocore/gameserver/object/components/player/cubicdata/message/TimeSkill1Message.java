package org.mmocore.gameserver.object.components.player.cubicdata.message;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Create by Mangol on 23.12.2015.
 */
public enum TimeSkill1Message implements ITimeSkillMessage {
    _0(0) {
        @Override
        public Optional<SystemMessage> hours() {
            return Optional.of(new SystemMessage(SystemMsg.THE_FIRST_GIFTS_REMAINING_RESUPPLY_TIME_IS_S1_HOURS_S2_MINUTES_S3_SECONDS));
        }

        @Override
        public Optional<SystemMessage> minutes() {
            return Optional.of(new SystemMessage(SystemMsg.THE_FIRST_GIFTS_REMAINING_RESUPPLY_TIME_IS_S1_MINUTES_S2_SECONDS));
        }

        @Override
        public Optional<SystemMessage> seconds() {
            return Optional.of(new SystemMessage(SystemMsg.THE_FIRST_GIFTS_REMAINING_RESUPPLY_TIME_IS_S1_SECONDS));
        }
    },
    _6239(6239) {
        @Override
        public Optional<SystemMessage> hours() {
            return Optional.of(new SystemMessage(SystemMsg.YOU_HAVE_S1_HOURS_S2_MINUTES_AND_S3_SECONDS_LEFT_IN_THE_PROOF_OF_SPACE_AND_TIME));
        }

        @Override
        public Optional<SystemMessage> minutes() {
            return Optional.of(new SystemMessage(SystemMsg.YOU_HAVE_S1_MINUTES_AND_S2_SECONDS_LEFT_IN_THE_PROOF_OF_SPACE_AND_TIME));
        }

        @Override
        public Optional<SystemMessage> seconds() {
            return Optional.of(new SystemMessage(SystemMsg.YOU_HAVE_S1_SECONDS_LEFT_IN_THE_PROOF_OF_SPACE_AND_TIME));
        }
    },
    _6685(6685) {
        @Override
        public Optional<SystemMessage> minutes() {
            return Optional.of(new SystemMessage(SystemMsg.THE_GIFTS_REMAINING_RESUPPLY_TIME_IS_S1_MINUTES_S2_SECONDS));
        }

        @Override
        public Optional<SystemMessage> seconds() {
            return Optional.of(new SystemMessage(SystemMsg.THE_GIFTS_REMAINING_RESUPPLY_TIME_IS_S1_SECONDS));
        }
    };

    private final int idMessage;

    TimeSkill1Message(final int idMessage) {
        this.idMessage = idMessage;
    }

    public static Optional<TimeSkill1Message> valueOf(final int idMessage) {
        for (final TimeSkill1Message m : values()) {
            if (m.getIdMessage() == idMessage) {
                return Optional.of(m);
            }
        }
        throw new NoSuchElementException("Not find TimeSkill1Message by id: " + idMessage);
    }

    public int getIdMessage() {
        return idMessage;
    }
}
