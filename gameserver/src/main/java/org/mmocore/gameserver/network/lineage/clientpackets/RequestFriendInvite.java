package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.Request;
import org.mmocore.gameserver.model.Request.L2RequestType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.FriendAddRequest;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.friend.FriendComponent;
import org.mmocore.gameserver.world.World;

public class RequestFriendInvite extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || StringUtils.isEmpty(_name)) {
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        final Player target = World.getPlayer(_name);
        if (target == null || (target.getPlayerAccess().GodMode && target.isInvisible())) {
            activeChar.sendPacket(SystemMsg.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME);
            return;
        }

        if (target == activeChar) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST);
            return;
        }

        if (target.isBlockAll() || target.isInBlockList(activeChar) || target.getMessageRefusal()) {
            activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
            return;
        }

        if (activeChar.getFriendComponent().getList().containsKey(target.getObjectId())) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST).addName(target));
            return;
        }

        if (activeChar.getFriendComponent().getList().size() >= FriendComponent.MAX_FRIEND_SIZE) {
            activeChar.sendPacket(SystemMsg.YOU_CAN_ONLY_ENTER_UP_128_NAMES_IN_YOUR_FRIENDS_LIST);
            return;
        }

        if (target.getFriendComponent().getList().size() >= FriendComponent.MAX_FRIEND_SIZE) {
            activeChar.sendPacket(SystemMsg.THE_FRIENDS_LIST_OF_THE_PERSON_YOU_ARE_TRYING_TO_ADD_IS_FULL_SO_REGISTRATION_IS_NOT_POSSIBLE);
            return;
        }

        if (target.isInOlympiadMode()) {
            activeChar.sendPacket(SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
            return;
        }

        new Request(L2RequestType.FRIEND, activeChar, target).setTimeout(10000L);

        activeChar.sendPacket(new SystemMessage(SystemMsg.YOUVE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST).addName(target));
        target.sendPacket(new SystemMessage(SystemMsg.C1_HAS_SENT_A_FRIEND_REQUEST).addName(activeChar), new FriendAddRequest(activeChar.getName()));
    }
}