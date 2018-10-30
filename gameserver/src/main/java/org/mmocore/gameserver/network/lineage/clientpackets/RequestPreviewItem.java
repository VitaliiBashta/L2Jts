package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.buylist.Product;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ShopPreviewInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.ShopPreviewList;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ArmorTemplate.ArmorType;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.utils.Log;

import java.util.HashMap;
import java.util.Map;

public class RequestPreviewItem extends L2GameClientPacket {
    // format: cdddb
    //private static final Logger LOGGER = LoggerFactory.getLogger(RequestPreviewItem.class);

    @SuppressWarnings("unused")
    private int _unknow;
    private int _listId;
    private int _count;
    private int[] _items;

    @Override
    protected void readImpl() {
        _unknow = readD();
        _listId = readD();
        _count = readD();
        if (_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[PreviewItem]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " trying send not correct item count, m.b used PH!");
            _count = 0;
            return;
        }
        _items = new int[_count];
        for (int i = 0; i < _count; i++) {
            _items[i] = readD();
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0) {
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!AllSettingsConfig.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        final NpcInstance merchant = activeChar.getLastNpc();
        final boolean isValidMerchant = merchant != null && merchant.isMerchantNpc() || merchant != null && merchant.isTeleportNpc();
        if (!activeChar.isGM() && (merchant == null || !isValidMerchant || !activeChar.isInRangeZ(merchant, activeChar.getInteractDistance(merchant)))) {
            Log.audit("[PreviewItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " isInRange > 200 || null");
            activeChar.sendActionFailed();
            return;
        }

        final BuyList buyList = merchant.getTemplate().getTradeList(_listId);
        if (buyList == null) {
            Log.audit("[PreviewItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " not find buyList on NPC trade list!");
            activeChar.sendActionFailed();
            return;
        }

        //int slots = 0;
        long totalPrice = 0; // Цена на примерку каждого итема 10 Adena.

        final Map<Integer, Integer> itemList = new HashMap<>();
        try {
            for (int i = 0; i < _count; i++) {
                final int itemId = _items[i];
                final Product product = buyList.getProductByItemId(itemId);
                if (product == null) {
                    activeChar.sendActionFailed();
                    return;
                }
                final ItemTemplate template = product.getItem();
                if (template == null) {
                    continue;
                }

                if (!template.isEquipable()) {
                    continue;
                }

                final int paperdoll = Inventory.getPaperdollIndex(template.getBodyPart());
                if (paperdoll < 0) {
                    continue;
                }

                if (activeChar.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael) {
                    if (template.getItemType() == ArmorType.HEAVY || template.getItemType() == ArmorType.MAGIC ||
                            template.getItemType() == ArmorType.SIGIL || template.getItemType() == WeaponType.NONE) {
                        continue;
                    }
                } else {
                    if (template.getItemType() == WeaponType.CROSSBOW || template.getItemType() == WeaponType.RAPIER ||
                            template.getItemType() == WeaponType.ANCIENTSWORD) {
                        continue;
                    }
                }

                if (itemList.containsKey(paperdoll)) {
                    activeChar.sendPacket(SystemMsg.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                    return;
                } else {
                    itemList.put(paperdoll, itemId);
                }

                totalPrice += ShopPreviewList.getWearPrice(template);
            }

            if (!activeChar.reduceAdena(totalPrice)) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
        } catch (ArithmeticException ae) {
            Log.audit("[PreviewItem]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " exception in main method, m.b used PH!");
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        if (!itemList.isEmpty()) {
            activeChar.sendPacket(new ShopPreviewInfo(itemList));
            // Schedule task
            ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(activeChar), ServerConfig.WEAR_DELAY * 1000);
        }
    }

    private static class RemoveWearItemsTask extends RunnableImpl {
        private final Player _activeChar;

        public RemoveWearItemsTask(final Player activeChar) {
            _activeChar = activeChar;
        }

        public void runImpl() throws Exception {
            _activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_);
            _activeChar.sendUserInfo(true);
        }
    }
}
