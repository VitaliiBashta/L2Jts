package org.mmocore.gameserver.network.lineage.clientpackets;

public class RequestTeleport extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        int unk = readD();
        int _type = readD();
        int unk3;
        int unk2;
        if (_type == 2) {
            unk2 = readD();
            unk3 = readD();
        } else if (_type == 3) {
            unk2 = readD();
            unk3 = readD();
            int unk4 = readD();
        }
    }

    @Override
    protected void runImpl() {
        //TODO not implemented
    }
}