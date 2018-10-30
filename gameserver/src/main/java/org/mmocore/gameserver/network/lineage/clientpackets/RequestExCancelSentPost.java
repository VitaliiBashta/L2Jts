package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReplySentPost;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowSentPostList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Log;

import java.util.Set;

/**
 * Запрос на удаление письма с приложениями. Возвращает приложения отправителю на личный склад и удаляет письмо. Ответ на кнопку Cancel в {@link ExReplySentPost}.
 */
public class RequestExCancelSentPost extends L2GameClientPacket {
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
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
            return;
        }

        if (activeChar.getEnchantScroll() != null) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
            return;
        }

        if (!activeChar.isInPeaceZone()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        final Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), postId);
        if (mail != null) {
            if (mail.getAttachments().isEmpty()) {
                activeChar.sendActionFailed();
                return;
            }
            if (mail.getType() != Mail.SenderType.NORMAL) {
                activeChar.sendActionFailed();
                return;
            }
            activeChar.getInventory().writeLock();
            try {
                int slots = 0;
                long weight = 0;
                for (final ItemInstance item : mail.getAttachments()) {
                    weight = Math.addExact(weight, Math.multiplyExact(item.getCount(), item.getTemplate().getWeight()));
                    if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null) {
                        slots++;
                    }
                }

                if (!activeChar.getInventory().validateWeight(weight)) {
                    activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
                    return;
                }

                if (!activeChar.getInventory().validateCapacity(slots)) {
                    activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
                    return;
                }

                final ItemInstance[] items;
                final Set<ItemInstance> attachments = mail.getAttachments();

                synchronized (attachments) {
                    items = mail.getAttachments().toArray(new ItemInstance[attachments.size()]);
                    attachments.clear();
                }

                for (final ItemInstance item : items) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getItemId()).addNumber(
                            item.getCount()));

                    Log.items(activeChar, Log.PostCancel, item);
                    activeChar.getInventory().addItem(item);
                }

                mail.delete();

                activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_CANCELLED);
            } catch (ArithmeticException ae) {
                Log.audit("[CancelSentPost]", "Player(ID:" + activeChar.getObjectId() + ") name: " + activeChar.getName() + " exception in main method, m.b used PH!");
            } finally {
                activeChar.getInventory().writeUnlock();
            }
        }
        activeChar.sendPacket(new ExShowSentPostList(activeChar));
    }
}
