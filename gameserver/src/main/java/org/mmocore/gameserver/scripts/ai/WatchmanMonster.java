package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.lang.reference.HardReferences;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI для ищущих помощи при HP < 50%
 *
 * @author Diamond
 */
public class WatchmanMonster extends Fighter {
    static final String[] flood = {"I'll be back", "You are stronger than expected"};
    static final String[] flood2 = {"Help me!", "Alarm! We are under attack!"};
    private long _lastSearch = 0;
    private boolean isSearching = false;
    private HardReference<? extends Creature> _attackerRef = HardReferences.emptyRef();

    public WatchmanMonster(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        final NpcInstance actor = getActor();
        if (attacker != null && !actor.getFaction().isNone() && actor.getCurrentHpPercents() < 50 && _lastSearch < System.currentTimeMillis() - 15000) {
            _lastSearch = System.currentTimeMillis();
            _attackerRef = attacker.getRef();

            if (findHelp()) {
                return;
            }
            Functions.npcSay(actor, "Anyone, help me!");
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    private boolean findHelp() {
        isSearching = false;
        final NpcInstance actor = getActor();
        Creature attacker = _attackerRef.get();
        if (attacker == null) {
            return false;
        }

        for (final NpcInstance npc : actor.getAroundNpc(1000, 150)) {
            if (!actor.isDead() && npc.isInFaction(actor) && !npc.isInCombat()) {
                clearTasks();
                isSearching = true;
                addTaskMove(npc.getLoc(), true);
                Functions.npcSay(actor, flood[Rnd.get(flood.length)]);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        _lastSearch = 0;
        _attackerRef = HardReferences.emptyRef();
        isSearching = false;
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtArrived() {
        NpcInstance actor = getActor();
        if (isSearching) {
            Creature attacker = _attackerRef.get();
            if (attacker != null) {
                Functions.npcSay(actor, flood2[Rnd.get(flood2.length)]);
                notifyFriends(attacker, 100);
            }
            isSearching = false;
            notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
        } else {
            super.onEvtArrived();
        }
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
        if (!isSearching) {
            super.onEvtAggression(target, aggro);
        }
    }
}