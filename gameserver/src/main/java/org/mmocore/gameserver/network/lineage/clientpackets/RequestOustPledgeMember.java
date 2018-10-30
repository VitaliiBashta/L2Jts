package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListDelete;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListDeleteAll;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

public class RequestOustPledgeMember extends L2GameClientPacket {
    private String _target;

    @Override
    protected void readImpl() {
        _target = readS(ServerConfig.CNAME_MAXLEN);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();

        if (activeChar == null || !((activeChar.getClanPrivileges() & Clan.CP_CL_DISMISS) == Clan.CP_CL_DISMISS)) {
            return;
        }

        final Clan clan = activeChar.getClan();
        final UnitMember member = clan.getAnyMember(_target);
        if (member == null) {
            activeChar.sendPacket(SystemMsg.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            return;
        }

        final Player memberPlayer = member.getPlayer();

        if (member.isOnline() && member.getPlayer().isInCombat()) {
            activeChar.sendPacket(SystemMsg.A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT);
            return;
        }

        final DominionSiegeEvent siegeEvent = memberPlayer == null ? null : memberPlayer.getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null && siegeEvent.isInProgress()) {
            activeChar.sendPacket(SystemMsg.THIS_CLAN_MEMBER_CANNOT_WITHDRAW_OR_BE_EXPELLED_WHILE_PARTICIPATING_IN_A_TERRITORY_WAR);
            return;
        }

        if (member.isClanLeader()) {
            activeChar.sendPacket(SystemMsg.A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN);
            return;
        }


        final int subUnitType = member.getPledgeType();
        clan.removeClanMember(subUnitType, member.getObjectId());
        clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED).addString(_target),
                new PledgeShowMemberListDelete(_target));

        if (subUnitType != Clan.SUBUNIT_ACADEMY) {
            clan.setExpelledMember();
        }

        if (memberPlayer == null) {
            return;
        }

        if (subUnitType == Clan.SUBUNIT_ACADEMY) {
            memberPlayer.setLvlJoinedAcademy(0);
        }
        memberPlayer.setClan(null);

        if (!memberPlayer.isNoble()) {
            memberPlayer.setTitle("");
        }

        memberPlayer.setLeaveClanCurTime();

        memberPlayer.broadcastCharInfo();
        //memberPlayer.broadcastRelationChanged();
        memberPlayer.store(true);

        memberPlayer.sendPacket(SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN, PledgeShowMemberListDeleteAll.STATIC);
    }
}