package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExShowDominionRegistry extends GameServerPacket {
    private final int dominionId;
    private final String ownerClanName;
    private final String ownerLeaderName;
    private final String ownerAllyName;
    private final int clanReq;
    private final int mercReq;
    private final int warTime;
    private final int currentTime;
    private final boolean registeredAsPlayer;
    private final boolean registeredAsClan;
    private List<TerritoryFlagsInfo> flags = Collections.emptyList();

    public ExShowDominionRegistry(final Player activeChar, final Dominion dominion) {
        dominionId = dominion.getId();

        final Clan owner = dominion.getOwner();
        final Alliance alliance = owner.getAlliance();

        final DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
        ownerClanName = owner.getName();
        ownerLeaderName = owner.getLeaderName();
        ownerAllyName = alliance == null ? StringUtils.EMPTY : alliance.getAllyName();
        warTime = (int) dominion.getSiegeDate().toEpochSecond();
        currentTime = (int) (System.currentTimeMillis() / 1000L);
        mercReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
        clanReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
        registeredAsPlayer = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(activeChar.getObjectId());
        registeredAsClan = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, activeChar.getClan()) != null;

        final List<Dominion> dominions = ResidenceHolder.getInstance().getResidenceList(Dominion.class);
        flags = new ArrayList<>(dominions.size());
        for (final Dominion d : dominions) {
            flags.add(new TerritoryFlagsInfo(d.getId(), d.getFlags()));
        }
    }

    @Override
    protected void writeData() {
        writeD(dominionId);
        writeS(ownerClanName);
        writeS(ownerLeaderName);
        writeS(ownerAllyName);
        writeD(clanReq); // Clan Request
        writeD(mercReq); // Merc Request
        writeD(warTime); // War Time
        writeD(currentTime); // Current Time
        writeD(registeredAsClan); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
        writeD(registeredAsPlayer); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
        writeD(0x01);
        writeD(flags.size()); // Territory Count
        for (final TerritoryFlagsInfo cf : flags) {
            writeD(cf.id); // Territory Id
            writeD(cf.flags.length); // Emblem Count
            for (final int flag : cf.flags) {
                writeD(flag); // Emblem ID - should be in for loop for emblem count
            }
        }
    }

    private static class TerritoryFlagsInfo {
        public final int id;
        public final Integer[] flags;

        public TerritoryFlagsInfo(final int id_, final Integer[] flags_) {
            id = id_;
            flags = flags_;
        }
    }
}