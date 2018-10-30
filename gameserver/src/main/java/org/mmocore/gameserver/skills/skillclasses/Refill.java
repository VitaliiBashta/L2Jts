package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class Refill extends Skill {
    public Refill(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (target == null || !target.isPlayer() || !target.isInBoat() || !target.getPlayer().getBoat().isClanAirShip()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target == null || target.isDead() || !target.isPlayer() || !target.isInBoat() || !target.getPlayer().getBoat().isClanAirShip()) {
                continue;
            }

            final ClanAirShip airship = (ClanAirShip) target.getPlayer().getBoat();
            airship.setCurrentFuel(airship.getCurrentFuel() + (int) _power);
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}