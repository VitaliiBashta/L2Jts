package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.listener.game.OnDayNightChangeListener;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * @author KilRoy and JDS(PainKiller)
 */
public class FantasyIsleParadEvent extends Event {
    private ZonedDateTime startTime = ZonedDateTime.now().plusYears(1);
    private boolean isInProgress = false;

    public FantasyIsleParadEvent(final MultiValueSet<String> set) {
        super(set);
        GameTimeListener gameTimeListener = new GameTimeListener();
        GameTimeManager.getInstance().addListener(gameTimeListener);
    }

    @Override
    public void initEvent() {
        super.initEvent();
    }

    @Override
    public void startEvent() {
        if (!isInProgress) {
            isInProgress = true;
            startTime = ZonedDateTime.now();
            super.startEvent();
        }
    }

    @Override
    public void stopEvent() {
        isInProgress = false;
        startTime = ZonedDateTime.now().plusYears(1);
        super.stopEvent();
    }

    @Override
    public void reCalcNextTime(final boolean onInit) {
        clearActions();
        registerActions();
    }

    @Override
    protected Instant startTime() {
        return startTime.toInstant();
    }

    @Override
    public EventType getType() {
        return EventType.FUN_EVENT;
    }

    private class GameTimeListener implements OnDayNightChangeListener {
        @Override
        public void onDay() {
            final Event eventDay = EventHolder.getInstance().getEvent(EventType.FUN_EVENT, 10032);
            eventDay.registerActions();
            eventDay.startEvent();
        }

        @Override
        public void onNight() {
            final Event eventNight = EventHolder.getInstance().getEvent(EventType.FUN_EVENT, 10031);
            eventNight.registerActions();
            eventNight.startEvent();
        }
    }
}