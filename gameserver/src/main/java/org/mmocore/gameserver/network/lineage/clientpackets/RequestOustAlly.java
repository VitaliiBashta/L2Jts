package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;

public class RequestOustAlly extends L2GameClientPacket {
    private String _clanName;

    @Override
    protected void readImpl() {
        _clanName = readS(32);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Clan leaderClan = activeChar.getClan();
        if (leaderClan == null) {
            activeChar.sendActionFailed();
            return;
        }
        final Alliance alliance = leaderClan.getAlliance();
        if (alliance == null) {
            activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }

        final Clan clan;

        if (!activeChar.isAllyLeader()) {
            activeChar.sendPacket(SystemMsg.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
            return;
        }

        if (_clanName == null) {
            return;
        }

        clan = ClanTable.getInstance().getClanByName(_clanName);

        if (clan != null) {
            if (!alliance.isMember(clan.getClanId())) {
                activeChar.sendActionFailed();
                return;
            }

            if (alliance.getLeader() == clan) {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE);
                return;
            }

            clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.S1).addString("Your clan has been expelled from " + alliance.getAllyName() + " alliance."), new SystemMessage(SystemMsg.A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION));
            clan.setAllyId(0);
            clan.setLeavedAlly();
            alliance.broadcastAllyStatus();
            alliance.removeAllyMember(clan.getClanId());
            alliance.setExpelledMember();
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustAlly.ClanDismissed").addString(clan.getName()).addString(alliance.getAllyName()));
        }
    }
}
