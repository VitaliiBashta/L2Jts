package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.jts.dataparser.data.holder.userbasicaction.UsetBasicActionOptionType;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.instances.residences.SiegeFlagInstance;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.PetSkillsTable;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class SUMMON_ACTION implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        final Servitor summon = player.getServitor();
        if (summon == null) {
            return;
        }
        if (!summon.isSummon() || summon.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        final SiegeEvent<?, ?> siegeEvent = summon.getEvent(SiegeEvent.class);
        final boolean siegeGolem = siegeEvent != null && siegeEvent.containsSiegeSummon(summon) && player.isInZone(ZoneType.SIEGE) != summon.isInZone(ZoneType.SIEGE);
        if (siegeGolem) {
            player.sendActionFailed();
            return;
        }
        if (summon.isDepressed()) {
            player.sendPacket(SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
            return;
        }
        final UsetBasicActionOptionType optionAction = UsetBasicActionOptionType.valueOf(option.get());
        switch (optionAction) {
            case change_mode:
                summon.setFollowMode(!summon.isFollowMode());
                break;
            case attack:
                if (!target.isPresent() || !target.get().isCreature() || summon.isDead()) {
                    player.sendActionFailed();
                    return;
                }
                final Creature targetCreature = (Creature) target.get();
                if (summon == targetCreature) {
                    player.sendPacket(SystemMsg.FAILED_TO_CHANGE_ATTACK_TARGET);
                    return;
                }
                if (targetCreature.isDead()) {
                    player.sendActionFailed();
                    return;
                }
                if (player.isInOlympiadMode() && !player.isOlympiadCompStart()) {
                    player.sendActionFailed();
                    return;
                }
                if (!ctrlPressed && !targetCreature.isAutoAttackable(summon)) {
                    summon.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, targetCreature, AllSettingsConfig.FOLLOW_RANGE);
                    return;
                }
                if (ctrlPressed && !targetCreature.isAttackable(summon)) {
                    summon.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, targetCreature, AllSettingsConfig.FOLLOW_RANGE);
                    return;
                }
                summon.getAI().Attack(targetCreature, ctrlPressed, shiftPressed);
                break;
            case stop:
                summon.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                break;
            case move:
                if (target.isPresent() && summon != target.get() && !summon.isMovementDisabled()) {
                    summon.setFollowMode(false);
                    summon.moveToLocation(target.get().getLoc(), 100, true);
                }
                break;
            case skill:
                if (!target.isPresent()) {
                    player.sendActionFailed();
                    return;
                }
                if (!useSkill.isPresent()) {
                    player.sendActionFailed();
                    return;
                }
                // TODO перенести эти условия в скиллы
                if (id == 1000 && !target.get().isDoor()) // Siege Golem - Siege Hammer
                {
                    player.sendActionFailed();
                    return;
                } else if ((id == 1039 || id == 1040) && (target.get().isDoor() || target.get() instanceof SiegeFlagInstance)) // Swoop Cannon (не может атаковать двери и флаги)
                {
                    player.sendActionFailed();
                    return;
                }
                final int skillLevel = PetSkillsTable.getInstance().getAvailableLevel(summon, useSkill.getAsInt());
                if (skillLevel == 0) {
                    player.sendActionFailed();
                    return;
                }
                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(useSkill.getAsInt(), skillLevel);
                if (skill == null) {
                    player.sendActionFailed();
                    return;
                }
                final Creature aimingTarget = skill.getTemplate().getAimingTarget(summon, target.get());
                if (skill.checkCondition(summon, aimingTarget, ctrlPressed, shiftPressed, true)) {
                    summon.getAI().Cast(skill, aimingTarget, ctrlPressed, shiftPressed);
                } else {
                    player.sendActionFailed();
                }
                break;
        }
    }
}
