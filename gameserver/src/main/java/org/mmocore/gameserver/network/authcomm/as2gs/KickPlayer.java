package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ServerClose;
import org.mmocore.gameserver.object.Player;

public class KickPlayer extends ReceivablePacket {
    String account;

    @Override
    public void readImpl() {
        account = readS();
    }

    @Override
    protected void runImpl() {
        GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
        if (client == null) {
            client = AuthServerCommunication.getInstance().removeAuthedClient(account);
        }
        if (client == null) {
            return;
        }

        final Player activeChar = client.getActiveChar();
        if (activeChar != null) {
            //FIXME [G1ta0] сообщение чаще всего не показывается, т.к. при закрытии соединения очередь на отправку очищается
            activeChar.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
            activeChar.kick();
        } else {
            client.close(ServerClose.STATIC);
        }
    }
}