package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.CastleSiegeInfo;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestSetCastleSiegeTime extends L2GameClientPacket {
    private int _id, _time;

    @Override
    protected void readImpl() {
        _id = readD();
        _time = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _id);
        if (castle == null) {
            return;
        }

        if (player.getClan().getCastle() != castle.getId()) {
            return;
        }

        if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME);
            return;
        }

        final CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

        siegeEvent.setNextSiegeTime(_time);

        player.sendPacket(new CastleSiegeInfo(castle, player));
    }
}