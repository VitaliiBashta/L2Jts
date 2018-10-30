package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * При спавне саммонят случайную охрану, бафает себя при спавне, и чеке миньенов.
 * Защита прямо пропорциональна количеству охранников.
 * TODO[K] - Реализовать бафф 6145 :)
 * TODO - переписать эту хуйволу смотреть птс АИ. ai_mine_robber_summoner - количество минионов не ДОЛЖНО БЫТЬ 0
 * больше да но не 0. (с)Мангол говнарь
 */
public class GraveRobberSummoner extends Mystic {
    private static final int[] Servitors = {22683, 22684, 22685, 22686};

    private int _lastMinionCount = 1;
    private boolean locked1 = true;

    public GraveRobberSummoner(NpcInstance actor) {
        super(actor);

        actor.addStatFunc(new FuncMulMinionCount(Stats.MAGIC_DEFENCE, 0x30, actor));
        actor.addStatFunc(new FuncMulMinionCount(Stats.POWER_DEFENCE, 0x30, actor));
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();

        actor.getPrivatesList().createMinion(Servitors[Rnd.get(Servitors.length)], Rnd.get(0, 2), 0, true);
        _lastMinionCount = Math.max(actor.getPrivatesList().getAlivePrivates().size(), 1);
        //actor.doCast(SkillTable.getInstance().getSkillEntry(6145, 1), actor, true);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        super.onEvtDead(killer);

        final NpcInstance actor = getActor();

        if (actor.hasPrivates()) {
            actor.getPrivatesList().deletePrivates();
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        MonsterInstance actor = (MonsterInstance) getActor();
        if (actor.isDead()) {
            return;
        }
        _lastMinionCount = Math.max(actor.getPrivatesList().getAlivePrivates().size(), 1);
        boolean locked2 = true;
        if (locked1 && actor.getPrivatesList().hasAlivePrivates()) {
            actor.doCast(SkillTable.getInstance().getSkillEntry(4254, 9), actor, true);
            locked1 = false;
        } else if (locked2) {
            actor.doCast(SkillTable.getInstance().getSkillEntry(5699, 9), actor, true);
            locked1 = false;
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    private class FuncMulMinionCount extends Func {
        public FuncMulMinionCount(Stats stat, int order, Object owner) {
            super(stat, order, owner);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * _lastMinionCount;
        }
    }
}