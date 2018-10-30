package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.AppearanceComponent;

/**
 * @author n0nam3
 * @date 22/08/2010 15:00
 */
public class RequestResetNickname extends L2GameClientPacket {
    @Override
    protected void readImpl() {
        // nothing (trigger)
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getAppearanceComponent().getTitleColor() != AppearanceComponent.DEFAULT_TITLE_COLOR) {
            activeChar.getAppearanceComponent().setTitleColor(AppearanceComponent.DEFAULT_TITLE_COLOR);
            activeChar.broadcastUserInfo(true);
        }
    }
}