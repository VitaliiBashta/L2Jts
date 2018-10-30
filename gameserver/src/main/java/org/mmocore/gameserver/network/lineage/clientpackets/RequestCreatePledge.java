package org.mmocore.gameserver.network.lineage.clientpackets;

public class RequestCreatePledge extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        //Format: cS
        String _pledgename = readS(64);
    }

    @Override
    protected void runImpl() {
        //TODO not implemented
    }
}