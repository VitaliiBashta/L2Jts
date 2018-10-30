package org.mmocore.gameserver.handler.usercommands.impl;

import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

/**
 * Support for /partyinfo command
 */
public class PartyInfo implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {81};

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (id != COMMAND_IDS[0]) {
            return false;
        }

        activeChar.sendPacket(SystemMsg._PARTY_INFORMATION_);

        Party playerParty = activeChar.getParty();

        if (playerParty != null) {
            int lootDistribution = playerParty.getLootDistribution();
            switch (lootDistribution) {
                case Party.ITEM_LOOTER:
                    activeChar.sendPacket(SystemMsg.LOOTING_METHOD_FINDERS_KEEPERS);
                    break;
                case Party.ITEM_ORDER:
                    activeChar.sendPacket(SystemMsg.LOOTING_METHOD_BY_TURN);
                    break;
                case Party.ITEM_ORDER_SPOIL:
                    activeChar.sendPacket(SystemMsg.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
                    break;
                case Party.ITEM_RANDOM:
                    activeChar.sendPacket(SystemMsg.LOOTING_METHOD_RANDOM);
                    break;
                case Party.ITEM_RANDOM_SPOIL:
                    activeChar.sendPacket(SystemMsg.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
                    break;
            }
        }
        activeChar.sendPacket(SystemMsg.__DASHES1__);
        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}
