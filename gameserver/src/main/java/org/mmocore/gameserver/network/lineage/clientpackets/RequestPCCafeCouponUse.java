package org.mmocore.gameserver.network.lineage.clientpackets;

/**
 * format: chS
 */
public class RequestPCCafeCouponUse extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        // format: (ch)S
        String _unknown = readS();
    }

    @Override
    protected void runImpl() {
        //TODO not implemented
    }
}