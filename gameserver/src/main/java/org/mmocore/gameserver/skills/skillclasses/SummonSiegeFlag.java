package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.entity.events.objects.SiegeClanObject;
import org.mmocore.gameserver.model.entity.events.objects.ZoneObject;
import org.mmocore.gameserver.model.instances.residences.SiegeFlagInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.FuncMul;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class SummonSiegeFlag extends Skill {
    private final FlagType _flagType;
    private final double _advancedMult;
    public SummonSiegeFlag(final StatsSet set) {
        super(set);
        _flagType = set.getEnum("flagType", FlagType.class);
        _advancedMult = set.getDouble("advancedMultiplier", 1.);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (!activeChar.isPlayer()) {
            return false;
        }
        if (!super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first)) {
            return false;
        }

        final Player player = (Player) activeChar;
        if (player.getClan() == null || !player.isClanLeader()) {
            return false;
        }

        switch (_flagType) {
            case DESTROY:
                //
                break;
            case OUTPOST:
            case NORMAL:
            case ADVANCED:
                if (player.isInZone(ZoneType.RESIDENCE)) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                    return false;
                }

                final Class<? extends SiegeEvent> siegeEventClass = _flagType == FlagType.OUTPOST ? DominionSiegeEvent.class : SiegeEvent.class;
                final SiegeEvent siegeEvent = activeChar.getEvent(siegeEventClass);
                if (siegeEvent == null || !siegeEvent.isInProgress()) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                    return false;
                }

                boolean inZone = false;
                final List<ZoneObject> zones = siegeEvent.getObjects(SiegeEvent.FLAG_ZONES);
                for (final ZoneObject zone : zones) {
                    if (player.isInZone(zone.getZone())) {
                        inZone = true;
                    }
                }

                if (!inZone) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                    return false;
                }

                final SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass().equals(DominionSiegeEvent.class) ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, player.getClan());
                if (siegeClan == null) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                    return false;
                }

                if (siegeClan.getFlag() != null) {
                    player.sendPacket(SystemMsg.AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS, new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final Player player = (Player) activeChar;

        final Clan clan = player.getClan();
        if (clan == null || !player.isClanLeader()) {
            return;
        }

        final Class<? extends SiegeEvent> siegeEventClass = _flagType == FlagType.OUTPOST ? DominionSiegeEvent.class : SiegeEvent.class;
        final SiegeEvent siegeEvent = activeChar.getEvent(siegeEventClass);
        if (siegeEvent == null || !siegeEvent.isInProgress()) {
            return;
        }

        final SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass().equals(DominionSiegeEvent.class) ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, clan);
        if (siegeClan == null) {
            return;
        }

        switch (_flagType) {
            case DESTROY:
                siegeClan.deleteFlag();
                break;
            default:
                if (siegeClan.getFlag() != null) {
                    return;
                }

                // 35062/36590
                final SiegeFlagInstance flag = (SiegeFlagInstance) NpcHolder.getInstance().getTemplate(_flagType == FlagType.OUTPOST ? 36590 : 35062).getNewInstance();
                flag.setClan(siegeClan);
                flag.addEvent(siegeEvent);

                if (_flagType == FlagType.ADVANCED) {
                    flag.addStatFunc(new FuncMul(Stats.MAX_HP, 0x50, flag, _advancedMult));
                }

                flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp(), true);
                flag.setHeading(player.getHeading());

                // Ставим флаг перед чаром
                final int x = (int) (player.getX() + 100 * Math.cos(player.headingToRadians(player.getHeading() - 32768)));
                final int y = (int) (player.getY() + 100 * Math.sin(player.headingToRadians(player.getHeading() - 32768)));
                flag.spawnMe(GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), x, y, player.getGeoIndex()));

                siegeClan.setFlag(flag);
                break;
        }
    }

    public enum FlagType {
        DESTROY,
        NORMAL,
        ADVANCED,
        OUTPOST
    }
}