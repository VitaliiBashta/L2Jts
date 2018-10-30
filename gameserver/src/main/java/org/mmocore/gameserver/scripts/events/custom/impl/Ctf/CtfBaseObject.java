package org.mmocore.gameserver.scripts.events.custom.impl.Ctf;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.geometry.Circle;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.objects.SpawnSimpleObject;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.scripts.events.custom.CaptureTeamFlagEvent;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.ZoneTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

/**
 * @author VISTALL
 * @date 23:00/03.04.2012
 */
public class CtfBaseObject extends SpawnSimpleObject {
    private Zone _zone = null;
    private TeamType _teamType;
    public CtfBaseObject(final int npcId, final Location loc, final TeamType teamType) {
        super(npcId, loc);
        _teamType = teamType;
    }

    @Override
    public void spawnObject(final Event event) {
        super.spawnObject(event);

        Circle c = new Circle(getLoc(), 250);
        c.setZmax(World.MAP_MAX_Z);
        c.setZmin(World.MAP_MIN_Z);

        StatsSet set = new StatsSet();
        set.set("name", StringUtils.EMPTY);
        set.set("type", ZoneType.dummy);
        set.set("territory", new Territory().add(c));

        _zone = new Zone(new ZoneTemplate(set));
        _zone.setReflection(event.getReflection());
        _zone.addListener(new OnZoneEnterLeaveListenerImpl());
        _zone.setActive(true);
    }

    @Override
    public void despawnObject(final Event event) {
        super.despawnObject(event);

        _zone.setActive(false);
        _zone = null;
    }

    private class OnZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(final Zone zone, final Creature actor) {
            if (!actor.isPlayer() || actor.getTeam() == TeamType.NONE || _teamType != actor.getTeam())
                return;

            Player player = actor.getPlayer();

            CaptureTeamFlagEvent event = actor.getEvent(CaptureTeamFlagEvent.class);

            FlagItemAttachment flagItemAttachment = player.getActiveWeaponFlagAttachment();
            if (!(flagItemAttachment instanceof CtfFlagObject))
                return;

            event.setWinner(actor.getTeam());
        }

        @Override
        public void onZoneLeave(final Zone zone, final Creature actor) {
            //
        }
    }
}
