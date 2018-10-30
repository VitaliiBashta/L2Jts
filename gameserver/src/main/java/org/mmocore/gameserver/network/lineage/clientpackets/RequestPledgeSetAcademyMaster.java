package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeReceiveMemberInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowMemberListUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;

public class RequestPledgeSetAcademyMaster extends L2GameClientPacket {
    private int _mode; // 1=set, 0=unset
    private String _sponsorName;
    private String _apprenticeName;

    @Override
    protected void readImpl() {
        _mode = readD();
        _sponsorName = readS(16);
        _apprenticeName = readS(16);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if ((activeChar.getClanPrivileges() & Clan.CP_CL_APPRENTICE) == Clan.CP_CL_APPRENTICE) {
            final UnitMember sponsor = activeChar.getClan().getAnyMember(_sponsorName);
            final UnitMember apprentice = activeChar.getClan().getAnyMember(_apprenticeName);
            if (sponsor != null && apprentice != null) {
                if (apprentice.getPledgeType() != Clan.SUBUNIT_ACADEMY || sponsor.getPledgeType() == Clan.SUBUNIT_ACADEMY) {
                    return; // hack?
                }

                if (_mode == 1) {
                    if (sponsor.hasApprentice()) {
                        activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustAlly.MemberAlreadyHasApprentice"));
                        return;
                    }
                    if (apprentice.hasSponsor()) {
                        activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustAlly.ApprenticeAlreadyHasSponsor"));
                        return;
                    }
                    sponsor.setApprentice(apprentice.getObjectId());
                    clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(apprentice));
                    clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1).addString(sponsor.getName()).addString(apprentice.getName()));
                } else {
                    if (!sponsor.hasApprentice()) {
                        activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustAlly.MemberHasNoApprentice"));
                        return;
                    }
                    sponsor.setApprentice(0);
                    clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(apprentice));
                    clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.S2_CLAN_MEMBER_C1S_APPRENTICE_HAS_BEEN_REMOVED).addString(sponsor.getName()).addString(apprentice.getName()));
                }
                if (apprentice.isOnline()) {
                    apprentice.getPlayer().broadcastCharInfo();
                }
                activeChar.sendPacket(new PledgeReceiveMemberInfo(sponsor));
            }
        } else {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.clientpackets.RequestOustAlly.NoMasterRights"));
        }
    }
}