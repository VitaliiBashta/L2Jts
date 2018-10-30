package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.RecipeShopItemInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ManufactureItem;

public class RequestRecipeShopMakeInfo extends L2GameClientPacket {
    private int _manufacturerId;
    private int _recipeId;

    @Override
    protected void readImpl() {
        _manufacturerId = readD();
        _recipeId = readD();
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

        long price = -1;
        for (final ManufactureItem i : manufacturer.getCreateList()) {
            if (i.getRecipeId() == _recipeId) {
                price = i.getCost();
                break;
            }
        }

        if (price == -1) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.sendPacket(new RecipeShopItemInfo(activeChar, manufacturer, _recipeId, price, 0xFFFFFFFF));
    }
}