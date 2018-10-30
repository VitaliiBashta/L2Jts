package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Populates the Siege Defender List in the SiegeInfo Window<BR>
 * <BR>
 * packet type id 0xcb<BR>
 * format: cddddddd + dSSdddSSd<BR>
 * <BR>
 * c = 0xcb<BR>
 * d = unitId<BR>
 * d = unknow (0x00)<BR>
 * d = активация регистрации (0x01)<BR>
 * d = unknow (0x00)<BR>
 * d = Number of Defending Clans?<BR>
 * d = Number of Defending Clans<BR>
 * { //repeats<BR>
 * d = ClanID<BR>
 * S = ClanName<BR>
 * S = ClanLeaderName<BR>
 * d = ClanCrestID<BR>
 * d = signed time (seconds)<BR>
 * d = Type -> Owner = 0x01 || Waiting = 0x02 || Accepted = 0x03 || Refuse = 0x04<BR>
 * d = AllyID<BR>
 * S = AllyName<BR>
 * S = AllyLeaderName<BR>
 * d = AllyCrestID<BR>
 *
 * @reworked VISTALL
 */
public class CastleSiegeDefenderList extends GameServerPacket {
    public static final int OWNER = 1;
    public static final int WAITING = 2;
    public static final int ACCEPTED = 3;
    public static final int REFUSE = 4;

    private final int id;
    private final int registrationValid;
    private List<DefenderClan> defenderClans = Collections.emptyList();

    public CastleSiegeDefenderList(final Castle castle) {
        id = castle.getId();
        registrationValid = !castle.getSiegeEvent().isRegistrationOver() && castle.getOwner() != null ? 1 : 0;

        final List<SiegeClanObject> defenders = castle.getSiegeEvent().getObjects(SiegeEvent.DEFENDERS);
        final List<SiegeClanObject> defendersWaiting = castle.getSiegeEvent().getObjects(CastleSiegeEvent.DEFENDERS_WAITING);
        final List<SiegeClanObject> defendersRefused = castle.getSiegeEvent().getObjects(CastleSiegeEvent.DEFENDERS_REFUSED);
        defenderClans = new ArrayList<>(defenders.size() + defendersWaiting.size() + defendersRefused.size());
        if (castle.getOwner() != null) {
            defenderClans.add(new DefenderClan(castle.getOwner(), OWNER, 0));
        }
        for (final SiegeClanObject siegeClan : defenders) {
            defenderClans.add(new DefenderClan(siegeClan.getClan(), ACCEPTED, (int) siegeClan.getDate().getEpochSecond()));
        }
        for (final SiegeClanObject siegeClan : defendersWaiting) {
            defenderClans.add(new DefenderClan(siegeClan.getClan(), WAITING, (int) siegeClan.getDate().getEpochSecond()));
        }
        for (final SiegeClanObject siegeClan : defendersRefused) {
            defenderClans.add(new DefenderClan(siegeClan.getClan(), REFUSE, (int) siegeClan.getDate().getEpochSecond()));
        }
    }

    @Override
    protected final void writeData() {
        writeD(id);
        writeD(0x00);
        writeD(registrationValid);
        writeD(0x00);

        writeD(defenderClans.size());
        writeD(defenderClans.size());
        for (final DefenderClan defenderClan : defenderClans) {
            final Clan clan = defenderClan.clan;

            writeD(clan.getClanId());
            writeS(clan.getName());
            writeS(clan.getLeaderName());
            writeD(clan.getCrestId());
            writeD(defenderClan.time);
            writeD(defenderClan.type);
            writeD(clan.getAllyId());
            final Alliance alliance = clan.getAlliance();
            if (alliance != null) {
                writeS(alliance.getAllyName());
                writeS(alliance.getAllyLeaderName());
                writeD(alliance.getAllyCrestId());
            } else {
                writeS(StringUtils.EMPTY);
                writeS(StringUtils.EMPTY);
                writeD(0x00);
            }
        }
    }

    private static class DefenderClan {
        private final Clan clan;
        private final int type;
        private final int time;

        public DefenderClan(final Clan clan, final int type, final int time) {
            this.clan = clan;
            this.type = type;
            this.time = time;
        }
    }
}