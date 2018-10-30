package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

public class FwExecutionRequest extends SendablePacket {
    private String _ip;
    private int _action;

    /**
     * @param ip
     * @param action - 1 allow | 0 deny
     */
    public FwExecutionRequest(String ip, int action) {
        _ip = ip;
        _action = action;
    }

    @Override
    protected void writeImpl() {
        writeC(0x21);
        writeD(_action);
        writeS(_ip);
    }
}