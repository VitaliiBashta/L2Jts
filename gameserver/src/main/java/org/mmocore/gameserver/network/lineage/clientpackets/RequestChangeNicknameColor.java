package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Util;

public class RequestChangeNicknameColor extends L2GameClientPacket {
    private static final int[] ITEM_IDS = {13021, 13307};

    private static final int[] COLORS =
            {
                    0x9393FF,    // Pink
                    0x7C49FC,    // Rose Pink
                    0x97F8FC,    // Lemon Yellow
                    0xFA9AEE,    // Lilac
                    0xFF5D93,    // Cobalt Violet
                    0x00FCA0,    // Mint Green
                    0xA0A601,    // Peacock Green
                    0x7898AF,    // Yellow Ochre
                    0x486295,    // Chocolate
                    0x999999    // Silver
            };

    private int _colorNum, _itemObjectId;
    private String _title;

    @Override
    protected void readImpl() {
        _colorNum = readD();
        _title = readS();
        _itemObjectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_colorNum < 0 || _colorNum >= COLORS.length) {
            return;
        }

        final ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemObjectId);
        if (item == null || !ArrayUtils.contains(ITEM_IDS, item.getItemId())) {
            return;
        }

        if (!_title.isEmpty() && !Util.isMatchingRegexp(_title, ServerConfig.CLAN_TITLE_TEMPLATE)) {
            activeChar.sendMessage(new CustomMessage("INCORRECT_TITLE"));
            return;
        }

        if (activeChar.consumeItem(item.getItemId(), 1)) {
            activeChar.getAppearanceComponent().setTitleColor(COLORS[_colorNum]);
            if (!Util.checkIsAllowedTitle(_title))
                activeChar.setTitle(_title);
            activeChar.broadcastUserInfo(true);
        }
    }
}