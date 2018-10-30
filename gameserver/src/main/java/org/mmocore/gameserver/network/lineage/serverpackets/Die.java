package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.base.RestartType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;

import java.util.EnumMap;
import java.util.Map;

public class Die extends GameServerPacket {
    private final int objectId;
    private final boolean fake;
    private final Map<RestartType, Boolean> types = new EnumMap<>(RestartType.class);
    private boolean sweepable;

    public Die(final Creature cha) {
        objectId = cha.getObjectId();
        fake = !cha.isDead();

        if (cha.isMonster()) {
            sweepable = ((MonsterInstance) cha).isSweepActive();
        } else if (cha.isPlayer() && !((Player) cha).isInOlympiadMode()) {
            final Player player = (Player) cha;
            put(RestartType.FIXED, player.getPlayerAccess().ResurectFixed || (!(AllSettingsConfig.ALT_DISABLE_FEATHER_ON_SIEGES_AND_EPIC && (player.isOnSiegeField() || player.isInZone(ZoneType.epic))) && (player.getInventory().getCountOf(10649) > 0 || player.getInventory().getCountOf(13300) > 0)));
            put(RestartType.AGATHION, player.isAgathionResAvailable());
            put(RestartType.TO_VILLAGE, true);

            final Clan clan = player.getClan();
            if (clan != null) {
                put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
                put(RestartType.TO_CASTLE, clan.getCastle() > 0);
                put(RestartType.TO_FORTRESS, clan.getHasFortress() > 0);
            }

            for (final Event e : cha.getEvents()) {
                e.checkRestartLocs(player, types);
            }
        }
    }

    @Override
    protected void writeData() {
        if (fake) {
            return;
        }

        writeD(objectId);
        writeD(get(RestartType.TO_VILLAGE)); // to nearest village
        writeD(get(RestartType.TO_CLANHALL)); // to hide away
        writeD(get(RestartType.TO_CASTLE)); // to castle
        writeD(get(RestartType.TO_FLAG));// to siege HQ
        writeD(sweepable ? 0x01 : 0x00); // sweepable  (blue glow)
        writeD(get(RestartType.FIXED));// FIXED
        writeD(get(RestartType.TO_FORTRESS));// fortress
        writeC(0); //show die animation
        writeD(get(RestartType.AGATHION));//agathion ress button
        writeD(0x00); //additional free space
    }

    private void put(final RestartType t, final boolean b) {
        types.put(t, b);
    }

    private boolean get(final RestartType t) {
        final Boolean b = types.get(t);
        return b != null && b;
    }
}