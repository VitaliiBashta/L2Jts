package org.mmocore.gameserver.model.entity.events.impl.reflection;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.geometry.Circle;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.impl.reflection.listener.RZoneEnterListener;
import org.mmocore.gameserver.model.entity.events.impl.reflection.object.RSnapshotObject;
import org.mmocore.gameserver.model.entity.events.objects.SpawnSimpleObject;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.custom.TitleType;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.World;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mangol
 * @since 05.10.2016
 */
public class RBaseController extends SpawnSimpleObject {
    private final List<RSnapshotObject> players = new CopyOnWriteArrayList<>();
    private final AtomicInteger point = new AtomicInteger(0);
    private final RTeamType teamType;
    private Zone zone;
    private ReflectionEvent event;
    private boolean winner;

    public RBaseController(final Location baseLocation, final RTeamType teamType) {
        super(teamType.getNpcId(), baseLocation);
        //this.event = event;
        this.teamType = teamType;
    }
/*
	public void setBaseLocation(Location baseLocation) {
		this.baseLocation = baseLocation;
	}*/

    /*	public void createZone() {
            final Circle circle = new Circle(baseLocation, 200);
            circle.setZmax(World.MAP_MAX_Z);
            circle.setZmin(World.MAP_MIN_Z);
            StatsSet set = new StatsSet();
            set.set("name", StringUtils.EMPTY);
            set.set("type", ZoneType.dummy);
            set.set("territory", new Territory().add(circle));
            zone = new Zone(new ZoneTemplate(set));
            zone.setReflection(event.getReflection());
            zone.addListener(new RZoneEnterListener<>(event, this));
            zone.setType(ZoneType.dummy);
            zone.setActive(true);
        }*/
    @Override
    public void spawnObject(final Event event) {
        super.spawnObject(event);
        this.event = (ReflectionEvent) event;
        final Circle circle = new Circle(getLoc(), 200);
        circle.setZmax(World.MAP_MAX_Z);
        circle.setZmin(World.MAP_MIN_Z);
        StatsSet set = new StatsSet();
        set.set("name", StringUtils.EMPTY);
        set.set("type", ZoneType.dummy);
        set.set("territory", new Territory().add(circle));
        zone = new Zone(new ZoneTemplate(set));
        zone.setReflection(event.getReflection());
        zone.addListener(new RZoneEnterListener<>(this.event, this));
        zone.setType(ZoneType.dummy);
        zone.setActive(true);
        getNpc().setTargetable(false);
        getNpc().setShowName(false);
        getNpc().broadcastCharInfo();
    }

    @Override
    public void despawnObject(final Event event) {
        super.despawnObject(event);
        zone.setActive(false);
        zone = null;
    }

    public void teleportPlayer(final boolean playerFrozen, final Reflection reflection) {
        players.stream().filter(o -> o != null).forEach(o -> {
            o.getPlayer().teleToLocation(Location.findPointToStay(getLoc(), 200, 350, o.getPlayer().getGeoIndex()), reflection);
            o.getPlayer().getCustomPlayerComponent().setName(teamType.getNameTeam());
            o.getPlayer().getCustomPlayerComponent().setTitleName(TitleType.EVENT);
            o.getPlayer().getCustomPlayerComponent().setNameColor(Integer.decode("0x" + Util.RGBtoBGR(teamType.getColor())));
            o.getPlayer().getCustomPlayerComponent().setTitleColor(OtherConfig.NORMAL_NAME_COLOUR);
            o.getPlayer().leaveParty();
            o.getPlayer().dispelBuffs();
            o.getPlayer().setUndying(true);
            if (playerFrozen)
                o.getPlayer().startFrozen();
            o.getPlayer().broadcastCharInfo();
            o.getPlayer().sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", new CustomMessage("event.r.info").toString(o.getPlayer()), o.getPlayer().getLanguage()));
        });
    }

    public List<RSnapshotObject> getPlayers() {
        return players;
    }

    public void addPlayer(final Player player) {
        if (!players.stream().anyMatch(p -> p.getPlayer() != null && p.getPlayer().getObjectId() == player.getObjectId())) {
            players.add(new RSnapshotObject(player));
            player.getEventComponent().setTeam(ReflectionEvent.class, teamType);
            player.sendPacket(new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", new CustomMessage("event.r.info").toString(player), player.getLanguage()));
        }
    }

    public int addAndGet(final int point) {
        return this.point.addAndGet(point);
    }

    public void addPlayerPoint(final Player player, final int point) {
        final Optional<RSnapshotObject> op = players.stream().filter(p -> p != null && p.getPlayer().getObjectId() == player.getObjectId()).findFirst();
        if (op.isPresent()) {
            op.get().addPoint(point);
        }
    }

    public int getPoint() {
        return point.get();
    }

    public RTeamType getTeamType() {
        return teamType;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(final boolean winner) {
        this.winner = winner;
    }

    public Zone getZone() {
        return zone;
    }

    public ReflectionEvent getEvent() {
        return event;
    }

    public Location getBaseLocation() {
        return getLoc();
    }
}
