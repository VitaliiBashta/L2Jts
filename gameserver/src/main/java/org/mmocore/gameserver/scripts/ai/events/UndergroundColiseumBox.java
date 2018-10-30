package org.mmocore.gameserver.scripts.ai.events;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.concurrent.Future;

/**
 * @author VISTALL
 * @date 17:59/19.05.2012
 */
public class UndergroundColiseumBox extends DefaultAI {
    private Future<?> _despawnTask;

    public UndergroundColiseumBox(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        _despawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            protected void runImpl() {
                getActor().decayOrDelete();
            }
        }, 20000L);
    }

    @Override
    public void onEvtDeSpawn() {
        super.onEvtDeSpawn();

        cancel();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        cancel();
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }

    private void cancel() {
        if (_despawnTask != null) {
            _despawnTask.cancel(false);
            _despawnTask = null;
        }
    }
}