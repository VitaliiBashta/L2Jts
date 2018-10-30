package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.manor.CropProcure;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * format
 * dd[dddc[d]c[d]dddcd]
 * dd[dddc[d]c[d]dQQcQ] - Gracia Final
 */
public class ExShowSellCropList extends GameServerPacket {
    private final Map<Integer, ItemInstance> cropsItems;
    private final Map<Integer, CropProcure> castleCrops;
    private int manorId = 1;

    public ExShowSellCropList(final Player player, final int manorId, final List<CropProcure> crops) {
        this.manorId = manorId;
        castleCrops = new TreeMap<>();
        cropsItems = new TreeMap<>();

        final List<Integer> allCrops = Manor.getInstance().getAllCrops();
        for (final int cropId : allCrops) {
            final ItemInstance item = player.getInventory().getItemByItemId(cropId);
            if (item != null) {
                cropsItems.put(cropId, item);
            }
        }

        for (final CropProcure crop : crops) {
            if (cropsItems.containsKey(crop.getId()) && crop.getAmount() > 0) {
                castleCrops.put(crop.getId(), crop);
            }
        }

    }

    @Override
    public void writeData() {
        writeD(manorId); // manor id
        writeD(cropsItems.size()); // size

        for (final ItemInstance item : cropsItems.values()) {
            writeD(item.getObjectId()); // Object id
            writeD(item.getItemId()); // crop id
            writeD(Manor.getInstance().getSeedLevelByCrop(item.getItemId())); // seed level

            writeC(1);
            writeD(Manor.getInstance().getRewardItem(item.getItemId(), 1)); // reward 1 id

            writeC(1);
            writeD(Manor.getInstance().getRewardItem(item.getItemId(), 2)); // reward 2 id

            if (castleCrops.containsKey(item.getItemId())) {
                final CropProcure crop = castleCrops.get(item.getItemId());
                writeD(manorId); // manor
                writeQ(crop.getAmount()); // buy residual
                writeQ(crop.getPrice()); // buy price
                writeC(crop.getReward()); // reward
            } else {
                writeD(0xFFFFFFFF); // manor
                writeQ(0); // buy residual
                writeQ(0); // buy price
                writeC(0); // reward
            }
            writeQ(item.getCount()); // my crops
        }
    }
}