package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.RecipeBookItemList;
import org.mmocore.gameserver.object.Player;

public class RequestRecipeBookOpen extends L2GameClientPacket {
    private boolean isDwarvenCraft;

    @Override
    protected void readImpl() {
        if (_buf.hasRemaining()) {
            isDwarvenCraft = readD() == 0;
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        sendPacket(new RecipeBookItemList(activeChar, isDwarvenCraft));
    }
}