package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2FriendSay;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.World;

/**
 * Recieve Private (Friend) Message
 * Format: c SS
 * S: Message
 * S: Receiving Player
 */
public class RequestSendL2FriendSay extends L2GameClientPacket {
    private String _message;
    private String _reciever;

    @Override
    protected void readImpl() {
        _message = readS(2048);
        _reciever = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getNoChannel() != 0) {
            if (activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0) {
                activeChar.sendPacket(SystemMsg.CHATTING_IS_CURRENTLY_PROHIBITED_);
                return;
            }
            activeChar.updateNoChannel(0);
        }

        final Player targetPlayer = World.getPlayer(_reciever);
        if (targetPlayer == null) {
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }

        if (targetPlayer.isBlockAll()) {
            activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
            return;
        }

        if (!activeChar.getFriendComponent().getList().containsKey(targetPlayer.getObjectId())) {
            return;
        }

        Log.chat("FRIENDTELL", activeChar.getName(), _reciever, _message);

        final L2FriendSay frm = new L2FriendSay(activeChar.getName(), _reciever, _message);
        targetPlayer.sendPacket(frm);
    }
}