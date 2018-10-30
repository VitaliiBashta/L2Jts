package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.DyeDataHolder;
import org.jts.dataparser.data.holder.dyedata.DyeData;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.PlayerUtils;

public class RequestHennaEquip extends L2GameClientPacket {
    private int _symbolId;

    /**
     * packet type id 0x6F format: cd
     */
    @Override
    protected void readImpl() {
        _symbolId = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }
        final DyeData dye = DyeDataHolder.getInstance().getDye(_symbolId);
        if (dye == null || !PlayerUtils.canPlayerWearDye(player, dye)) {
            player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
            return;
        }
        final boolean no_level = player.getLevel() < dye.dye_level;
        if (player.getPlayerClassComponent().getClassId().getLevel().ordinal() < ClassLevel.Second.ordinal() && no_level) {
            player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
            return;
        }
        final long adena = player.getAdena();
        final long countDye = player.getInventory().getCountOf(dye.dye_item_id);
        if (countDye >= dye.need_count && adena >= dye.wear_fee) {
            if (player.consumeItem(dye.dye_item_id, dye.need_count) && player.reduceAdena(dye.wear_fee)) {
                player.sendPacket(SystemMsg.THE_SYMBOL_HAS_BEEN_ADDED);
                player.addDye(dye);
            }
        } else {
            player.sendPacket(SystemMsg.THE_SYMBOL_CANNOT_BE_DRAWN);
        }
    }
}
