package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMark;

/**
 * dd d*[ddddSdS]
 */
public class ExGetBookMarkInfo extends GameServerPacket {
    private final int maxSize;
    private final TeleportBookMark[] bookMarks;

    public ExGetBookMarkInfo(final Player player) {
        maxSize = player.getTeleportBookMarkComponent().getTpBookmarkSize();
        bookMarks = player.getTeleportBookMarkComponent().getTpBookMarks().toArray(new TeleportBookMark[player.getTeleportBookMarkComponent().getTpBookMarks().size()]);
    }

    @Override
    protected void writeData() {
        writeD(0x00);
        writeD(maxSize);
        writeD(bookMarks.length);
        for (int i = 0; i < bookMarks.length; i++) {
            final TeleportBookMark bookMark = bookMarks[i];
            writeD(i + 1);
            writeD(bookMark.x);
            writeD(bookMark.y);
            writeD(bookMark.z);
            writeS(bookMark.getName());
            writeD(bookMark.getIcon());
            writeS(bookMark.getAcronym());
        }
    }
}