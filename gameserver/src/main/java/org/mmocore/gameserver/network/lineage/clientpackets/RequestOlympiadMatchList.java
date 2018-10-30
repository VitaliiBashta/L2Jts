package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReceiveOlympiad;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 0:20/09.04.2011
 */
public class RequestOlympiadMatchList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        // trigger
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd()) {
            player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return;
        }

        player.sendPacket(new ExReceiveOlympiad.MatchList());
    }
}
