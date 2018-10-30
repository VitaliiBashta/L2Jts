package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.Collections;
import java.util.List;

/**
 * Populates the Siege Attacker List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xca<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = ca<BR>
 * d = CastleID<BR>
 * d = unknow (0x00)<BR>
 * d = registration valid (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Attackers Clans?<BR>
 * d = Number of Attackers Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @reworked VISTALL
 */
public class CastleSiegeAttackerList extends GameServerPacket {
    private final int id;
    private final int registrationValid;
    private List<SiegeClanObject> clans = Collections.emptyList();

    public CastleSiegeAttackerList(final Residence residence) {
        id = residence.getId();
        registrationValid = !residence.getSiegeEvent().isRegistrationOver() ? 1 : 0;
        clans = residence.getSiegeEvent().getObjects(SiegeEvent.ATTACKERS);
    }

    @Override
    protected final void writeData() {
        writeD(id);

        writeD(0x00);
        writeD(registrationValid);
        writeD(0x00);

        writeD(clans.size());
        writeD(clans.size());
        for (final SiegeClanObject siegeClan : clans) {
            final Clan clan = siegeClan.getClan();

            writeD(clan.getClanId());
            writeS(clan.getName());
            writeS(clan.getLeaderName());
            writeD(clan.getCrestId());
            writeD((int) siegeClan.getDate().getEpochSecond());

            final Alliance alliance = clan.getAlliance();
            writeD(clan.getAllyId());
            if (alliance != null) {
                writeS(alliance.getAllyName());
                writeS(alliance.getAllyLeaderName());
                writeD(alliance.getAllyCrestId());
            } else {
                writeS(StringUtils.EMPTY);
                writeS(StringUtils.EMPTY);
                writeD(0);
            }
        }
    }
}