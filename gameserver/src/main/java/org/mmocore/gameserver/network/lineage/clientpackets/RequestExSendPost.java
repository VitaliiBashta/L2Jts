package org.mmocore.gameserver.network.lineage.clientpackets;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.mysql;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.model.mail.task.ReceiverNotifyTask;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplyWritePost;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Запрос на отсылку нового письма. В ответ шлется {@link ExReplyWritePost}.
 *
 * @see RequestExPostItemList
 * @see RequestExRequestReceivedPostList
 */
public class RequestExSendPost extends L2GameClientPacket {
    private int _messageType;
    private String _recieverName, _topic, _body;
    private int _count;
    private int[] _items;
    private long[] _itemQ;
    private long _price;

    /**
     * format: SdSS dx[dQ] Q
     */
    @Override
    protected void readImpl() {
        _recieverName = readS(35); // имя адресата
        _messageType = readD(); // тип письма, 0 простое 1 с запросом оплаты
        _topic = readS(Byte.MAX_VALUE); // topic
        _body = readS(Short.MAX_VALUE); // body

        _count = readD(); // число прикрепленных вещей
        if (_count * 12 + 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            Log.audit("[SendPost]", "Player(ID:" + getClient().getActiveChar().getObjectId() + ") name: " + getClient().getActiveChar().getName() + " item count seted on not correct!");
            _count = 0;
            return;
        }

        _items = new int[_count];
        _itemQ = new long[_count];

        for (int i = 0; i < _count; i++) {
            _items[i] = readD(); // objectId
            _itemQ[i] = readQ(); // количество
            if (_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i) {
                _count = 0;
                return;
            }
        }

        _price = readQ(); // цена для писем с запросом оплаты

        if (_price < 0) {
            _count = 0;
            _price = 0;
        }
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

        // Custom
        if (activeChar.isGM() && "ONLINE_ALL".equalsIgnoreCase(_recieverName)) {
            final TIntLongMap map = new TIntLongHashMap();
            if (_items != null && _items.length > 0) {
                for (int i = 0; i < _items.length; i++) {
                    final ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);
                    map.put(item.getItemId(), _itemQ[i]);
                }
            }

            GameObjectsStorage.getPlayers().stream().filter(Player::isOnline).forEach(p -> Functions.sendSystemMail(p, _topic, _body, map));

            activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
            activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
            return;
        }

        if (!ServerConfig.ALLOW_MAIL) {
            activeChar.sendMessage(new CustomMessage("mail.Disabled"));
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
            return;
        }

        if (activeChar.getEnchantScroll() != null) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
            return;
        }

        if (_count > 0 && !activeChar.isInPeaceZone()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (!activeChar.antiFlood.canMail()) {
            return;
        }

        if (_price > 0) {
            if (!activeChar.getPlayerAccess().UseTrade) {
                activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_);
                activeChar.sendActionFailed();
                return;
            }

            final String tradeBan = activeChar.getPlayerVariables().get(PlayerVariables.TRADE_BAN);
            if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis())) {
                if (tradeBan.equals("-1")) {
                    activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently"));
                } else {
                    activeChar.sendMessage(new CustomMessage("common.TradeBanned").addString(Util.formatTime((int) (Long.parseLong(tradeBan) / 1000L - System.currentTimeMillis() / 1000L))));
                }
                return;
            }
        }

        // ищем цель и проверяем блоклисты
        if (activeChar.isInBlockList(_recieverName)) // тем кто в блоклисте не шлем
        {
            activeChar.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_BLOCKED_C1).addString(_recieverName));
            return;
        }

        final int recieverId;
        final Player target = World.getPlayer(_recieverName);
        if (target != null) {
            recieverId = target.getObjectId();
            _recieverName = target.getName();
            if (target.isInBlockList(activeChar)) // цель заблокировала отправителя
            {
                activeChar.sendPacket(new SystemMessage(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(_recieverName));
                return;
            }
        } else {
            recieverId = CharacterDAO.getInstance().getObjectIdByName(_recieverName);
            if (recieverId > 0)
            //TODO [G1ta0] корректировать _recieverName
            {
                if (mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + recieverId + " AND target_Id=" + activeChar.getObjectId()) >
                        0) // цель заблокировала отправителя
                {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(_recieverName));
                    return;
                }
            }
        }

        if (recieverId == 0) // не нашли цель?
        {
            activeChar.sendPacket(SystemMsg.WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
            return;
        }

        if (recieverId == activeChar.getObjectId()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
            return;
        }

        final int expireTime = (_messageType == 1 ? 12 : 360) * 3600 +
                (int) (System.currentTimeMillis() / 1000L); //TODO [G1ta0] хардкод времени актуальности почты

        if (_count > 8) //клиент не дает отправить больше 8 вещей
        {
            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
            return;
        }

        final long serviceCost = 100 + _count * 1000; //TODO [G1ta0] хардкод цена за почту

        final List<ItemInstance> attachments = new ArrayList<>();

        activeChar.getInventory().writeLock();
        try {
            if (activeChar.getAdena() < serviceCost) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
                return;
            }

            // подготовить аттачи
            if (_count > 0) {
                for (int i = 0; i < _count; i++) {
                    final ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);
                    if (item == null || item.getCount() < _itemQ[i] || (item.getItemId() == ItemTemplate.ITEM_ID_ADENA && item.getCount() < _itemQ[i] + serviceCost) || !item.canBeTraded(activeChar)) {
                        activeChar.sendPacket(SystemMsg.THE_ITEM_THAT_YOURE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISNT_PROPER);
                        return;
                    }
                }
            }

            if (!activeChar.reduceAdena(serviceCost, true)) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
                return;
            }

            if (_count > 0) {
                for (int i = 0; i < _count; i++) {
                    final ItemInstance item = activeChar.getInventory().removeItemByObjectId(_items[i], _itemQ[i]);

                    Log.items(activeChar, Log.PostSend, item);

                    item.setOwnerId(activeChar.getObjectId());
                    item.setLocation(ItemLocation.MAIL);
                    if (item.getJdbcState().isSavable()) {
                        item.save();
                    } else {
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();
                    }

                    attachments.add(item);
                }
            }
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        final Mail mail = new Mail();
        mail.setSenderId(activeChar.getObjectId());
        mail.setSenderName(activeChar.getName());
        mail.setReceiverId(recieverId);
        mail.setReceiverName(_recieverName);
        mail.setTopic(_topic);
        mail.setBody(_body);
        mail.setPrice(_messageType > 0 ? _price : 0);
        mail.setUnread(true);
        mail.setType(Mail.SenderType.NORMAL);
        mail.setExpireTime(expireTime);
        mail.setCreatedTime(System.currentTimeMillis());
        attachments.forEach(mail::addAttachment);
        mail.save();
        activeChar.antiFlood.setLastMailTime(System.currentTimeMillis());

        activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
        activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
        ThreadPoolManager.getInstance().schedule(ReceiverNotifyTask.createTask(recieverId), ServerConfig.receiverDelayMail);
    }
}
