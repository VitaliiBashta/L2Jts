package org.mmocore.gameserver.phantoms.model;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.clientCustoms.PhantomConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomOnlineHolder;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.phantoms.PhantomUtils;
import org.mmocore.gameserver.phantoms.action.AbstractPhantomAction;
import org.mmocore.gameserver.phantoms.ai.AbstractPhantomAi;
import org.mmocore.gameserver.phantoms.ai.PhantomDelayType;
import org.mmocore.gameserver.utils.Location;

import java.util.concurrent.Future;

/**
 * Created by Hack
 * Date: 21.08.2016 3:32
 */
public class Phantom extends Player {
    private final PhantomMemory memory = new PhantomMemory();
    private Future<?> spawnTask;
    private Future<?> despawnTask;
    private Future<?> aiTask;
    private Future<?> actionTask;
    private Location spawnLoc;

    public Phantom(int objectId, String accountName) {
        super(objectId, accountName);
    }

    public void schedulePhantomSpawn() {
        spawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                spawnLoc = getLoc();
                spawnMe();
                startPhantomAi();
                schedulePhantomDespawn();
            }
        }, Rnd.get(PhantomConfig.phantomSpawnDelayMinMax[0] * 60000, PhantomConfig.phantomSpawnDelayMinMax[1] * 60000));
    }

    public void schedulePhantomDespawn() {
        despawnTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                despawnPhantom();
            }
        }, Rnd.get(PhantomConfig.phantomDespawnDelayMinMax[0] * 60000, PhantomConfig.phantomDespawnDelayMinMax[1] * 60000));
    }

    public void despawnPhantom() {
        stopPhantomAi();
        if (despawnTask != null && !despawnTask.isCancelled())
            despawnTask.cancel(false);
        kick();
        PhantomOnlineHolder.getInstance().deletePhantom(this);
    }

    @Override
    public AbstractPhantomAi getAI() {
        return (AbstractPhantomAi) _ai;
    }

    public void startPhantomAi() {
        aiTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(getAI(),
                getPhantomAiDelay(PhantomDelayType.AI_INIT),
                getPhantomAiDelay(PhantomDelayType.AI_TICK));
    }

    public boolean stopPhantomAi() {
        stopActionTask();
        return aiTask.cancel(true);
    }

    public void stopActionTask() {
        if (actionTask != null && !actionTask.isCancelled())
            actionTask.cancel(true);
    }

    public void updateAi(AbstractPhantomAi ai) {
        stopPhantomAi();
        setAI(ai);
        startPhantomAi();
    }

    private long getPhantomAiDelay(PhantomDelayType delayType) {
        switch (getAI().getType()) {
            case TOWN:
                switch (delayType) {
                    case AI_INIT:
                        return PhantomUtils.getRndDelay(PhantomConfig.townAiInit);
                    case AI_TICK:
                        return PhantomUtils.getRndDelay(PhantomConfig.townAiTick);
                }
                break;
        }
        return 0;
    }

    public void doAction(AbstractPhantomAction action) {
        action.setActor(this);
        actionTask = action.schedule();
    }

    public PhantomMemory getMemory() {
        return memory;
    }

    public Location getSpawnLoc() {
        return spawnLoc;
    }
}
