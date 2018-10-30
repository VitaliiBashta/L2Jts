package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.collections.CollectionUtils;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMark;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

public class RequestTeleportBookMark extends L2GameClientPacket {
    private static final SkillEntry SKILL = SkillTable.getInstance().getSkillEntry(2588, 1);
    private int _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isCastingNow()) {
            return;
        }

        final TeleportBookMark bookMark = CollectionUtils.safeGet(activeChar.getTeleportBookMarkComponent().getTpBookMarks(), _slot - 1);
        if (bookMark == null) {
            return;
        }

        activeChar.getTeleportBookMarkComponent().setActivatedTeleportBookMark(bookMark);
        activeChar.getAI().Cast(SKILL, activeChar);
    }
}