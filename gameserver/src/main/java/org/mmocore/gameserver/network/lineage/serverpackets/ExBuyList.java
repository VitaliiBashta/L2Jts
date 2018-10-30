package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;

public class ExBuyList extends L2GameServerPacket {
    private final int listId;
    private final Optional<BuyList> buyList;
    private final long adena;

    public ExBuyList(final BuyList tradeList, final Player activeChar) {
        adena = activeChar.getAdena();
        if (tradeList != null) {
            listId = tradeList.getListId();
            buyList = Optional.of(tradeList);
            activeChar.setBuyListId(listId);
        } else {
            listId = 0;
            buyList = Optional.empty();
            activeChar.setBuyListId(0);
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0xB7);
        writeD(0x00);
        writeQ(adena); // current money
        writeD(listId);
        writeH(buyList.isPresent() ? buyList.get().getProducts().size() : 0);
        if (buyList.isPresent()) {
            buyList.get().getProducts().stream().filter(item -> item.getCount() > 0 || !item.hasLimitedStock()).forEach(item -> {
                writeItemInfo(item);
                writeQ(item.getPrice());
            });
        }
    }
}
