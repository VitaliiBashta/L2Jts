package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 * TODO[K] - временное ограничение игры на релакс серверах, на офе. В паре с /fatiguetime
 */
public class RequestRemainTime extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (ServerConfig.AUTH_SERVER_BRACKETS) {
            player.sendPacket(SystemMsg.THIS_WEEK_USAGE_TIME_HAS_FINISHED);
        } else {
            player.sendPacket(SystemMsg.THIS_COMMAND_CAN_ONLY_BE_USED_IN_THE_RELAX_SERVER);
        }
    }
}