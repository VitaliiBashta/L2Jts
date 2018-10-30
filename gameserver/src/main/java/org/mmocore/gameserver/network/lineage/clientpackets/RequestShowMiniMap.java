package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ShowMiniMap;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ItemFunctions;

public class RequestShowMiniMap extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        // Map of Hellbound
        if ((activeChar.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP) || activeChar.isInZone("[Hellbound_territory]")) && ItemFunctions.getItemCount(activeChar, 9994) == 0) {
            activeChar.sendPacket(SystemMsg.THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP);
            return;
        }

        if (activeChar.isMiniMapOpened()) {
            activeChar.setMiniMapOpened(true, false);
        } else {
            activeChar.setMiniMapOpened(false, true);
        }

        sendPacket(new ShowMiniMap(activeChar, 0));
    }
}