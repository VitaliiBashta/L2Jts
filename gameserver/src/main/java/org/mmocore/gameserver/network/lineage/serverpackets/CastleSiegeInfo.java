package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.time.Instant;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = UnitID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = Size of Siege Time Select Related
 * d - next siege time
 *
 * @reworked VISTALL
 */
public class CastleSiegeInfo extends GameServerPacket {
    private final int id;
    private final int ownerObjectId;
    private long startTime;
    private int allyId;
    private boolean isLeader;
    private String ownerName = "NPC";
    private String leaderName = StringUtils.EMPTY;
    private String allyName = StringUtils.EMPTY;
    private int[] nextTimeMillis = ArrayUtils.EMPTY_INT_ARRAY;

    public CastleSiegeInfo(final Castle castle, final Player player) {
        this((Residence) castle, player);

        final CastleSiegeEvent siegeEvent = castle.getSiegeEvent();
        final long siegeTimeSeconds = castle.getSiegeDate().toEpochSecond();
        if (siegeTimeSeconds == 0) {
            nextTimeMillis = ArrayUtils.toPrimitive(siegeEvent.getNextSiegeTimes());
        } else {
            startTime = siegeTimeSeconds;
        }
    }

    public CastleSiegeInfo(final ClanHall ch, final Player player) {
        this((Residence) ch, player);

        startTime = ch.getSiegeDate().toEpochSecond();
    }

    protected CastleSiegeInfo(final Residence residence, final Player player) {
        id = residence.getId();
        ownerObjectId = residence.getOwnerId();
        final Clan owner = residence.getOwner();
        if (owner != null) {
            isLeader = owner.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) == player.getObjectId();
            ownerName = owner.getName();
            leaderName = owner.getLeaderName(Clan.SUBUNIT_MAIN_CLAN);
            final Alliance ally = owner.getAlliance();
            if (ally != null) {
                allyId = ally.getAllyId();
                allyName = ally.getAllyName();
            }
        }
    }

    @Override
    protected void writeData() {
        writeD(id);
        writeD(isLeader ? 0x01 : 0x00);
        writeD(ownerObjectId);
        writeS(ownerName); // Clan Name
        writeS(leaderName); // Clan Leader Name
        writeD(allyId); // Ally ID
        writeS(allyName); // Ally Name
        writeD((int) Instant.now().getEpochSecond());
        writeD((int) startTime);
        writeDD(nextTimeMillis, true);
    }
}