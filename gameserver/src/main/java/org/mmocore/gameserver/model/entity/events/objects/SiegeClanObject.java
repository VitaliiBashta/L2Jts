package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.TimeUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Comparator;

public class SiegeClanObject implements Serializable {
    private final Clan clan;
    private final Instant date;
    private String type;
    private NpcInstance flag;
    public SiegeClanObject(final String type, final Clan clan, final long param) {
        this(type, clan, 0, Instant.now());
    }

    public SiegeClanObject(final String type, final Clan clan, final long param, final Instant date) {
        this.type = type;
        this.clan = clan;
        this.date = date;
    }

    public int getObjectId() {
        return clan.getClanId();
    }

    public Clan getClan() {
        return clan;
    }

    public NpcInstance getFlag() {
        return flag;
    }

    public void setFlag(final NpcInstance npc) {
        flag = npc;
    }

    public void deleteFlag() {
        if (flag != null) {
            flag.deleteMe();
            flag = null;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void broadcast(final IBroadcastPacket... packet) {
        getClan().broadcastToOnlineMembers(packet);
    }

    public void broadcast(final L2GameServerPacket... packet) {
        getClan().broadcastToOnlineMembers(packet);
    }

    public void setEvent(final boolean start, final SiegeEvent event) {
        if (start) {
            for (final Player player : clan.getOnlineMembers(0)) {
                player.addEvent(event);
                player.broadcastCharInfo();
            }
        } else {
            for (final Player player : clan.getOnlineMembers(0)) {
                player.removeEvent(event);
                player.getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                player.broadcastCharInfo();
            }
        }
    }

    public boolean isParticle(final Player player) {
        return true;
    }

    public long getParam() {
        return 0;
    }

    public Instant getDate() {
        return date;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getClan().getName() + ", reg: " + TimeUtils.dateTimeFormat(getDate()) + ", param: " + getParam() + ", type: " + type + ']';
    }

    public static class SiegeClanComparator implements Comparator<SiegeClanObject> {
        private static final SiegeClanComparator INSTANCE = new SiegeClanComparator();

        public static SiegeClanComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int compare(final SiegeClanObject o1, final SiegeClanObject o2) {
            return Long.compare(o2.getParam(), o1.getParam());
        }
    }
}
