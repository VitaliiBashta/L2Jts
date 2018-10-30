package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class ClanGate extends Skill {
    public ClanGate(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (!activeChar.isPlayer()) {
            return false;
        }

        final Player player = (Player) activeChar;
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return false;
        }

        final IBroadcastPacket msg = Call.canSummonHere(player);
        if (msg != null) {
            activeChar.sendPacket(msg);
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        final Player player = (Player) activeChar;
        final Clan clan = player.getClan();
        clan.broadcastToOtherOnlineMembers(SystemMsg.COURT_MAGICIAN_THE_PORTAL_HAS_BEEN_CREATED, player);

        getEffects(skillEntry, activeChar, activeChar, getActivateRate() > 0, true);
    }
}
