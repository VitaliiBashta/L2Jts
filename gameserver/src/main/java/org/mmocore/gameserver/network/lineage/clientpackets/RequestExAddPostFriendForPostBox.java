package org.mmocore.gameserver.network.lineage.clientpackets;

import gnu.trove.map.TIntObjectMap;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterPostFriendDAO;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExConfirmAddingPostFriend;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.friend.FriendComponent;

/**
 * @author VISTALL
 * @date 21:06/22.03.2011
 */
public class RequestExAddPostFriendForPostBox extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() throws Exception {
        _name = readS(ServerConfig.CNAME_MAXLEN);
    }

    @Override
    protected void runImpl() throws Exception {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        final int targetObjectId = CharacterDAO.getInstance().getObjectIdByName(_name);
        if (targetObjectId == 0) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.NAME_IS_NOT_EXISTS));
            return;
        }

        if (_name.equalsIgnoreCase(player.getName())) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.NAME_IS_NOT_REGISTERED));
            return;
        }

        final TIntObjectMap<String> postFriend = player.getPostFriends();
        if (postFriend.size() >= FriendComponent.MAX_POST_FRIEND_SIZE) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.LIST_IS_FULL));
            return;
        }

        if (postFriend.containsKey(targetObjectId)) {
            player.sendPacket(new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.ALREADY_ADDED));
            return;
        }

        CharacterPostFriendDAO.getInstance().insert(player, targetObjectId);
        postFriend.put(targetObjectId, CharacterDAO.getInstance().getNameByObjectId(targetObjectId));

        player.sendPacket(new SystemMessage(SystemMsg.S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST).addString(_name),
                new ExConfirmAddingPostFriend(_name, ExConfirmAddingPostFriend.SUCCESS));
    }
}
