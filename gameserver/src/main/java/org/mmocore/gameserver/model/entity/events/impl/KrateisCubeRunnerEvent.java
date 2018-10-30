package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.objects.SpawnExObject;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ChatUtils;
import org.quartz.CronExpression;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author VISTALL
 * @date 21:42/10.12.2010
 */
public class KrateisCubeRunnerEvent extends Event {
    public static final String MANAGER = "manager";
    public static final String REGISTRATION = "registration";
    private static final CronExpression DATE_PATTERN = QuartzUtils.createCronExpression("0 0,30 * * * ?");
    private final List<KrateisCubeEvent> _cubes = new ArrayList<>(3);
    private boolean _isInProgress;
    private boolean _isRegistrationOver;
    private ZonedDateTime startTime = ZonedDateTime.now();

    public KrateisCubeRunnerEvent(final MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        _cubes.add(EventHolder.getInstance().<KrateisCubeEvent>getEvent(EventType.PVP_EVENT, 2));
        _cubes.add(EventHolder.getInstance().<KrateisCubeEvent>getEvent(EventType.PVP_EVENT, 3));
        _cubes.add(EventHolder.getInstance().<KrateisCubeEvent>getEvent(EventType.PVP_EVENT, 4));
    }

    @Override
    public void startEvent() {
        super.startEvent();
        _isInProgress = true;
    }

    @Override
    public void stopEvent() {
        _isInProgress = false;

        super.stopEvent();

        reCalcNextTime(false);
    }

    @Override
    public void announce(final int val) {
        final NpcInstance npc = getNpc();
        if (npc == null)
            return;
        switch (val) {
            case -600:
            case -300:
                ChatUtils.shout(npc, NpcString.THE_MATCH_WILL_BEGIN_IN_S1_MINUTES, String.valueOf(-val / 60));
                break;
            case -540:
            case -330:
            case 60:
            case 360:
            case 660:
            case 960:
                ChatUtils.shout(npc, NpcString.REGISTRATION_FOR_THE_NEXT_MATCH_WILL_END_AT_S1_MINUTES_AFTER_HOUR,
                        String.valueOf(startTime.getMinute() == 30 ? 57 : 27));
                break;
            case -480:
                ChatUtils.shout(npc, NpcString.THERE_ARE_5_MINUTES_REMAINING_TO_REGISTER_FOR_KRATEIS_CUBE_MATCH);
                break;
            case -360:
                ChatUtils.shout(npc, NpcString.THERE_ARE_3_MINUTES_REMAINING_TO_REGISTER_FOR_KRATEIS_CUBE_MATCH);
                break;
            case -240:
                ChatUtils.shout(npc, NpcString.THERE_ARE_1_MINUTES_REMAINING_TO_REGISTER_FOR_KRATEIS_CUBE_MATCH);
                break;
            case -180:
            case -120:
            case -60:
                ChatUtils.shout(npc, NpcString.THE_MATCH_WILL_BEGIN_SHORTLY);
                break;
            case 600:
                ChatUtils.shout(npc, NpcString.THE_MATCH_WILL_BEGIN_IN_S1_MINUTES, String.valueOf(20));
                break;
        }
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        clearActions();

        final Instant instant = Instant.ofEpochMilli(DATE_PATTERN.getNextValidTimeAfter(new Date()).getTime());
        startTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

        registerActions();
    }

    public NpcInstance getNpc() {
        final SpawnExObject obj = getFirstObject(MANAGER);

        return obj.getFirstSpawned();
    }

    @Override
    public boolean isInProgress() {
        return _isInProgress;
    }

    public boolean isRegistrationOver() {
        return _isRegistrationOver;
    }

    @Override
    protected Instant startTime() {
        return startTime.toInstant();
    }

    @Override
    public void printInfo() {
        //
    }

    @Override
    public void action(final String name, final boolean start) {
        if (name.equalsIgnoreCase(REGISTRATION)) {
            _isRegistrationOver = !start;
        } else {
            super.action(name, start);
        }
    }

    public List<KrateisCubeEvent> getCubes() {
        return _cubes;
    }

    public boolean isRegistered(final Player player) {
        for (final KrateisCubeEvent cubeEvent : _cubes) {
            if (cubeEvent.getRegisteredPlayer(player) != null && cubeEvent.getParticlePlayer(player) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EventType getType() {
        return EventType.MAIN_EVENT;
    }
}
