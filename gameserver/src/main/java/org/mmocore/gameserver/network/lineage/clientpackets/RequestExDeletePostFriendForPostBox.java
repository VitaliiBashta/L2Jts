package org.mmocore.gameserver.network.lineage.clientpackets;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.database.dao.impl.CharacterPostFriendDAO;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 21:06/22.03.2011
 */
public class RequestExDeletePostFriendForPostBox extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        _name = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (StringUtils.isEmpty(_name)) {
            return;
        }

        int key = 0;
        final TIntObjectMap<String> postFriends = player.getPostFriends();
        final TIntObjectIterator<String> iterator = postFriends.iterator();
        for (int i = postFriends.size(); i-- > 0; ) {
            iterator.advance();
            if (iterator.value().equalsIgnoreCase(_name)) {
                key = iterator.key();
                break;
            }
        }

        if (key == 0) {
            player.sendPacket(SystemMsg.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
            return;
        }

        player.getPostFriends().remove(key);

        CharacterPostFriendDAO.getInstance().delete(player, key);
        player.sendPacket(new SystemMessage(SystemMsg.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST).addString(_name));
    }
}
