package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.World;

public class RequestExOustFromMPCC extends L2GameClientPacket {
    private String _name;

    /**
     * format: chS
     */
    @Override
    protected void readImpl() {
        _name = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
            return;
        }

        final Player target = World.getPlayer(_name);

        // Чар с таким имененм не найден в мире
        if (target == null) {
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
            return;
        }

        // Сам себя нельзя
        if (activeChar == target) {
            return;
        }

        // Указанный чар не в пати, не в СС, в чужом СС
        if (!target.isInParty() || !target.getParty().isInCommandChannel() || activeChar.getParty().getCommandChannel() != target.getParty().getCommandChannel()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        // Это может делать только лидер СС
        if (activeChar.getParty().getCommandChannel().getGroupLeader() != activeChar) {
            activeChar.sendPacket(SystemMsg.C1S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL);
            return;
        }

        target.getParty().getCommandChannel().getGroupLeader().sendPacket(new SystemMessage(SystemMsg
                .C1S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL)
                .addString(target.getName()));
        target.getParty().getCommandChannel().removeParty(target.getParty());
        target.getParty().broadCast(SystemMsg.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
    }
}
