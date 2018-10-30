package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.database.dao.impl.CharacterTeleportBookmarkDAO;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetBookMarkInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMark;

public class RequestDeleteBookMarkSlot extends L2GameClientPacket {
    private int _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final TeleportBookMark bookMark = player.getTeleportBookMarkComponent().getTpBookMarks().remove(_slot - 1);
        if (bookMark != null) {
            CharacterTeleportBookmarkDAO.getInstance().delete(player, bookMark);
            player.sendPacket(new ExGetBookMarkInfo(player));
        }
    }
}