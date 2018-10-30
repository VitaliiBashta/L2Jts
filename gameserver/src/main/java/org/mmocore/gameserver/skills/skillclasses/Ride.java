package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Ride extends Skill {
    public Ride(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (!activeChar.isPlayer()) {
            return false;
        }

        final Player player = (Player) activeChar;
        if (getNpcId() != 0) {
            if (player.isInOlympiadMode()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
                return false;
            }
            if (player.isInDuel() || player.isSitting() || player.isInCombat() || player.isFishing() || player.isCursedWeaponEquipped() ||
                    player.isTransformed() || player.getServitor() != null || player.isMounted() || player.isInBoat()) {
                player.sendPacket(SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                return false;
            }
        } else if (getNpcId() == 0 && !player.isMounted()) {
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature caster, final List<Creature> targets) {
        if (!caster.isPlayer()) {
            return;
        }

        final Player activeChar = (Player) caster;
        activeChar.setMount(getNpcId(), 0, 0, -1);
    }
}