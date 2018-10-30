package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.ChestInstance;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Unlock extends Skill {
    private final int _unlockPower;

    public Unlock(final StatsSet set) {
        super(set);
        _unlockPower = set.getInteger("unlockPower", 0) + 100;
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (target == null || target instanceof ChestInstance && target.isDead()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        if (target instanceof ChestInstance && activeChar.isPlayer()) {
            return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
        }

        if (!target.isDoor() || _unlockPower == 0) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        final DoorInstance door = (DoorInstance) target;

        if (door.isOpen()) {
            activeChar.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
            return false;
        }

        if (!door.isUnlockable()) {
            activeChar.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }

        if (door.getKey() > 0 && _unlockPower - door.getLevel() * 100 < 0) // ключ не подходит к двери или дверь слишком высокого уровня
        {
            activeChar.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_UNLOCK_THE_DOOR);
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature targ : targets) {
            if (targ != null) {
                if (targ.isDoor()) {
                    final DoorInstance target = (DoorInstance) targ;
                    if (!target.isOpen() && (target.getKey() > 0 || Rnd.chance(_unlockPower - target.getLevel() * 100))) {
                        target.openMe((Player) activeChar, true);
                    } else {
                        activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
                    }
                } else if (targ instanceof ChestInstance) {
                    final ChestInstance target = (ChestInstance) targ;
                    if (!target.isDead()) {
                        target.tryOpen((Player) activeChar, this);
                    }
                }
            }
        }
    }
}
