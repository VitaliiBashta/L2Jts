package org.mmocore.gameserver.network.lineage.clientpackets;

public class RequestExCleftEnter extends L2GameClientPacket {
    private int unk;

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        unk = readD();
    }

    @Override
    protected void runImpl() {
        System.out.println(unk);
        //TODO not implemented
    }
}