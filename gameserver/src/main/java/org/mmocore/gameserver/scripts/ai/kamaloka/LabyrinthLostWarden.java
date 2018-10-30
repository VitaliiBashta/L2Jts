package org.mmocore.gameserver.scripts.ai.kamaloka;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.ReflectionBossInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author pchayka
 */
public class LabyrinthLostWarden extends Fighter {
    private static final SkillEntry pa_down = SkillTable.getInstance().getSkillEntry(5701, 7);
    private final NpcInstance actor = getActor();
    private final Reflection r = actor.getReflection();

    public LabyrinthLostWarden(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        final NpcInstance captain = findLostCaptain();
        if (!r.isDefault() && captain != null) {
            captain.altOnMagicUseTimer(captain, pa_down);
        }
        super.onEvtDead(killer);
    }

    private NpcInstance findLostCaptain() {
        for (final NpcInstance n : r.getNpcs()) {
            if (n instanceof ReflectionBossInstance) {
                return n;
            }
        }
        return null;
    }
}