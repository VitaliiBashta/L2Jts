package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DuelEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class RequestDuelStart extends L2GameClientPacket {
    private String _name;
    private int _duelType;

    @Override
    protected void readImpl() {
        _name = readS(ServerConfig.CNAME_MAXLEN);
        _duelType = readD();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }

        if (player.isProcessingRequest()) {
            player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        final Player target = World.getPlayer(_name);
        if (target == null || target == player) {
            player.sendPacket(SystemMsg.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
            return;
        }

        final DuelEvent duelEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, _duelType);
        if (duelEvent == null) {
            return;
        }

        if (!duelEvent.canDuel(player, target, true)) {
            return;
        }

        if (target.isBusy()) {
            player.sendPacket(new SystemMessage(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
            return;
        }

        duelEvent.askDuel(player, target, 0);
    }
}