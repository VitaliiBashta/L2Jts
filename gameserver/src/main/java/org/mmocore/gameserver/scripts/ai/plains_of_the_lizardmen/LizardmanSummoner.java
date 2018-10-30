package org.mmocore.gameserver.scripts.ai.plains_of_the_lizardmen;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.PositionUtils;

/**
 * Author: Bonux, KilRoy
 * При ударе монстра спавнятся 2 х Tanta Lizardman Scout и они агрятся на игрока.
 */
public class LizardmanSummoner extends Mystic {
    private final int TANTA_LIZARDMAN_SCOUT = 22768;
    private final int SPAWN_COUNT = 2;
    private boolean spawnedMobs = false;

    public LizardmanSummoner(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        spawnedMobs = false;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (!spawnedMobs && attacker.isPlayable()) {
            if (Rnd.chance(50))
                getActor().doCast(SkillTable.getInstance().getSkillEntry(6430, 1), attacker, true);
            NpcInstance actor = getActor();
            for (int i = 0; i < SPAWN_COUNT; i++) {
                try {
                    NpcInstance npc = NpcUtils.spawnSingle(TANTA_LIZARDMAN_SCOUT, actor.getLoc());
                    //int radius = ((i % 2) == 0 ? -1 : 1) * 16000;
                    //int x = (int) (actor.getX() + 80 * Math.cos(actor.headingToRadians(actor.getHeading() - 32768 + radius)));
                    //int y = (int) (actor.getY() + 80 * Math.sin(actor.headingToRadians(actor.getHeading() - 32768 + radius)));
                    npc.setHeading(PositionUtils.calculateHeadingFrom(npc, attacker));
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            spawnedMobs = true;
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}
