package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.Location;

import java.util.List;

public class Teleport extends Skill {
    private final Location _loc;

    public Teleport(final StatsSet set) {
        super(set);
        final String[] cords = set.getString("loc", "").split(";");
        if (cords.length == 3) {
            _loc = new Location(Integer.parseInt(cords[0]), Integer.parseInt(cords[1]), Integer.parseInt(cords[2]));
        } else {
            _loc = null;
        }
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (activeChar.isPlayer()) {
            final Player p = (Player) activeChar;
            if (p.getActiveWeaponFlagAttachment() != null) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
                return false;
            }
            if (p.isInDuel() || p.getTeam() != TeamType.NONE) {
                activeChar.sendMessage(new CustomMessage("common.RecallInDuel"));
                return false;
            }
            if (p.isInOlympiadMode()) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_SKILL_IN_A_GRAND_OLYMPIAD_MATCH);
                return false;
            }
        }

        if (activeChar.isInZone(ZoneType.no_escape) && activeChar.getReflection() != null && activeChar.getReflection().getCoreLoc() != null) {
            if (activeChar.isPlayer()) {
                activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Teleport.Here"));
            }
            return false;
        }
        if (activeChar.isOutOfControl()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(getId(), getLevel()));
            return false;
        }
        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
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
                if (_loc != null) {
                    if (pcTarget.getReflection() != null && !pcTarget.getReflection().isDefault()) {
                        final Location newLoc = pcTarget.getReflection().getReturnLoc() != null ? pcTarget.getReflection().getReturnLoc() : _loc;
                        pcTarget.teleToLocation(newLoc, ReflectionManager.DEFAULT);
                        return;
                    }
                    pcTarget.teleToLocation(_loc, ReflectionManager.DEFAULT);
                    return;

                } else {
                    _log.info("_loc is null: " + skillEntry.getId());
                }
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
