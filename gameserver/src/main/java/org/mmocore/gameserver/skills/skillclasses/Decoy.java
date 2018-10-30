package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.DecoyInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.idfactory.IdFactory;

import java.util.List;


/**
 * @author n0nam3
 * @date 23/07/2010 19:14
 */
public class Decoy extends Skill {
    private final int _npcId;
    private final int _lifeTime;

    public Decoy(final StatsSet set) {
        super(set);

        _npcId = set.getInteger("npcId", 0);
        _lifeTime = set.getInteger("lifeTime", 1200) * 1000;
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (activeChar.isAlikeDead() || !activeChar.isPlayer() || !activeChar.equals(target)) // only TARGET_SELF
        {
            return false;
        }

        if (_npcId <= 0) {
            return false;
        }

		/* need correct
		if(activeChar.getPet() != null || activeChar.getPlayer().isMounted())
		{
			activeChar.getPlayer().sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
			return false;
		}
		 */
        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature caster, final List<Creature> targets) {
        final Player activeChar = caster.getPlayer();

        final NpcTemplate DecoyTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
        final DecoyInstance decoy = new DecoyInstance(IdFactory.getInstance().getNextId(), DecoyTemplate, activeChar, _lifeTime);

        decoy.setCurrentHp(decoy.getMaxHp(), false);
        decoy.setCurrentMp(decoy.getMaxMp());
        decoy.setHeading(activeChar.getHeading());
        decoy.setReflection(activeChar.getReflection());

        activeChar.setDecoy(decoy);

        decoy.spawnMe(caster.getLoc());

    }
}