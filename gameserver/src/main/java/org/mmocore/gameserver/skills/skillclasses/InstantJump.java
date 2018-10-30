package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FlyToLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;

import java.util.List;

public class InstantJump extends Skill {
    public InstantJump(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(SkillEntry skillEntry, Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((activeChar != null) && (activeChar.isPlayer()) && (activeChar.getPlayer().isMounted())) {
            return false;
        }
        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(SkillEntry skillEntry, Creature activeChar, List<Creature> targets) {
        if (targets.size() == 0) {
            return;
        }
        Creature target = targets.get(0);
        if (Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0.0D, activeChar, skillEntry))) {
            if (activeChar.isPlayer()) {
                activeChar.sendPacket(new SystemMessage(SystemMsg.C1_DODGES_THE_ATTACK).addName(target));
            }
            if (target.isPlayer()) {
                target.sendPacket(new SystemMessage(SystemMsg.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(activeChar));
            }
            return;
        }
        int px = target.getX();
        int py = target.getY();
        double ph = PositionUtils.convertHeadingToDegree(target.getHeading());

        ph += 180D;
        if (ph > 360D) {
            ph -= 360D;
        }
        ph = 3.14D * ph / 180D;

        int x = (int) (px + 25.0D * Math.cos(ph));
        int y = (int) (py + 25.0D * Math.sin(ph));
        int z = target.getZ();

        Location loc = new Location(x, y, z);
        if (GeodataConfig.ALLOW_GEODATA) {
            loc = GeoEngine.moveCheck(activeChar.getX(), activeChar.getY(), activeChar.getZ(), x, y, activeChar.getReflection().getGeoIndex());
        }
        if (loc.distance(target.getLoc()) < 10D) {
            x = (int) (px - 25D * Math.cos(ph));
            y = (int) (py - 25D * Math.sin(ph));
            z = target.getZ();

            loc = new Location(x, y, z);
            if (GeodataConfig.ALLOW_GEODATA) {
                loc = GeoEngine.moveCheck(activeChar.getX(), activeChar.getY(), activeChar.getZ(), x, y, activeChar.getReflection().getGeoIndex());
            }
        }
        if (target.isMonster()) {
            MonsterInstance monster = (MonsterInstance) target;
            monster.abortAttack(true, true);
            monster.abortCast(true, true);
            monster.getAI().notifyEvent(CtrlEvent.EVT_THINK, new Object[0]);
        } else if ((target.isPlayable()) && (target.getTarget() != null)) {
            target.setTarget(null);
            target.abortAttack(true, true);
            target.abortCast(true, true);
        }
        target.abortAttack(true, true);
        target.stopMove();
        target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, activeChar);
        activeChar.broadcastPacket(new FlyToLocation(activeChar, loc, FlyToLocation.FlyType.DUMMY));
        activeChar.setXYZ(loc.x, loc.y, loc.z);
        activeChar.setTarget(target);
        activeChar.setHeading(PositionUtils.calculateHeadingFrom(activeChar, target));
        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        activeChar.validateLocation(1);
    }
}