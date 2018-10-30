package org.mmocore.gameserver.network.lineage.clientpackets;

@SuppressWarnings("unused")
public class RequestSendMsnChatLog extends L2GameClientPacket {

    @Override
    protected void runImpl() {
        //LOGGER.info.println(getType() + " :: " + unk + " :: " + unk2 + " :: " + unk3);
    }

    /**
     * format: SSd
     */
    @Override
    protected void readImpl() {
        String unk = readS();
        String unk2 = readS();
        int unk3 = readD();
    }
}