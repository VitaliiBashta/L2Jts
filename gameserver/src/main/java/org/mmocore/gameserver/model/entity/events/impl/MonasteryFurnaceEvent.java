package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;

import java.time.Instant;

/**
 * @author PaInKiLlEr
 */
public class MonasteryFurnaceEvent extends Event {
    public static final String FURNACE_ROOM = "furnace_room";
    public static final String PROTECTOR_ROOM = "Protector_Room";
    public static final String FIGHTER_ROOM = "Fighter_Room";
    public static final String MYSTIC_ROOM = "Mystic_Room";
    public static final String DIVINITY_ROOM = "Divinity_Monster";

    public static final int[] MONSTER_ID = {22798, 22799, 22800};
    public static final int furnaceDefault = 18914;

    private Instant startTime = Instant.now();
    private boolean progress;
    private boolean furanceSpawn = false;

    public MonasteryFurnaceEvent(final MultiValueSet<String> set) {
        super(set);
        progress = true;
    }

    public void stopEvent() {
        super.stopEvent();
        callActions(onInitActions);
        reCalcNextTime(true);
        progress = true;
        furanceSpawn = false;
    }

    public void setProgress(final boolean progress) {
        this.progress = progress;
    }

    public void setFuranceSpawn(final boolean furanceSpawn) {
        this.furanceSpawn = furanceSpawn;
    }

    public long getTime() {
        return startTime.getEpochSecond();
    }

    public boolean isFuranceSpawned() {
        return furanceSpawn;
    }

    @Override
    public boolean isInProgress() {
        return progress;
    }

    @Override
    public void reCalcNextTime(final boolean onStart) {
        startTime = Instant.now();
        registerActions();
    }

    @Override
    public EventType getType() {
        return EventType.FUN_EVENT;
    }

    @Override
    protected Instant startTime() {
        return startTime;
    }
}