package org.mmocore.gameserver.object.components.player.cubicdata.action;

import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.CubicDataSkill;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.TargetType;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.Optional;

/**
 * Create by Mangol on 22.09.2015.
 */
public class CBySkillAction extends AbstractCAction<CubicComponent> {
    public CBySkillAction(final CubicComponent component) {
        super(component);
    }

    @Override
    public void useAction(final Player player) {
        final int otherChance = Rnd.get(100);
        final Optional<CubicDataSkill> skill1 = Optional.ofNullable(getCubicTemplate().skill1);
        final Optional<CubicDataSkill> skill2 = Optional.ofNullable(getCubicTemplate().skill2);
        final Optional<CubicDataSkill> skill3 = Optional.ofNullable(getCubicTemplate().skill3);
        final int skillChance1 = skill1.isPresent() ? skill1.get().skillChance : 0;
        final int skillChance2 = skill2.isPresent() ? skill2.get().skillChance : 0;
        final int skillChance3 = skill3.isPresent() ? skill3.get().skillChance : 0;
        if (skill1.isPresent() && otherChance <= skillChance1) {
            final int chance1 = skill1.get().useChance;
            if (Rnd.chance(chance1)) {
                final TargetType targetType1 = skill1.get().skill_target_type;
                if (targetType1 == TargetType.target) {
                    if (isCond(player, skill1.get().skill_op_cond.cond[1], skill1.get().skill_op_cond.cond[2])) {
                        final Creature target = (Creature) player.getTarget();
                        final int skill_id_1 = getLinker().skillPchIdfindClearValue(skill1.get().skill_name)[0];
                        final int skill_lvl_1 = getLinker().skillPchIdfindClearValue(skill1.get().skill_name)[1];
                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_1, skill_lvl_1);
                        getCubicComponent().useSkill(skill, target, false);
                    }
                }
            }
        } else if (skill2.isPresent() && otherChance <= skillChance1 + skillChance2) {
            final int chance2 = skill2.get().useChance;
            if (Rnd.chance(chance2)) {
                final TargetType targetType2 = skill2.get().skill_target_type;
                if (targetType2 == TargetType.target) {
                    if (isCond(player, skill2.get().skill_op_cond.cond[1], skill2.get().skill_op_cond.cond[2])) {
                        final Creature target = (Creature) player.getTarget();
                        final int skill_id_2 = getLinker().skillPchIdfindClearValue(skill2.get().skill_name)[0];
                        final int skill_lvl_2 = getLinker().skillPchIdfindClearValue(skill2.get().skill_name)[1];
                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_2, skill_lvl_2);
                        getCubicComponent().useSkill(skill, target, false);
                    }
                } else if (targetType2 == TargetType.heal) {
                    final int hpPercent = skill2.get().skill_op_cond.cond[1];
                    final int hpCurrent = skill2.get().skill_op_cond.cond[2];
                    if (player.getCurrentHpPercents() <= hpPercent && player.getCurrentHp() <= hpCurrent) {
                        final int skill_id_2 = getLinker().skillPchIdfindClearValue(skill2.get().skill_name)[0];
                        final int skill_lvl_2 = getLinker().skillPchIdfindClearValue(skill2.get().skill_name)[1];
                        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_2, skill_lvl_2);
                        getCubicComponent().useSkill(skill, player, false);
                    }
                }
            }
        } else if (skill3.isPresent() && otherChance <= skillChance1 + skillChance2 + skillChance3) {
            final int chance3 = skill3.get().useChance;
            if (Rnd.chance(chance3)) {
                final TargetType targetType3 = skill3.get().skill_target_type;
                if (targetType3 == TargetType.master) {
                    final boolean isDebuff = skill3.get().skill_op_cond.isDebuff;
                    if (isDebuff) {
                        final int debuffCount = (int) player.getEffectList().getAllEffects().stream().filter(m -> m.getSkill().getTemplate().isOffensive()).count();
                        if (debuffCount > 0) {
                            final int skill_id_3 = getLinker().skillPchIdfindClearValue(skill3.get().skill_name)[0];
                            final int skill_lvl_3 = getLinker().skillPchIdfindClearValue(skill3.get().skill_name)[1];
                            final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_3, skill_lvl_3);
                            getCubicComponent().useSkill(skill, player, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isCond(final Creature creature, final Object... params) {
        if (creature == null || !creature.isCreature()) {
            return false;
        }
        GameObject gameObject = creature.getTarget();
        if (gameObject == null || !gameObject.isCreature()) {
            return false;
        }
        final Creature target = (Creature) gameObject;
        final Optional<Servitor> servitor = Optional.ofNullable(creature.getServitor());
        final boolean noThisTargetServitor = !servitor.isPresent() || target != servitor.get();
        if (creature.isInCombat() && target != creature && noThisTargetServitor && isCanAttackTarget(creature, target)) {
            final int hpPercent = (int) params[0];
            final int hpCurrent = (int) params[1];
            if (target.isInCombat() && target.getCurrentHpPercents() >= hpPercent && target.getCurrentHp() >= hpCurrent) {
                return true;
            }
        }
        return false;
    }
}
