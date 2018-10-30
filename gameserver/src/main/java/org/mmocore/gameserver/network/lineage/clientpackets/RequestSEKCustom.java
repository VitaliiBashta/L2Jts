package org.mmocore.gameserver.network.lineage.clientpackets;

public class RequestSEKCustom extends L2GameClientPacket {

    /**
     * format: dd
     */
    @Override
    protected void readImpl() {
        int slotNum = readD();
        int direction = readD();
    }

    @Override
    protected void runImpl() {
        //TODO not implemented
    }
}