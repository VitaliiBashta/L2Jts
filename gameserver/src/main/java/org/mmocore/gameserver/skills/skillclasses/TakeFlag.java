package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.entity.events.objects.TerritoryWardObject;
import org.mmocore.gameserver.model.entity.residence.Dominion;
import org.mmocore.gameserver.model.instances.residences.SiegeFlagInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class TakeFlag extends Skill {
    public TakeFlag(final StatsSet set) {
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

        if (player.getClan() == null) {
            return false;
        }

        final DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
        if (siegeEvent1 == null) {
            return false;
        }

        if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject)) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (player.isMounted()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || !target.getClan().equals(player.getClan())) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        final DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
        if (siegeEvent2 == null || siegeEvent1 != siegeEvent2) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skillEntry));
            return false;
        }

        if (!player.isInRangeZ(target, 200)) {
            player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        return true;
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                final Player player = (Player) activeChar;
                final DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
                if (siegeEvent1 == null) {
                    continue;
                }
                if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || !target.getClan().equals(player.getClan())) {
                    continue;
                }
                if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject)) {
                    continue;
                }
                final DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
                if (siegeEvent2 == null || !siegeEvent1.equals(siegeEvent2)) {
                    continue;
                }

                // текущая територия, к которой пойдет Вард
                final Dominion dominion = siegeEvent1.getResidence();
                // вард с вражеской територии
                final TerritoryWardObject wardObject = (TerritoryWardObject) player.getActiveWeaponFlagAttachment();
                // територия с которой уйдет Вард
                final DominionSiegeEvent siegeEvent3 = wardObject.getEvent();
                final Dominion dominion3 = siegeEvent3.getResidence();
                // айди територии к которой относится Вард
                final int wardDominionId = wardObject.getDominionId();

                // удаляем с инвентарями вард, и освободжаем ресурсы
                wardObject.despawnObject(siegeEvent3);
                // удаляем Вард
                dominion3.removeFlag(wardDominionId);
                // добавляем Вард
                dominion.addFlag(wardDominionId);
                // позиции вардов с текущей територии
                // спавним Варда, уже в новой територии
                siegeEvent1.spawnAction("ward_" + wardDominionId, true);

                final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
                runnerEvent.broadcastTo(new SystemMessage(SystemMsg.CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2S_TERRITORY_WARD).addString(dominion.getOwner().getName()).addResidenceName(wardDominionId));
            }
        }
    }
}