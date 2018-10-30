package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.List;

public class NegateStats extends Skill {
    private final List<Stats> negateStats;
    private final int negateCount;
    private boolean negateOffensive = false;

    public NegateStats(final StatsSet set) {
        super(set);

        final String[] negateStats = set.getString("negateStats", "").split(" ");
        this.negateStats = new ArrayList<>(negateStats.length);
        for (final String stat : negateStats) {
            if (!stat.isEmpty()) {
                this.negateStats.add(Stats.valueOfXml(stat));
            }
        }

        negateOffensive = set.getBool("negateDebuffs", false);
        negateCount = set.getInteger("negateCount", 0);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                if (!negateOffensive && !Formulas.calcSkillSuccess(activeChar, target, skillEntry, getActivateRate())) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(getId(), getLevel()));
                    continue;
                }

                int count = 0;
                for (final Stats stat : negateStats) {
                    for (final Effect e : target.getEffectList().getAllEffects()) {
                        final SkillEntry skill = e.getSkill();
                        // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
                        if (!skill.getTemplate().isOffensive() && skill.getTemplate().getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getTemplate().getMagicLevel() - getMagicLevel())) {
                            count++;
                            continue;
                        }
                        if (skill.getTemplate().isOffensive() == negateOffensive && containsStat(e, stat) && skill.getTemplate().isCancelable()) {
                            target.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getDisplayLevel()));
                            e.exit();
                            count++;
                        }
                        if (negateCount > 0 && count >= negateCount) {
                            break;
                        }
                    }
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    private boolean containsStat(final Effect e, final Stats stat) {
        for (final FuncTemplate ft : e.getTemplate().getAttachedFuncs()) {
            if (ft._stat == stat) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOffensive() {
        return !negateOffensive;
    }

    public List<Stats> getNegateStats() {
        return negateStats;
    }
}