package org.mmocore.gameserver.network.lineage.clientpackets;

/**
 * format: ddS
 */
public class PetitionVote extends L2GameClientPacket {

    @Override
    protected void runImpl() {
    }

    @Override
    protected void readImpl() {
        int _type = readD();
        int _unk1 = readD();
        String _petitionText = readS(4096);
        // not done
    }
}