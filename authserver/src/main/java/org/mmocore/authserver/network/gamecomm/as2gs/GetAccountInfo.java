package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

/**
 * @author VISTALL
 * @date 20:50/25.03.2011
 */
public class GetAccountInfo extends SendablePacket {
    private String _name;

    public GetAccountInfo(String name) {
        _name = name;
    }

    @Override
    protected void writeImpl() {
        writeC(0x04);
        writeS(_name);
    }
}
