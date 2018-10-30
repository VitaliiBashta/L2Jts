package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tp_bookmark.TeleportBookMark;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

/**
 * @author VISTALL
 * @date 17:03/08.08.2011
 */
public class BookMarkTeleport extends Skill {
    public BookMarkTeleport(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove,
                                  final boolean first) {
        if (!activeChar.isPlayer() || !super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first)) {
            return false;
        }

        final Player player = activeChar.getPlayer();

        final TeleportBookMark loc = player.getTeleportBookMarkComponent().getActivatedTeleportBookMark();
        if (loc == null) {
            player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (player.isActionBlocked(Zone.BLOCKED_ACTION_USE_BOOKMARK)) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
            return false;
        }
        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return false;
        }
        if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            return false;
        }
        if (!player.getReflection().equals(ReflectionManager.DEFAULT)) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
            return false;
        }
        if (player.isInDuel()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            return false;
        }
        if (player.isInCombat() || player.getPvpFlag() != 0) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            return false;
        }
        if (player.isOnSiegeField() || player.isInZoneBattle()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGESCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_HIDEOUT_SIEGE);
            return false;
        }
        if (player.isFlying()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            return false;
        }
        if (player.isInWater() || player.isInBoat()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            return false;
        }

        if (first && !player.consumeItem(13016, 1)) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM);
            return false;
        }
        return true;
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        final Player player = activeChar.getPlayer();

        final TeleportBookMark loc = player.getTeleportBookMarkComponent().getActivatedTeleportBookMark();
        if (loc == null)
            return;

        player.getTeleportBookMarkComponent().setActivatedTeleportBookMark(null);

        player.teleToLocation(loc, ReflectionManager.DEFAULT);
    }
}
