package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.listener.actor.player.OnPlayerSummonServitorListener;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;

/**
 * @author G1ta0
 * @author VISTALL
 */
public class EffectServitorShare extends Effect {
    private final OnPlayerSummonServitorListener _listener = new OnPlayerSummonServitorListenerImpl();

    public EffectServitorShare(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        getEffected().addListener(_listener);

        final Servitor summon = getEffected().getServitor();
        if (summon != null) {
            _listener.onSummonServitor(null, summon);
        }
    }

    @Override
    public void onExit() {
        super.onExit();

        getEffected().removeListener(_listener);

        final Servitor summon = getEffected().getServitor();
        if (summon != null) {
            summon.removeStatsByOwner(this);
            summon.updateStats();
        }
    }

    @Override
    public Func[] getStatFuncs() {
        return Func.EMPTY_FUNC_ARRAY;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    public static class FuncShare extends Func {
        public FuncShare(final Stats stat, final int order, final Object owner, final double value) {
            super(stat, order, owner, value);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double val = 0;
            // временный хардкод, до переписи статов
            switch (stat) {
                case MAX_HP:
                    val = creature.getPlayer().getMaxHp();
                    break;
                case MAX_MP:
                    val = creature.getPlayer().getMaxMp();
                    break;
                case POWER_ATTACK:
                    val = creature.getPlayer().getPAtk(null);
                    break;
                case MAGIC_ATTACK:
                    val = creature.getPlayer().getMAtk(null, null);
                    break;
                case POWER_DEFENCE:
                    val = creature.getPlayer().getPDef(null);
                    break;
                case MAGIC_DEFENCE:
                    val = creature.getPlayer().getMDef(null, null);
                    break;
                case POWER_ATTACK_SPEED:
                    val = creature.getPlayer().getPAtkSpd();
                    break;
                case MAGIC_ATTACK_SPEED:
                    val = creature.getPlayer().getMAtkSpd();
                    break;
                case CRITICAL_BASE:
                    val = creature.getPlayer().getCriticalHit(null, null);
                    break;
            }

            return initialValue + val * value;
        }
    }

    private class OnPlayerSummonServitorListenerImpl implements OnPlayerSummonServitorListener {
        @Override
        public void onSummonServitor(final Player player, final Servitor summon) {
            final FuncTemplate[] funcTemplates = getTemplate().getAttachedFuncs();
            final Func[] funcs = new Func[funcTemplates.length];
            for (int i = 0; i < funcs.length; i++) {
                funcs[i] = new FuncShare(funcTemplates[i]._stat, funcTemplates[i]._order, EffectServitorShare.this, funcTemplates[i]._value);
            }

            summon.addStatFuncs(funcs);
            summon.updateStats();
        }
    }
}