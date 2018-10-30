package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;

/**
 * @author VISTALL
 * @date 18:26/03.03.2011
 */
public class ClanHallNpcSiegeEvent extends SiegeEvent<ClanHall, SiegeClanObject> {
    public ClanHallNpcSiegeEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void startEvent() {
        _oldOwner = getResidence().getOwner();

        broadcastInZone(new SystemMessage(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()));

        super.startEvent();
    }

    @Override
    public void stopEvent(final boolean step) {
        final Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            if (_oldOwner != newOwner) {
                newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                newOwner.incReputation(1700, false, toString());

                if (_oldOwner != null) {
                    _oldOwner.incReputation(-1700, false, toString());
                }
            }

            broadcastInZone(new SystemMessage(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()));
            broadcastInZone(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));
        } else {
            broadcastInZone(new SystemMessage(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));
        }

        super.stopEvent(step);

        _oldOwner = null;
    }

    @Override
    public void processStep(final Clan clan) {
        if (clan != null) {
            getResidence().changeOwner(clan);
        }

        stopEvent(true);
    }

    @Override
    public void loadSiegeClans() {
        //
    }
}
