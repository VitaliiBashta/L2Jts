package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author Mangol, Magister
 * TODO[K] - timer rework!
 * @version Freya
 */
public class adiantum_trap extends DefaultAI {
    public adiantum_trap(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        ThreadPoolManager.getInstance().schedule(new TimerId(1000), 1);
        super.onEvtSpawn();
    }

    private class TimerId extends RunnableImpl {
        final int _taskId;

        public TimerId(final int taskId) {
            _taskId = taskId;
        }

        @Override
        public void runImpl() {
            final NpcInstance actor = getActor();
            switch (_taskId) {
                case 1000:
                    actor.doCast(SkillTable.getInstance().getSkillEntry(5705, 1), actor, true);
                    ThreadPoolManager.getInstance().schedule(new TimerId(2000), 10000);
                    break;
                case 2000:
                    actor.deleteMe();
                    break;
            }
        }
    }
}
