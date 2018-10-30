package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ItemFunctions;

public class RequestPetGetItem extends L2GameClientPacket {
    // format: cd
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        final Servitor summon = activeChar.getServitor();
        if (summon == null || !summon.isPet() || summon.isDead() || summon.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        final ItemInstance item = (ItemInstance) activeChar.getVisibleObject(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (!ItemFunctions.checkIfCanPickup(summon, item)) {
            final SystemMessage sm;
            if (item.getItemId() == 57) {
                sm = new SystemMessage(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                sm.addNumber(item.getCount());
            } else {
                sm = new SystemMessage(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                sm.addItemName(item.getItemId());
            }
            sendPacket(sm);
            activeChar.sendActionFailed();
            return;
        }

        summon.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item, null);
    }
}