package org.mmocore.gameserver.network.authcomm.as2gs;

import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.authcomm.gs2as.PingResponse;

public class PingRequest extends ReceivablePacket {
    @Override
    public void readImpl() {

    }

    @Override
    protected void runImpl() {
        AuthServerCommunication.getInstance().sendPacket(new PingResponse());
    }
}