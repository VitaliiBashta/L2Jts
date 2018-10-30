package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.manager.GmManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.impl.CastleSiegeEvent;
import org.mmocore.gameserver.model.instances.ArtefactInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.Log;

import java.util.List;


public class TakeCastle extends Skill {
    public TakeCastle(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (!super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first)) {
            return false;
        }

        if (activeChar == null || !activeChar.isPlayer()) {
            return false;
        }

        final Player player = (Player) activeChar;
        if (player.getClan() == null || !player.isClanLeader()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        final CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
        if (siegeEvent == null) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        final CastleSiegeEvent siegeEvent2 = target.getEvent(CastleSiegeEvent.class);
        if (siegeEvent2 != siegeEvent) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (siegeEvent.getSiegeClan(CastleSiegeEvent.ATTACKERS, player.getClan()) == null) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (player.isMounted()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (!player.isInRangeZ(target, 185) || player.getZDeltaSq(target.getZ()) > 2500) {
            player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        if (first) {
            siegeEvent.broadcastTo(SystemMsg.THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT, CastleSiegeEvent.DEFENDERS);
            final String debug = "TakeCastle: caster: " + activeChar.getName() + ", loc:" + activeChar.getLoc() + ", castle: " + siegeEvent.getName() + ", target: " + target;

            Log.debug(debug);
            GmManager.broadcastMessageToGMs(debug);
        }

        return true;
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                if (!target.isArtefact() || !activeChar.isInRangeZ(target, 185) || activeChar.getZDeltaSq(target.getZ()) > 2500) {
                    continue;
                }
                final Player player = (Player) activeChar;

                final CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
                if (siegeEvent != null) {
                    siegeEvent.broadcastTo(new SystemMessage(SystemMsg.CLAN_S1_HAS_SUCCESSFULLY_ENGRAVED_THE_HOLY_ARTIFACT).addString(player.getClan().getName()), CastleSiegeEvent.ATTACKERS, CastleSiegeEvent.DEFENDERS);
                    siegeEvent.processCastArtefact(player.getClan(), (ArtefactInstance) target);
                }
            }
        }
    }
}