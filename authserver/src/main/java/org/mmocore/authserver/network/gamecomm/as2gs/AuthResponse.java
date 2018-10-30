package org.mmocore.authserver.network.gamecomm.as2gs;

import org.mmocore.authserver.network.gamecomm.SendablePacket;

public class AuthResponse extends SendablePacket {
    private int serverId;
    private String name;

    public AuthResponse(int id, String name) {
        serverId = id;
        this.name = name;
    }

    @Override
    protected void writeImpl() {
        writeC(0x00);
        writeC(serverId);
        writeS(name);
    }
}