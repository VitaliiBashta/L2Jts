package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterSelectionInfo;

public class CharacterRestore extends L2GameClientPacket {
    // cd
    private int _charSlot;

    @Override
    protected void readImpl() {
        _charSlot = readD();
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();
        try {
            client.markRestoredChar(_charSlot);
        } catch (Exception e) {
        }
        final CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
        sendPacket(cl);
        client.setCharSelection(cl.getCharInfo());
    }
}