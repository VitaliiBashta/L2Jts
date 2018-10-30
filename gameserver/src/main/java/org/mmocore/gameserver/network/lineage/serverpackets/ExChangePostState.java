package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExChangePostState extends GameServerPacket {
    private final boolean receivedBoard;
    private final Mail[] mails;
    private final int changeId;

    public ExChangePostState(final boolean receivedBoard, final int type, final Mail... n) {
        this.receivedBoard = receivedBoard;
        mails = n;
        changeId = type;
    }

    @Override
    protected void writeData() {
        writeD(receivedBoard ? 1 : 0);
        writeD(mails.length);
        for (final Mail mail : mails) {
            writeD(mail.getMessageId()); // postId
            writeD(changeId); // state
        }
    }
}