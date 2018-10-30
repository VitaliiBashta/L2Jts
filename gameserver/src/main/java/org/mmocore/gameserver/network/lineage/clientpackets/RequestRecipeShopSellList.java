package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.RecipeShopSellList;
import org.mmocore.gameserver.object.Player;

/**
 * Возврат к списку из информации о рецепте
 */
public class RequestRecipeShopSellList extends L2GameClientPacket {
    int _manufacturerId;

    @Override
    protected void readImpl() {
        _manufacturerId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        final Player manufacturer = (Player) activeChar.getVisibleObject(_manufacturerId);
        if (manufacturer == null || manufacturer.getPrivateStoreType() != Player.STORE_PRIVATE_MANUFACTURE || !manufacturer.isInRangeZ(activeChar, manufacturer.getInteractDistance(activeChar))) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new RecipeShopSellList(activeChar, manufacturer));
    }
}