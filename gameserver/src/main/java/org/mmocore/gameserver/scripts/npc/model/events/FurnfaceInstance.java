package org.mmocore.gameserver.scripts.npc.model.events;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.entity.events.impl.MonasteryFurnaceEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * Created by [STIGMATED] : 07.08.12 : 22:52
 */
@SuppressWarnings("serial")
public class FurnfaceInstance extends NpcInstance {
    public FurnfaceInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setTargetable(false);
    }

    public void setActive2114001(int i) {
        setTargetable(false);
        if (getEvent(MonasteryFurnaceEvent.class).getId() == i) {
            setNpcState(1);
            ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
        }
    }

    public void setActive2114002() {
        setTargetable(false);
        setNpcState(1);
        ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
    }

    public void setSCE_GAME_PLAYER_START() {
        setNpcState(1);
        ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
        setTargetable(true);
    }

    public void setSCE_GAME_END() {
        setNpcState(1);
        ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
        setTargetable(false);
    }

    public void setSCE_GAME_FAILURE() {
        setTargetable(false);
        setNpcState(1);
        ThreadPoolManager.getInstance().schedule(new OFF_TIMER(), 2 * 1000);
    }

    private class OFF_TIMER extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            setNpcState(2);
        }
    }
}