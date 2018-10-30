package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplyReceivedPost;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowReceivedPostList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.World;

import java.util.Set;

/**
 * Шлется клиентом при согласии принять письмо в {@link ExReplyReceivedPost}. Если письмо с оплатой то создателю письма шлется запрошенная сумма.
 */
public class RequestExReceivePost extends L2GameClientPacket {
    private int postId;

    /**
     * format: d
     */
    @Override
    protected void readImpl() {
        postId = readD();
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

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (activeChar.getEnchantScroll() != null) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
            return;
        }

        final Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
        if (mail != null) {
            activeChar.getInventory().writeLock();
            try {
                final Set<ItemInstance> attachments = mail.getAttachments();
                final ItemInstance[] items;

                if (!attachments.isEmpty() && !activeChar.isInPeaceZone()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
                    return;
                }
                synchronized (attachments) {
                    if (mail.getAttachments().isEmpty()) {
                        return;
                    }

                    items = mail.getAttachments().toArray(new ItemInstance[attachments.size()]);

                    int slots = 0;
                    long weight = 0;
                    for (final ItemInstance item : items) {
                        weight = Math.addExact(weight, Math.multiplyExact(item.getCount(), item.getTemplate().getWeight()));
                        if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null) {
                            slots++;
                        }
                    }

                    if (!activeChar.getInventory().validateWeight(weight)) {
                        activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
                        return;
                    }

                    if (!activeChar.getInventory().validateCapacity(slots)) {
                        activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
                        return;
                    }

                    if (mail.getPrice() > 0) {
                        if (!activeChar.reduceAdena(mail.getPrice(), true)) {
                            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
                            return;
                        }

                        final Player sender = World.getPlayer(mail.getSenderId());
                        if (sender != null) {
                            sender.addAdena(mail.getPrice(), true);
                            sender.sendPacket(new SystemMessage(SystemMsg.S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL).addName(activeChar));
                        } else {
                            final int expireTime = 360 * 3600 +
                                    (int) (System.currentTimeMillis() / 1000L); //TODO [G1ta0] хардкод времени актуальности почты
                            final Mail reply = mail.reply();
                            reply.setExpireTime(expireTime);
                            reply.setCreatedTime(System.currentTimeMillis());

                            final ItemInstance item = ItemFunctions.createItem(ItemTemplate.ITEM_ID_ADENA);
                            item.setOwnerId(reply.getReceiverId());
                            item.setCount(mail.getPrice());
                            item.setLocation(ItemLocation.MAIL);
                            item.save();

                            Log.items(activeChar, Log.PostSend, item);

                            reply.addAttachment(item);
                            reply.save();
                        }
                    }

                    attachments.clear();
                }

                mail.setJdbcState(JdbcEntityState.UPDATED);
                if (StringUtils.isEmpty(mail.getBody())) {
                    mail.delete();
                } else {
                    mail.update();
                }

                for (final ItemInstance item : items) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getItemId()).addNumber(item.getCount()));
                    Log.items(activeChar, Log.PostRecieve, item);
                    activeChar.getInventory().addItem(item);
                }

                activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_RECEIVED);
            } catch (ArithmeticException ae) {
                Log.audit("[ReceivePost]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " exception in main method, m.b used PH!");
            } finally {
                activeChar.getInventory().writeUnlock();
            }
        }

        activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
    }
}
