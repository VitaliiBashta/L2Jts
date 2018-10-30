package org.mmocore.gameserver.scripts.ai.isle_of_prayer;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

/**
 * AI моба-мага для Isle of Prayer.<br>
 * - Если атакован членом группы, состоящей более чем из 2х чаров, то спаунятся штрафные мобы Witch Warder ID: 18364, 18365, 18366 (случайным образом 2 штуки).
 *
 * @author SYS
 */
public class IsleOfPrayerFighter extends Fighter {
    private static final int PENALTY_MOBS[] = {18364, 18365, 18366};
    private static final int YELLOW_CRYSTAL = 9593;
    private static final int GREEN_CRYSTAL = 9594;
    private boolean _penaltyMobsNotSpawned = true;

    public IsleOfPrayerFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (_penaltyMobsNotSpawned && attacker.isPlayable() && attacker.getPlayer() != null) {
            final Party party = attacker.getPlayer().getParty();
            if (party != null && party.getMemberCount() > 2) {
                _penaltyMobsNotSpawned = false;
                for (int i = 0; i < 2; i++) {
                    final MonsterInstance npc = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(PENALTY_MOBS[Rnd.get(PENALTY_MOBS.length)]));
                    npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
                    npc.setReflection(actor.getReflection());
                    npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
                    npc.spawnMe(npc.getSpawnedLoc());
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
                }
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        _penaltyMobsNotSpawned = true;
        if (killer != null) {
            final Player player = killer.getPlayer();
            if (player != null) {
                final NpcInstance actor = getActor();
                switch (actor.getNpcId()) {
                    case 22259: // Muddy Coral
                        if (Rnd.chance(26)) {
                            actor.dropItem(player, YELLOW_CRYSTAL, 1);
                        }
                        break;
                    case 22263: // Sonneratia
                        if (Rnd.chance(14)) {
                            actor.dropItem(player, GREEN_CRYSTAL, 1);
                        }
                        break;
                }
            }
        }
        super.onEvtDead(killer);
    }
}