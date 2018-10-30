package org.mmocore.gameserver.skills.skillclasses;

import org.jts.dataparser.data.holder.PetDataHolder;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.List;

public class PetSummon extends Skill {
    public PetSummon(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        final Player player = activeChar.getPlayer();
        if (player == null) {
            return false;
        }

        if (player.getPetControlItem() == null) {
            return false;
        }

        final int npcId = PetDataHolder.getInstance().getPetTemplateId(player.getPetControlItem().getItemId());
        if (npcId == 0) {
            return false;
        }

        if (player.isInCombat()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT);
            return false;
        }

        if (player.isProcessingRequest()) {
            player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }

        if (player.isMounted() || player.getServitor() != null) {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME);
            return false;
        }

        if (player.isInBoat()) {
            player.sendPacket(SystemMsg.YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION);
            return false;
        }

        if (player.isInFlyingTransform() || player.isCursedWeaponEquipped()) {
            player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(player.getPetControlItem().getItemId()));
            return false;
        }

        if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH);
            return false;
        }

        for (final GameObject o : World.getAroundObjects(player, 120, 200)) {
            if (o.isDoor()) {
                player.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
                return false;
            }
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature caster, final List<Creature> targets) {
        final Player activeChar = caster.getPlayer();

        activeChar.summonPet(activeChar.getPetControlItem(), Location.findPointToStay(activeChar, 50, 70));

        if (isSSPossible()) {
            caster.unChargeShots(isMagic());
        }
    }
}
