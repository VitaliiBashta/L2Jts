package org.mmocore.gameserver.network.lineage.clientpackets;

public class RequestTimeCheck extends L2GameClientPacket {

    /**
     * format: dd
     */
    @Override
    protected void readImpl() {
        int unk = readD();
        int unk2 = readD();
    }

    @Override
    protected void runImpl() {
        //TODO not implemented
    }
}