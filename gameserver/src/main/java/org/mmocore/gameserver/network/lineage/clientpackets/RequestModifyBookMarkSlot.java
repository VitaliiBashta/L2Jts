package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.gameserver.database.dao.impl.CharacterTeleportBookmarkDAO;
import org.mmocore.gameserver.network.lineage.serverpackets.ExGetBookMarkInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMark;

/**
 * dSdS
 */
public class RequestModifyBookMarkSlot extends L2GameClientPacket {
    private String _name, _acronym;
    private int _icon, _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
        _name = readS(32);
        _icon = readD();
        _acronym = readS(4);
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final TeleportBookMark mark = CollectionUtils.safeGet(player.getTeleportBookMarkComponent().getTpBookMarks(), _slot - 1);
        if (mark != null) {
            mark.setName(_name);
            mark.setIcon(_icon);
            mark.setAcronym(_acronym);

            CharacterTeleportBookmarkDAO.getInstance().update(player, mark);
            player.sendPacket(new ExGetBookMarkInfo(player));
        }
    }
}