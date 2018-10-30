package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


/**
 * TODO: Выпилить этот далбоетизм и впилить как нужно с эффектами и кондами.
 */
public class Call extends Skill {
    final boolean _party;
    final int _targetItemConsumeId;
    final int _targetItemConsumeCount;

    public Call(final StatsSet set) {
        super(set);
        _party = set.getBool("party", false);
        _targetItemConsumeId = set.getInteger("targetItemConsumeId", 0);
        _targetItemConsumeCount = set.getInteger("targetItemConsumeCount", 0);
    }

    /**
     * Может ли призывающий в данный момент использовать призыв
     */
    public static IBroadcastPacket canSummonHere(final Player activeChar) {
        if (activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.isOutOfControl() || activeChar.isFlying() ||
                activeChar.isFestivalParticipant()) {
            return SystemMsg.NOTHING_HAPPENED;
        }

        // "Нельзя вызывать персонажей в/из зоны свободного PvP"
        // "в зоны осад"
        // "на Олимпийский стадион"
        // "в зоны определенных рейд-боссов и эпик-боссов"
        if (activeChar.isInZoneBattle()
                || activeChar.isActionBlocked(Zone.BLOCKED_ACTION_CALL_PC)
                || activeChar.isInZone(ZoneType.SIEGE)
                || activeChar.isInZone(ZoneType.no_restart)
                || activeChar.isInZone(ZoneType.no_summon)
                || activeChar.isInBoat()
                || !activeChar.getReflection().equals(ReflectionManager.DEFAULT)) {
            return SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;
        }

        //if(activeChar.isInCombat())
        //return Msg.YOU_CANNOT_SUMMON_DURING_COMBAT;

        if (activeChar.isInStoreMode() || activeChar.isInTrade()) {
            return SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE;
        }

        return null;
    }

    /**
     * Может ли цель ответить на призыв
     */
    public static IBroadcastPacket canBeSummoned(final Creature target) {
        if (target == null || !target.isPlayer() || target.getPlayer().isTerritoryFlagEquipped() || target.isFlying() || target.isOutOfControl() ||
                target.getPlayer().isFestivalParticipant() || !target.getPlayer().getPlayerAccess().UseTeleport) {
            return SystemMsg.INVALID_TARGET;
        }

        if (((Player) target).isInOlympiadMode()) {
            return SystemMsg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD;
        }

        if (((Player) target).isActionBlocked(Zone.BLOCKED_ACTION_CALL_PC) || target.isInZoneBattle() || target.isInZone(ZoneType.SIEGE) || target.isInZone(ZoneType.no_restart) || target.isInZone(ZoneType.no_summon) ||
                !target.getReflection().equals(ReflectionManager.DEFAULT) || target.isInBoat()) {
            return SystemMsg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
        }

        // Нельзя призывать мертвых персонажей
        if (target.isAlikeDead()) {
            return new SystemMessage(SystemMsg.C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED).addString(target.getName());
        }

        // Нельзя призывать персонажей, которые находятся в режиме PvP или Combat Mode
        if (target.getPvpFlag() != 0 || target.isInCombat()) {
            return new SystemMessage(SystemMsg.C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED).addString(target.getName());
        }

        final Player pTarget = (Player) target;

        // Нельзя призывать торгующих персонажей
        if (pTarget.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || pTarget.isProcessingRequest()) {
            return new SystemMessage(SystemMsg.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED).addString(
                    target.getName());
        }

        return null;
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (activeChar.isPlayer()) {
            if (_party && ((Player) activeChar).getParty() == null) {
                return false;
            }

            IBroadcastPacket msg = canSummonHere((Player) activeChar);
            if (msg != null) {
                activeChar.sendPacket(msg);
                return false;
            }

            // Эта проверка только для одиночной цели
            if (!_party) {
                if (activeChar.equals(target)) {
                    return false;
                }

                msg = canBeSummoned(target);
                if (msg != null) {
                    activeChar.sendPacket(msg);
                    return false;
                }
            }
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        final IBroadcastPacket msg = canSummonHere((Player) activeChar);
        if (msg != null) {
            activeChar.sendPacket(msg);
            return;
        }

        if (_party) {
            if (((Player) activeChar).getParty() != null) {
                for (final Player target : ((Player) activeChar).getParty().getPartyMembers()) {
                    if (!target.equals(activeChar) && canBeSummoned(target) == null && !target.isTerritoryFlagEquipped()) {
                        target.stopMove();
                        target.teleToLocation(activeChar.getLoc(), activeChar.getGeoIndex());
                        getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
                    }
                }
            }

            if (isSSPossible()) {
                activeChar.unChargeShots(isMagic());
            }
            return;
        }

        for (final Creature target : targets) {
            if (target != null) {
                if (canBeSummoned(target) != null) {
                    continue;
                }

                ((Player) target).summonCharacterRequest((Player) activeChar, _targetItemConsumeId, _targetItemConsumeCount);

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}