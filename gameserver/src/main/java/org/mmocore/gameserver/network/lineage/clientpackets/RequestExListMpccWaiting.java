package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExListMpccWaiting;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 */
public class RequestExListMpccWaiting extends L2GameClientPacket {
    private int _listId;
    private int _locationId;
    private boolean _allLevels;

    @Override
    protected void readImpl() throws Exception {
        _listId = readD();
        _locationId = readD();
        _allLevels = readD() == 1;
    }

    @Override
    protected void runImpl() throws Exception {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new ExListMpccWaiting(player, _listId, _locationId, _allLevels));
    }
}