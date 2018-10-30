package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExCursedWeaponList;
import org.mmocore.gameserver.object.Creature;

public class RequestCursedWeaponList extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Creature activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(new ExCursedWeaponList());
    }
}