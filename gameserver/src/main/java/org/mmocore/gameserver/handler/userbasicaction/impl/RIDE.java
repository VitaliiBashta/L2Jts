package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.jts.dataparser.data.holder.PetDataHolder;
import org.jts.dataparser.data.holder.petdata.PetData;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.impl.UndyingMatchEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class RIDE implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.isOutOfControl() || player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }
        final Servitor pet = player.getServitor();
        if (player.isTransformed()) {
            return;
            //FIXME[K] - найти подходящий мессадж
        } else if (player.isMounted()) {
            if (player.isFlying() && !player.checkLandingState()) // Виверна
            {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION, ActionFail.STATIC);
                return;
            }
            player.dismount();
            return;
        } else if (!player.isInRangeZ(pet, 300)) {
            player.sendPacket(SystemMsg.YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE);
        }
        if (pet == null) {
            return;
        }
        final PetData info = PetDataHolder.getInstance().getPetData(pet.getNpcId());
        if (info != null && (pet.getCurrentFed() * 100 / pet.getMaxFed()) <= info.getLevelStatForLevel(pet.getLevel()).getHungryLimit()) {
            player.sendPacket(SystemMsg.A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
        } else if (player.isInBoat() || player.isSitting()) {
            player.sendPacket(SystemMsg.A_STRIDER_CAN_BE_RIDDEN_ONLY_WHEN_STANDING);
        } else if (player.isDead()) {
            player.sendPacket(SystemMsg.A_STRIDER_CANNOT_BE_RIDDEN_WHEN_DEAD);
        } else if (pet.isDead()) {
            player.sendPacket(SystemMsg.A_DEAD_STRIDER_CANNOT_BE_RIDDEN);
        } else if (pet.isInCombat()) {
            player.sendPacket(SystemMsg.A_STRIDER_IN_BATTLE_CANNOT_BE_RIDDEN);
        } else if (player.getEvent(UndyingMatchEvent.class) != null || player.isInDuel() || player.isInCombat()) {
            player.sendPacket(SystemMsg.A_STRIDER_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE);
        } else if (player.isFishing() || player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null || player.isCastingNow() || player.isParalyzed()) {
            //FIXME[K] - найти подходящий мессадж
        } else {
            player.getEffectList().stopEffect(Skill.SKILL_EVENT_TIMER);
            player.setMount(pet.getTemplate().npcId, pet.getControlItemObjId(), pet.getLevel(), pet.getCurrentFed());
            pet.unSummon(false, false);
        }
    }
}
