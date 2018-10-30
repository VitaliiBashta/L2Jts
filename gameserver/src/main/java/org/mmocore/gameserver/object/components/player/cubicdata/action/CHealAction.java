package org.mmocore.gameserver.object.components.player.cubicdata.action;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.Optional;

/**
 * Create by Mangol on 22.09.2015.
 */
public class CHealAction extends AbstractCAction<CubicComponent> {
    public CHealAction(final CubicComponent component) {
        super(component);
    }

    @Override
    public void useAction(final Player player) {
        final int heal_param1 = getCubicTemplate().target_type.heal_params[0];
        final int heal_param2 = getCubicTemplate().target_type.heal_params[1];
        final int heal_param3 = getCubicTemplate().target_type.heal_params[2];
        final int heal_param4 = getCubicTemplate().target_type.heal_params[3];
        final int skill_id_1 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill1.skill_name)[0];
        final int skill_id_2 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill2.skill_name)[0];
        final int skill_id_3 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill3.skill_name)[0];
        final int skill_lvl_1 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill1.skill_name)[1];
        final int skill_lvl_2 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill2.skill_name)[1];
        final int skill_lvl_3 = getLinker().skillPchIdfindClearValue(getCubicTemplate().skill3.skill_name)[1];
        final SkillEntry skill1 = SkillTable.getInstance().getSkillEntry(skill_id_1, skill_lvl_1);
        final SkillEntry skill2 = SkillTable.getInstance().getSkillEntry(skill_id_2, skill_lvl_2);
        final SkillEntry skill3 = SkillTable.getInstance().getSkillEntry(skill_id_3, skill_lvl_3);
        final Creature creature = getTarget(player, skill1, skill2, skill3).get();
        if (isCond(creature, heal_param1, heal_param2)) {
            final int skill_chance_1 = getCubicTemplate().skill1.useChance;
            if (Rnd.chance(skill_chance_1)) {
                getCubicComponent().useSkill(skill1, creature, false);
            }
        } else if (isCond(creature, heal_param2, heal_param3)) {
            final int skill_chance_2 = getCubicTemplate().skill2.useChance;
            if (Rnd.chance(skill_chance_2)) {
                getCubicComponent().useSkill(skill2, creature, false);
            }
        } else if (isCond(creature, heal_param3, heal_param4)) {
            final int skill_chance_3 = getCubicTemplate().skill3.useChance;
            if (Rnd.chance(skill_chance_3)) {
                getCubicComponent().useSkill(skill3, creature, false);
            }
        }
    }

    @Override
    protected Optional<Creature> getTarget(final Player player, final Object... param) {
        Creature cre = player;
        final int[] heal_param = getCubicTemplate().target_type.heal_params;
        final SkillEntry skill1 = (SkillEntry) param[0];
        final SkillEntry skill2 = (SkillEntry) param[1];
        final SkillEntry skill3 = (SkillEntry) param[2];
        double percent_hp = player.getCurrentHpPercents();
        if (player.getServitor() != null && !player.getServitor().isDead() && player.getServitor().getCurrentHpPercents() < percent_hp) {
            cre = player.getServitor();
            percent_hp = player.getServitor().getCurrentHpPercents();
        }
        if (player.isInParty()) {
            for (final Playable playable : player.getParty().getPartyMembersWithPets()) {
                if (playable == null) {
                    continue;
                }
                if (!playable.isAlikeDead()) {
                    if (isCond(playable, heal_param[0], heal_param[1])) {
                        if (playable.getCurrentHpPercents() < percent_hp && player.isInRangeZ(cre, skill1.getTemplate().getCastRange())) {
                            cre = playable;
                            percent_hp = playable.getCurrentHpPercents();
                        }
                    } else if (isCond(playable, heal_param[1], heal_param[2])) {
                        if (playable.getCurrentHpPercents() < percent_hp && player.isInRangeZ(cre, skill2.getTemplate().getCastRange())) {
                            cre = playable;
                            percent_hp = playable.getCurrentHpPercents();
                        }
                    } else if (isCond(playable, heal_param[2], heal_param[3])) {
                        if (playable.getCurrentHpPercents() < percent_hp && player.isInRangeZ(cre, skill3.getTemplate().getCastRange())) {
                            cre = playable;
                            percent_hp = playable.getCurrentHpPercents();
                        }
                    }
                }
            }
        }
        return Optional.of(cre);
    }

    @Override
    protected boolean isCond(final Creature creature, final Object... params) {
        return creature.getCurrentHpPercents() <= (int) params[0] && creature.getCurrentHpPercents() > (int) params[1];
    }
}
