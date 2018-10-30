package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.pledge.Privilege;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.NickNameChanged;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class RequestGiveNickName extends L2GameClientPacket {
    private String target;
    private String title;

    @Override
    protected void readImpl() {
        target = readS(ServerConfig.CNAME_MAXLEN);
        title = readS();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!title.isEmpty() && (!Util.isMatchingRegexp(title, ServerConfig.CLAN_TITLE_TEMPLATE)
                || !Util.checkIsAllowedTitle(title))) {
            activeChar.sendMessage(new CustomMessage("INCORRECT_TITLE"));
            return;
        }

        // Дворяне могут устанавливать/менять себе title
        if (activeChar.isNoble() && target.equalsIgnoreCase(activeChar.getName())) {
            activeChar.setTitle(title);
            activeChar.sendPacket(SystemMsg.YOUR_TITLE_HAS_BEEN_CHANGED);
            activeChar.broadcastPacket(new NickNameChanged(activeChar));
            return;
        }

        // Can the player change/give a title?
        if (!activeChar.hasPrivilege(Privilege.CL_GIVE_TITLE)) {
            activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (activeChar.getClan().getLevel() < 3) {
            activeChar.sendPacket(SystemMsg.A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE);
            return;
        }

        if (GameObjectsStorage.getPlayer(target) == null) {
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }

        final UnitMember member = activeChar.getClan().getAnyMember(target);
        if (member != null) {
            member.setTitle(title);
            member.getPlayer().sendPacket(SystemMsg.YOUR_TITLE_HAS_BEEN_CHANGED);
            member.getPlayer().sendChanges();
        } else
            activeChar.sendPacket(SystemMsg.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
    }
}