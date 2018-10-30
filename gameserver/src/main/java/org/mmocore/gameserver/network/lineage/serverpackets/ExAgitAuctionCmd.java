package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author KilRoy
 */
public class ExAgitAuctionCmd extends GameServerPacket {

    @Override
    protected void writeData() {
        writeD(0x00);
        int v5 = 1;
        int v4 = 1;
        int unk = 0;
        switch (unk) {
            case 1:
                writeQ(0x00);
                writeC(0x00);
                writeC(0x00);
                writeC(0x00);
                writeC(0x00);
                break;
            case 2:
                writeC(0x00);
                switch (v5) {
                    case 1:
                    case 3:
                    case 4:
                    case 6:
                        writeD(0x00);
                        writeS("");
                        writeS("");
                        writeQ(0x00);
                        writeQ(0x00);
                        writeQ(0x00);
                        writeD(0x00);
                        break;
                    case 2:
                        writeC(0x00);
                        writeQ(0x00);
                        writeD(0x00);
                        writeD(0x00);
                        break;
                    case 5:
                        writeC(0x00);
                        writeD(0x00);
                        writeD(0x00);
                        writeQ(0x00);
                        writeD(0x00);
                        writeD(0x00);
                        writeQ(0x00);
                        break;
                }
                break;
            case 3:
                writeD(0x00);
                if (v4 > 0) {
                    writeC(0x00);
                    writeD(0x00);
                }
                break;
            case 4:
                writeD(0x00);
                writeQ(0x00);
                writeQ(0x00);
                break;
            case 5:
                writeD(0x00);
                writeQ(0x00);
                writeQ(0x00);
                writeQ(0x00);
                writeD(0x00);
                writeD(0x00);
                break;
            case 7:
                writeQ(0x00);
                break;
            case 9:
                writeC(0x00);
                writeQ(0x00);
                writeQ(0x00);
                break;
            case 11:
            case 14:
                writeD(0x00);
                writeQ(0x00);
                writeQ(0x00);
                break;
            case 13:
                writeD(0x00);
                writeQ(0x00);
                writeD(0x00);
                break;
            case 16:
                writeD(0x00);
                writeQ(0x00);
                writeQ(0x00);
                writeQ(0x00);
                writeD(0x00);
                break;
            case 17:
                writeD(0x00);
                if (v4 > 0) {
                    writeC(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeQ(0x00);
                    writeD(0x00);
                    writeD(0x00);
                    writeQ(0x00);
                }
                break;
            case 18:
                writeQ(0x00);
                writeQ(0x00);
                break;
            case 20:
                writeD(0x00);
                if (v4 > 0) {
                    writeD(0x00);
                    writeD(0x00);
                    writeQ(0x00);
                    writeC(0x00);

                }
                break;
            default:
                break;
        }
    }
}