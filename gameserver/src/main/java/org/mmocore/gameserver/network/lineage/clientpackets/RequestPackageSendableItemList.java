package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.PackageSendableList;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 20:35/16.05.2011
 */
public class RequestPackageSendableItemList extends L2GameClientPacket {
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        player.sendPacket(new PackageSendableList(_objectId, player));
    }
}
