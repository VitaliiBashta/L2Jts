package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.jts.dataparser.data.holder.userbasicaction.UsetBasicActionOptionType;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.model.instances.PetBabyInstance;
import org.mmocore.gameserver.model.instances.PetInstance;
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
public class PET_ACTION implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option,
                          final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        final Servitor servitor = player.getServitor();
        if (servitor == null) {
            return;
        }
        if (!servitor.isPet() || servitor.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (servitor.isDepressed()) {
            player.sendPacket(SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
            return;
        }
        final PetInstance pet = (PetInstance) servitor;
        if (pet.isVeryHungryPet()) {
            player.sendPacket(SystemMsg.WHEN_YOUR_PETS_HUNGER_GAUGE_IS_AT_0_PERCENT_YOU_CANNOT_USE_YOUR_PET);
            return;
        }
        if (player.getLevel() + 20 <= pet.getLevel()) {
            player.sendPacket(SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
            return;
        }
        final UsetBasicActionOptionType optionAction = UsetBasicActionOptionType.valueOf(option.get());
        switch (optionAction) {
            case change_mode:
                pet.setFollowMode(!pet.isFollowMode());
                break;
            case attack:
                if (!target.isPresent() || !target.get().isCreature() || pet.isDead()) {
                    player.sendActionFailed();
                    return;
                }
                final Creature targetCreature = (Creature) target.get();
                if (pet == targetCreature) {
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
                // Sin Eater
                if (pet.getTemplate().getNpcId() == PetUtils.PetId.SIN_EATER_ID) {
                    return;
                }
                if (!ctrlPressed && !targetCreature.isAutoAttackable(pet)) {
                    pet.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, targetCreature, AllSettingsConfig.FOLLOW_RANGE);
                    return;
                }
                if (ctrlPressed && !targetCreature.isAttackable(pet)) {
                    pet.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, targetCreature, AllSettingsConfig.FOLLOW_RANGE);
                    return;
                }
                pet.getAI().Attack(targetCreature, ctrlPressed, shiftPressed);
                break;
            case stop:
                pet.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                break;
            case move:
                if (target.isPresent() && pet != target.get() && !pet.isMovementDisabled()) {
                    pet.setFollowMode(false);
                    pet.moveToLocation(target.get().getLoc(), 100, true);
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
                if (id == 1001) {
                    player.sendActionFailed();
                    return;
                }
                if (id == 1070) {
                    if (pet instanceof PetBabyInstance) {
                        ((PetBabyInstance) pet).triggerBuff();
                    }
                }
                final int skillLevel = PetSkillsTable.getInstance().getAvailableLevel(pet, useSkill.getAsInt());
                if (skillLevel == 0) {
                    player.sendActionFailed();
                    return;
                }
                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(useSkill.getAsInt(), skillLevel);
                if (skill == null) {
                    player.sendActionFailed();
                    return;
                }
                final Creature aimingTarget = skill.getTemplate().getAimingTarget(pet, target.isPresent() ? target.get() : null);
                if (skill.checkCondition(pet, aimingTarget, ctrlPressed, shiftPressed, true)) {
                    pet.getAI().Cast(skill, aimingTarget, ctrlPressed, shiftPressed);
                } else {
                    player.sendActionFailed();
                }
                break;
        }
    }
}
