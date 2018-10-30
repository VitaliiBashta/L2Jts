package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.TrapInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.NpcInfo;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.world.World;

import java.util.List;


public class DetectTrap extends Skill {
    public DetectTrap(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null && target.isTrap()) {
                final TrapInstance trap = (TrapInstance) target;
                if (trap.getLevel() <= getPower()) {
                    trap.setDetected(true);
                    for (final Player player : World.getAroundObservers(trap)) {
                        player.sendPacket(new NpcInfo(trap, player));
                    }
                }
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}