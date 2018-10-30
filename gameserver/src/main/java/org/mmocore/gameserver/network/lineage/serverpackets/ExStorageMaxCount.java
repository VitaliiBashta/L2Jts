package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

public class ExStorageMaxCount extends GameServerPacket {
    private final int inventory;
    private final int warehouse;
    private final int clan;
    private final int privateSell;
    private final int privateBuy;
    private final int recipeDwarven;
    private final int recipeCommon;
    private final int inventoryExtraSlots;
    private final int questItemsLimit;

    public ExStorageMaxCount(final Player player) {
        inventory = player.getInventoryLimit();
        warehouse = player.getWarehouseLimit();
        clan = OtherConfig.WAREHOUSE_SLOTS_CLAN;
        privateBuy = privateSell = player.getTradeLimit();
        recipeDwarven = player.getDwarvenRecipeLimit();
        recipeCommon = player.getCommonRecipeLimit();
        inventoryExtraSlots = player.getBeltInventoryIncrease();
        questItemsLimit = OtherConfig.INVENTORY_BASE_QUEST;
    }

    @Override
    protected final void writeData() {
        writeD(inventory);
        writeD(warehouse);
        writeD(clan);
        writeD(privateSell);
        writeD(privateBuy);
        writeD(recipeDwarven);
        writeD(recipeCommon);
        writeD(inventoryExtraSlots); // belt inventory slots increase count
        writeD(questItemsLimit); //  quests list  by off 100 maximum
    }
}