package org.mmocore.gameserver.handler.usercommands.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.handler.usercommands.IUserCommandHandler;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.entity.events.impl.UndyingMatchEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.SendTradeDone;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;

/**
 * @author KilRoy
 * Support for command: /mount /dismount
 */
public class MountDismount implements IUserCommandHandler {
    private static final int[] COMMAND_IDS = {61, 62};

    @Override
    public boolean useUserCommand(final int id, final Player activeChar) {
        if (!ArrayUtils.contains(COMMAND_IDS, id)) {
            return false;
        }

        switch (id) {
            case 61:
                if (activeChar.getServitor() == null) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_PET);
                    return false;
                }

                final Servitor pet = activeChar.getServitor();

                if (activeChar.isTransformed()) {
                    //FIXME[K] - найти подходящий мессадж
                } else if (pet == null || !pet.isMountable()) {
                    if (activeChar.isMounted()) {
                        if (activeChar.isFlying() && !activeChar.checkLandingState()) // Виверна
                        {
                            activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION, ActionFail.STATIC);
                            return false;
                        }
                        activeChar.dismount();
                    }
                } else if (!activeChar.isInRangeZ(pet, 300)) {
                    activeChar.sendPacket(SystemMsg.YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE);
                } else if ((pet.getCurrentFed() * 100 / pet.getMaxFed()) <= 10) {
                    activeChar.sendPacket(SystemMsg.A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
                } else if (activeChar.isInBoat() || activeChar.isSitting()) {
                    activeChar.sendPacket(SystemMsg.A_STRIDER_CAN_BE_RIDDEN_ONLY_WHEN_STANDING);
                } else if (activeChar.isDead()) {
                    activeChar.sendPacket(SystemMsg.A_STRIDER_CANNOT_BE_RIDDEN_WHEN_DEAD);
                } else if (pet.isDead()) {
                    activeChar.sendPacket(SystemMsg.A_DEAD_STRIDER_CANNOT_BE_RIDDEN);
                } else if (pet.isInCombat()) {
                    activeChar.sendPacket(SystemMsg.A_STRIDER_IN_BATTLE_CANNOT_BE_RIDDEN);
                } else if (activeChar.getEvent(UndyingMatchEvent.class) != null || activeChar.isInDuel() || activeChar.isInCombat()) {
                    activeChar.sendPacket(SystemMsg.A_STRIDER_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE);
                } else if (activeChar.isFishing() || activeChar.isCursedWeaponEquipped() || activeChar.getActiveWeaponFlagAttachment() != null || activeChar.isCastingNow() || activeChar.isParalyzed()) {
                    //FIXME[K] - найти подходящий мессадж
                } else {
                    activeChar.getEffectList().stopEffect(Skill.SKILL_EVENT_TIMER);
                    activeChar.setMount(pet.getTemplate().npcId, pet.getControlItemObjId(), pet.getLevel(), pet.getCurrentFed());
                    pet.unSummon(false, false);
                }
                break;
            case 62:
                final Servitor mountedPet = activeChar.getServitor();
                if (mountedPet == null || !mountedPet.isMountable()) {
                    if (activeChar.isMounted()) {
                        if (activeChar.isFlying() && !activeChar.checkLandingState()) // Виверна TODO[K] - проверять высоту полета и минимальную Z координату под чаром и слать 1158 мессадж
                        {
                            activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION, ActionFail.STATIC);
                            return false;
                        }
                        for (final ItemInstance items : ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(activeChar.getObjectId(), ItemLocation.PET_INVENTORY)) //FIXME[K] - возможно стоит выполнять в синхронизации с инвентарем.
                        {
                            if (items.getLocation() == ItemLocation.PET_INVENTORY) {
                                activeChar.sendPacket(SystemMsg.THERE_ARE_ITEMS_IN_YOUR_PET_INVENTORY_RENDERING_YOU_UNABLE_TO_SELLTRADEDROP_PET_SUMMONING_ITEMS, SendTradeDone.FAIL, ActionFail.STATIC);
                                break;
                            }
                        }
                        activeChar.dismount();
                    }
                }
                break;
        }

        return true;
    }

    @Override
    public final int[] getUserCommandList() {
        return COMMAND_IDS;
    }
}