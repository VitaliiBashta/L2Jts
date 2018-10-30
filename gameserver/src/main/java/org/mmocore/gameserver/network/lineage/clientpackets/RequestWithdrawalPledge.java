package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListDelete;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListDeleteAll;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

public class RequestWithdrawalPledge extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        //is the guy in a clan  ?
        if (activeChar.getClanId() == 0) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInCombat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_LEAVE_A_CLAN_WHILE_ENGAGED_IN_COMBAT);
            return;
        }

        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        final UnitMember member = clan.getAnyMember(activeChar.getObjectId());
        if (member == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (member.isClanLeader()) {
            activeChar.sendPacket(SystemMsg.A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN);
            return;
        }

        final DominionSiegeEvent siegeEvent = activeChar.getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null && siegeEvent.isInProgress()) {
            activeChar.sendPacket(SystemMsg.THIS_CLAN_MEMBER_CANNOT_WITHDRAW_OR_BE_EXPELLED_WHILE_PARTICIPATING_IN_A_TERRITORY_WAR);
            return;
        }

        final int subUnitType = activeChar.getPledgeType();

        clan.removeClanMember(subUnitType, activeChar.getObjectId());

        clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addString(activeChar.getName()),
                new PledgeShowMemberListDelete(activeChar.getName()));

        if (subUnitType == Clan.SUBUNIT_ACADEMY) {
            activeChar.setLvlJoinedAcademy(0);
        }

        activeChar.setClan(null);
        if (!activeChar.isNoble()) {
            activeChar.setTitle(StringUtils.EMPTY);
        }

        activeChar.setLeaveClanCurTime();
        activeChar.broadcastCharInfo();

        activeChar.sendPacket(SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN, PledgeShowMemberListDeleteAll.STATIC);
    }
}