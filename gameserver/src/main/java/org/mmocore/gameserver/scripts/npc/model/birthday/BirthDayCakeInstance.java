package org.mmocore.gameserver.scripts.npc.model.birthday;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.world.World;

import java.util.concurrent.Future;

/**
 * @author VISTALL
 * @date 22:21/28.08.2011
 */
public class BirthDayCakeInstance extends NpcInstance {
    private static final SkillEntry SKILL = SkillTable.getInstance().getSkillEntry(22035, 1);
    private Future<?> _castTask;

    public BirthDayCakeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setTargetable(false);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        _castTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CastTask(), 1000L, 1000L);
    }

    @Override
    public void onDespawn() {
        super.onDespawn();

        _castTask.cancel(false);
        _castTask = null;
    }

    private class CastTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            for (Player player : World.getAroundPlayers(BirthDayCakeInstance.this, 500, 100)) {
                if (player.getEffectList().getEffectsBySkill(SKILL) != null) {
                    continue;
                }

                SKILL.getEffects(BirthDayCakeInstance.this, player, false, false);
            }
        }
    }
}
