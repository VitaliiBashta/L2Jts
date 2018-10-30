package org.mmocore.gameserver.object.components.player.friend;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.database.dao.impl.CharacterFriendDAO;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.L2Friend;
import org.mmocore.gameserver.network.lineage.serverpackets.L2FriendStatus;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;

import java.util.Collections;
import java.util.Map;

public class FriendComponent {
    public static final int MAX_POST_FRIEND_SIZE = 100;
    public static final int MAX_FRIEND_SIZE = 128;
    private final Player playerRef;
    private Map<Integer, Friend> friendList = Collections.emptyMap();

    public FriendComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public void restore() {
        friendList = CharacterFriendDAO.getInstance().select(getPlayer());
    }

    public void removeFriend(String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        int objectId = removeFriend0(name);
        if (objectId > 0) {
            Player friendChar = World.getPlayer(objectId);

            getPlayer().sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST).addString(name), new L2Friend(name, false, friendChar != null, objectId));

            if (friendChar != null) {
                friendChar.sendPacket(new SystemMessage(SystemMsg.S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST).addString(getPlayer().getName()), new L2Friend(getPlayer(), false));
            }
        } else {
            getPlayer().sendPacket(new SystemMessage(SystemMsg.C1_IS_NOT_ON_YOUR_FRIEND_LIST).addString(name));
        }
    }

    public void notifyFriends(boolean login) {
        for (Friend friend : friendList.values()) {
            Player friendPlayer = GameObjectsStorage.getPlayer(friend.getObjectId());
            if (friendPlayer != null) {
                Friend thisFriend = friendPlayer.getFriendComponent().getList().get(getPlayer().getObjectId());
                if (thisFriend == null) {
                    continue;
                }

                thisFriend.update(getPlayer(), login);

                if (login) {
                    friendPlayer.sendPacket(new SystemMessage(SystemMsg.YOUR_FRIEND_S1_JUST_LOGGED_IN).addString(getPlayer().getName()));
                }

                friendPlayer.sendPacket(new L2FriendStatus(getPlayer(), login));

                friend.update(friendPlayer, login);
            }
        }
    }

    public void addFriend(Player friendPlayer) {
        friendList.put(friendPlayer.getObjectId(), new Friend(friendPlayer));

        CharacterFriendDAO.getInstance().insert(getPlayer(), friendPlayer);
    }

    private int removeFriend0(String name) {
        if (name == null) {
            return 0;
        }

        Integer objectId = 0;
        for (Map.Entry<Integer, Friend> entry : friendList.entrySet()) {
            if (name.equalsIgnoreCase(entry.getValue().getName())) {
                objectId = entry.getKey();
                break;
            }
        }

        if (objectId > 0) {
            friendList.remove(objectId);
            CharacterFriendDAO.getInstance().delete(getPlayer(), objectId);
            return objectId;
        }
        return 0;
    }

    public Map<Integer, Friend> getList() {
        return friendList;
    }

    @Override
    public String toString() {
        return "FriendSystem[owner=" + getPlayer().getName() + ']';
    }
}
