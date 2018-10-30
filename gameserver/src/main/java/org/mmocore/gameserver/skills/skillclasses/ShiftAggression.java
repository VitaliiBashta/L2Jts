package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.npc.AggroList.AggroInfo;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.world.World;

import java.util.List;


public class ShiftAggression extends Skill {
    public ShiftAggression(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (activeChar.getPlayer() == null) {
            return;
        }

        for (final Creature target : targets) {
            if (target != null) {
                if (!target.isPlayer()) {
                    continue;
                }

                final Player player = (Player) target;

                for (final NpcInstance npc : World.getAroundNpc(activeChar, getSkillRadius(), getSkillRadius())) {
                    final AggroInfo ai = npc.getAggroList().get(activeChar);
                    if (ai == null) {
                        continue;
                    }
                    npc.getAggroList().addDamageHate(player, 0, ai.hate);
                    npc.getAggroList().remove(activeChar, true);
                }
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
