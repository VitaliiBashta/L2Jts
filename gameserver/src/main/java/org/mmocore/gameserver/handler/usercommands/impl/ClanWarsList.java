package org.mmocore.gameserver.handler.usercommands.impl;


import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Support for /attacklist /underattacklist /warlist commands
 */
public class ClanWarsList implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {88, 89, 90};

    @Override
    public boolean useUserCommand(final int id, final Player activeChar) {
        if (id != COMMAND_IDS[0] && id != COMMAND_IDS[1] && id != COMMAND_IDS[2]) {
            return false;
        }

        final Clan clan = activeChar.getClan();
        if (clan == null) {
            activeChar.sendPacket(SystemMsg.NOT_JOINED_IN_ANY_CLAN);
            return false;
        }

        List<Clan> data = new ArrayList<>();
        if (id == 88) {
            // attack list
            activeChar.sendPacket(SystemMsg._ATTACK_LIST_);
            data = clan.getEnemyClans();
        } else if (id == 89) {
            // under attack list
            activeChar.sendPacket(SystemMsg._UNDER_ATTACK_LIST_);
            data = clan.getAttackerClans();
        } else
        // id = 90
        {
            // war list
            activeChar.sendPacket(SystemMsg._WAR_LIST_);
            for (final Clan c : clan.getEnemyClans()) {
                if (clan.getAttackerClans().contains(c)) {
                    data.add(c);
                }
            }
        }

        for (final Clan c : data) {
            final String clanName = c.getName();
            final Alliance alliance = c.getAlliance();
            if (alliance != null) {
                activeChar.sendPacket(new SystemMessage(SystemMsg.S1_S2_ALLIANCE).addString(clanName).addString(alliance.getAllyName()));
            } else {
                activeChar.sendPacket(new SystemMessage(SystemMsg.S1_NO_ALLIANCE_EXISTS).addString(clanName));
            }
        }

        activeChar.sendPacket(SystemMsg.__EQUALS__);
        return true;
    }

    @Override
    public int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
