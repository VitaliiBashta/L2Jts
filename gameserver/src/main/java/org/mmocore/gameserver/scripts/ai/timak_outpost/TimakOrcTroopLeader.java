package org.mmocore.gameserver.scripts.ai.timak_outpost;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI для Timak Orc Troop Leader ID: 20767, кричащего и призывающего братьев по клану при ударе.
 *
 * @author SYS
 */
public class TimakOrcTroopLeader extends Fighter {
    private static final int[] BROTHERS = {20768, // Timak Orc Troop Shaman
            20769, // Timak Orc Troop Warrior
            20770 // Timak Orc Troop Archer
    };

    private boolean firstTimeAttacked = true;

    public TimakOrcTroopLeader(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (!actor.isDead() && firstTimeAttacked) {
            firstTimeAttacked = false;
            Functions.npcSay(actor, NpcString.SHOW_YOURSELVES);
            for (int bro : BROTHERS) // бро бро бротюня, пизды насыпают xD
            {
                try {
                    NpcInstance npc = NpcHolder.getInstance().getTemplate(bro).getNewInstance();
                    npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
                    npc.setReflection(actor.getReflection());
                    npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
                    npc.spawnMe(npc.getSpawnedLoc());
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}