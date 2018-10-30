package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.List;

/**
 * format(packet 0xFE)
 * ch cd [ddddcdcd]
 * c  - id
 * h  - sub id
 * <p/>
 * c
 * d  - size
 * <p/>
 * [
 * d  - level
 * d  - seed price
 * d  - seed level
 * d  - crop price
 * c
 * d  - reward 1 id
 * c
 * d  - reward 2 id
 * ]
 */
public class ExShowManorDefaultInfo extends GameServerPacket {
    private List<Integer> crops = null;

    public ExShowManorDefaultInfo() {
        crops = Manor.getInstance().getAllCrops();
    }

    @Override
    protected void writeData() {
        writeC(0);
        writeD(crops.size());
        for (final int cropId : crops) {
            writeD(cropId); // crop Id
            writeD(Manor.getInstance().getSeedLevelByCrop(cropId)); // level
            writeD(Manor.getInstance().getSeedBasicPriceByCrop(cropId)); // seed price
            writeD(Manor.getInstance().getCropBasicPrice(cropId)); // crop price
            writeC(1); // rewrad 1 Type
            writeD(Manor.getInstance().getRewardItem(cropId, 1)); // Rewrad 1 Type Item Id
            writeC(1); // rewrad 2 Type
            writeD(Manor.getInstance().getRewardItem(cropId, 2)); // Rewrad 2 Type Item Id
        }
    }
}