package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

public class Logout extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        // Dont allow leaving if player is fighting
        if (activeChar.isInCombat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT);
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isBlocked() &&
                !activeChar.isFlying()) // Разрешаем выходить из игры если используется сервис HireWyvern. Вернет в начальную точку.
        {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.Logout.OutOfControl"));
            activeChar.sendActionFailed();
            return;
        }

        // Prevent player from logging out if they are a festival participant
        // and it is in progress, otherwise notify party members that the player
        // is not longer a participant.
        if (activeChar.isFestivalParticipant()) {
            if (SevenSignsFestival.getInstance().isFestivalInitialized()) {
                activeChar.sendMessage("You cannot log out while you are a participant in a festival.");
                activeChar.sendActionFailed();
                return;
            }
        }

        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.Logout.Olympiad"));
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInObserverMode()) {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.Logout.Observer"));
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isTeleporting()) {
            // FIXME сообщение
            activeChar.sendActionFailed();
            return;
        }

        activeChar.kick();
    }
}