package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * Пример:
 * 08
 * a5 04 31 48 ObjectId
 * 00 00 00 7c unk
 * <p/>
 * format  d
 */
public class DeleteObject extends GameServerPacket {
    private final int objectId;

    public DeleteObject(final GameObject obj) {
        objectId = obj.getObjectId();
    }

    @Override
    protected final void writeData() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.getObjectId() == objectId) {
            return;
        }

        writeD(objectId);
        writeD(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
    }

    @Override
    public String getType() {
        return super.getType() + ' ' + GameObjectsStorage.findObject(objectId) + " (" + objectId + ')';
    }
}