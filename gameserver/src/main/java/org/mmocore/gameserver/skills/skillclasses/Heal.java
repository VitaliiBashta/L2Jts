package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.SkillMastery;
import org.mmocore.gameserver.model.instances.residences.SiegeFlagInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Heal extends Skill {
    private final boolean _ignoreHpEff;
    private final boolean _staticPower;

    public Heal(final StatsSet set) {
        super(set);
        _ignoreHpEff = set.getBool("ignoreHpEff", false);
        _staticPower = set.getBool("staticPower", isHandler());
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (target == null || target.isDoor() || target instanceof SiegeFlagInstance) {
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        double hp = getPower();

        if (!_staticPower) {
/*			final int mAtk = activeChar.getMAtk(null, skillEntry);
			int mAtkMod = 1;
			int staticBonus = 0;

			if(isSSPossible())
			{
				switch(activeChar.getChargedSpiritShot())
				{
					case ItemInstance.CHARGED_BLESSED_SPIRITSHOT:
						mAtkMod = 4;
						//staticBonus = getStaticBonus(mAtk);
						break;
					case ItemInstance.CHARGED_SPIRITSHOT:
						mAtkMod = 2;
						//staticBonus = getStaticBonus(mAtk) / 2;
						break;
				}
			}*/
            hp += (int) Math.sqrt(activeChar.getMAtk(null, skillEntry));
            int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
            if (sps == 2)
                hp *= getHpConsume() == 0 ? 1.44 : 1.265822784810127;
            else if (sps == 1)
                hp *= getHpConsume() == 0 ? 1.26 : 1.050632911392405;

            if (activeChar.getSkillMastery(skillEntry.getTemplate()) == SkillMastery.INC_POWER) {
                activeChar.removeSkillMastery(skillEntry);
                hp *= 3;
            }


/*			if(Formulas.calcMCrit(4.5)) // guess
			{
				hp *= 3.; // TODO: DS: apply on all targets ?
			}*/
        }

        for (final Creature target : targets) {
            if (target != null) {
                if (target.isHealBlocked()) {
                    continue;
                }

                // Player holding a cursed weapon can't be healed and can't heal
                if (!target.equals(activeChar)) {
                    if (target.isPlayer() && target.isCursedWeaponEquipped()) {
                        continue;
                    } else if (activeChar.isPlayer() && activeChar.isCursedWeaponEquipped()) {
                        continue;
                    }
                }

                double addToHp;
                if (_staticPower) {
                    addToHp = _power;
                } else {
                    addToHp = hp;
                    if (!isHandler()) {
                        addToHp *= (!_ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100., activeChar, skillEntry) : 100.) / 100.; // percent second
                        addToHp += activeChar.calcStat(Stats.HEAL_POWER, activeChar, skillEntry); // static first
                    }
                }

                addToHp = Math.max(0, Math.min(addToHp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));

                if (addToHp > 0) {
                    target.setCurrentHp(addToHp + target.getCurrentHp(), false);
                }
                if (target.isPlayer()) {
                    if (getId() == 4051) {
                        target.sendPacket(SystemMsg.REJUVENATING_HP);
                    } else if (activeChar.equals(target)) {
                        activeChar.sendPacket(new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber(Math.round(addToHp)));
                    } else {
                        target.sendPacket(new SystemMessage(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(activeChar).addNumber(Math.round(addToHp)));
                    }
                }
                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible() && isMagic()) {
            activeChar.unChargeShots(isMagic());
        }
    }

/*	private int getStaticBonus(int mAtk)
	{
		final double power = getPower();
		final double bottom = getPower() / 4.;
		if(mAtk < bottom)
		{
			return 0;
		}

		final double top = getPower() / 3.1;
		if(mAtk > getPower())
		{
			return (int) top;
		}

		mAtk -= bottom;
		return (int) (top * (mAtk / (power - bottom)));
	}*/
}