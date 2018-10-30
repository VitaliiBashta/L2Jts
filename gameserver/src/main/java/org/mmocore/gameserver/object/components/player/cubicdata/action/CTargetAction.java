package org.mmocore.gameserver.object.components.player.cubicdata.action;

import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.CubicDataOpCond;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.CubicDataSkill;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.Optional;

/**
 * Create by Mangol on 22.09.2015.
 */
public class CTargetAction extends AbstractCAction<CubicComponent> {
    public CTargetAction(final CubicComponent component) {
        super(component);
    }

    @Override
    public void useAction(final Player player) {
        if (isCond(player)) {
            final Creature target = (Creature) player.getTarget();
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
                    if (target.isDoor() && skill1.get().targetStaticObject == 0) {
                        return;
                    }
                    final int skill_id_1 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill1.skill_name)[0];
                    final int skill_lvl_1 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill1.skill_name)[1];
                    final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_1, skill_lvl_1);
                    getCubicComponent().useSkill(skill, target, false);
                    notifyNpcPower(target, player, (int) getCubicTemplate().power);
                }
            } else if (skill2.isPresent() && otherChance <= skillChance1 + skillChance2) {
                final int chance2 = skill2.get().useChance;
                if (Rnd.chance(chance2)) {
                    if (target.isDoor() && skill2.get().targetStaticObject == 0) {
                        return;
                    }
                    final int skill_id_2 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill2.skill_name)[0];
                    final int skill_lvl_2 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill2.skill_name)[1];
                    final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_2, skill_lvl_2);
                    getCubicComponent().useSkill(skill, target, false);
                    notifyNpcPower(target, player, (int) getCubicTemplate().power);
                }
            } else if (skill3.isPresent() && otherChance <= skillChance1 + skillChance2 + skillChance3) {
                final int chance3 = skill3.get().useChance;
                if (Rnd.chance(chance3)) {
                    if (target.isDoor() && skill3.get().targetStaticObject == 0) {
                        return;
                    }
                    final int skill_id_3 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill3.skill_name)[0];
                    final int skill_lvl_3 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill3.skill_name)[1];
                    final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id_3, skill_lvl_3);
                    getCubicComponent().useSkill(skill, target, false);
                    notifyNpcPower(target, player, (int) getCubicTemplate().power);
                }
            }
        }
    }

    @Override
    protected boolean isCond(final Creature creature, final Object... param) {
        if (creature.getTarget() == null || !creature.getTarget().isCreature())
            return false;
        final Creature target = (Creature) creature.getTarget();
        final Optional<Servitor> servitor = Optional.ofNullable(creature.getServitor());
        final boolean noThisTargetServitor = !servitor.isPresent() || target != servitor.get();
        if (creature.isInCombat() && target != creature && noThisTargetServitor && isCanAttackTarget(creature, target)) {
            final Optional<CubicDataOpCond> opCond = Optional.ofNullable(getCubicTemplate().op_cond);
            if (opCond.isPresent()) {
                final int hpPercent = opCond.get().cond[1];
                final int hpCurrent = opCond.get().cond[2];
                final boolean all = target.isInCombat() && target.getCurrentHpPercents() >= hpPercent && target.getCurrentHp() >= hpCurrent;
                if (all) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

}
