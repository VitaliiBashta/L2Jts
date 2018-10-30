package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.Fishing;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.WeaponTemplate;

import java.util.List;


public class ReelingPumping extends Skill {

    public ReelingPumping(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (!((Player) activeChar).isFishing()) {
            activeChar.sendPacket(getSkillType() == SkillType.PUMPING ? SystemMsg.YOU_MAY_ONLY_USE_THE_PUMPING_SKILL_WHILE_YOU_ARE_FISHING
                    : SystemMsg.YOU_MAY_ONLY_USE_THE_REELING_SKILL_WHILE_YOU_ARE_FISHING);
            activeChar.sendActionFailed();
            return false;
        }
        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature caster, final List<Creature> targets) {
        if (caster == null || !caster.isPlayer()) {
            return;
        }

        final Player player = caster.getPlayer();
        final Fishing fishing = player.getFishing();
        if (fishing == null || !fishing.isInCombat()) {
            return;
        }

        final WeaponTemplate weaponItem = player.getActiveWeaponItem();
        final int SS = player.getChargedFishShot() ? 2 : 1;
        int pen = 0;
        final double gradebonus = 1 + weaponItem.getCrystalType().ordinal() * 0.1;
        int dmg = (int) (getPower() * gradebonus * SS);

        if (player.getSkillLevel(1315) < getLevel() - 2) // 1315 - Fish Expertise
        {
            // Penalty
            player.sendPacket(
                    SystemMsg.DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED);
            pen = 50;
            dmg -= pen;
        }

        if (SS == 2) {
            player.unChargeFishShot();
        }

        fishing.useFishingSkill(dmg, pen, getSkillType());
    }
}