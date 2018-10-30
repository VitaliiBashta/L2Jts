package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

/**
 * Created by [STIGMATED] : 10.08.12 : 17:10
 */
public class Escape extends Skill {
    private final TeleportType _teleportType;

    public Escape(final StatsSet set) {
        super(set);
        _teleportType = TeleportType.valueOf(set.getString("teleType"));
    }

    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        final Player player = activeChar.getPlayer();
        // BSOE в кланхолл/замок работает только при наличии оного
        if (getHitTime() == 200) {
            if (_teleportType == TeleportType.AGIT) {
                if (player.getClan() == null || player.getClan().getHasHideout() == 0) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
                    return false;
                }
            } else if (_teleportType == TeleportType.CASTLE) {
                if (player.getClan() == null || player.getClan().getCastle() == 0) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
                    return false;
                }
            } else if (_teleportType == TeleportType.FORTRESS) {
                if (player.getClan() == null || player.getClan().getHasFortress() == 0) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(_itemConsumeId[0]));
                    return false;
                }
            }


        } else if (player.isOutOfControl()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(getId(), getLevel()));
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            final Player pcTarget = target.getPlayer();
            if (pcTarget == null) {
                continue;
            }
            if (!pcTarget.getPlayerAccess().UseTeleport) {
                continue;
            }
            if (pcTarget.getActiveWeaponFlagAttachment() != null) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
                continue;
            }
            if (pcTarget.isFestivalParticipant()) {
                activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Teleport.Festival"));
                continue;
            }
            if (pcTarget.isInOlympiadMode()) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD);
                return;
            }
            if (pcTarget.isInDuel() || pcTarget.getTeam() != TeamType.NONE) {
                activeChar.sendMessage(new CustomMessage("common.RecallInDuel"));
                return;
            }
            switch (_teleportType) {
                // TODO town должно тпшить в ближайший город, виладж в ближайший город/деревню
                case TOWN:
                case VILLAGE: {
                    pcTarget.teleToClosestTown();
                    break;
                }
                case AGIT: {
                    pcTarget.teleToClanhall();
                    break;
                }
                case CASTLE: {
                    pcTarget.teleToCastle();
                    break;
                }
                case FORTRESS: {
                    pcTarget.teleToFortress();
                    break;
                }
            }
        }
    }

    private enum TeleportType {
        TOWN, //
        VILLAGE, //
        AGIT, //
        CASTLE, //
        FORTRESS
    }
}
