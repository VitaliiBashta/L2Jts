package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import org.mmocore.gameserver.network.lineage.GameClient.GameClientState;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.CharacterSelectionInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.RestartResponse;
import org.mmocore.gameserver.object.Player;

public class RequestRestart extends L2GameClientPacket {
    /**
     * packet type id 0x57
     * format:      c
     */

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            return;
        }

        if (activeChar.isInObserverMode()) {
            activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInCombat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isOutOfControl() &&
                !activeChar.isFlying()) // Разрешаем выходить из игры если используется сервис HireWyvern. Вернет в начальную точку.
        {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestRestart.OutOfControl"));
            activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        // Prevent player from restarting if they are a festival participant
        // and it is in progress, otherwise notify party members that the player
        // is not longer a participant.
        if (activeChar.isFestivalParticipant()) {
            if (SevenSignsFestival.getInstance().isFestivalInitialized()) {
                activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestRestart.Festival"));
                activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
                return;
            }
        }

        if (getClient() != null) {
            getClient().setState(GameClientState.AUTHED);
        }
        activeChar.restart();
        // send char list
        final CharacterSelectionInfo cl = new CharacterSelectionInfo(getClient().getLogin(), getClient().getSessionKey().playOkID1);
        sendPacket(RestartResponse.OK, cl);
        getClient().setCharSelection(cl.getCharInfo());
    }
}