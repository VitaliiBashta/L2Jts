package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.manager.CastleManorManager;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.manor.CropProcure;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * format
 * dd[dddc]
 * dd[dQQc] - Gracia Final
 */
public class ExShowProcureCropDetail extends GameServerPacket {
    private final int cropId;
    private final Map<Integer, CropProcure> castleCrops;

    public ExShowProcureCropDetail(final int cropId) {
        this.cropId = cropId;
        this.castleCrops = new TreeMap<>();

        final List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (final Castle c : castleList) {
            final CropProcure cropItem = c.getCrop(cropId, CastleManorManager.PERIOD_CURRENT);
            if (cropItem != null && cropItem.getAmount() > 0) {
                castleCrops.put(c.getId(), cropItem);
            }
        }
    }

    @Override
    public void writeData() {
        writeD(cropId); // crop id
        writeD(castleCrops.size()); // size

        for (final int manorId : castleCrops.keySet()) {
            final CropProcure crop = castleCrops.get(manorId);
            writeD(manorId); // manor name
            writeQ(crop.getAmount()); // buy residual
            writeQ(crop.getPrice()); // buy price
            writeC(crop.getReward()); // reward type
        }
    }
}