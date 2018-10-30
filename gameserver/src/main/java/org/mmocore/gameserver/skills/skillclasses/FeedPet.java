package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SetupGauge;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class FeedPet extends Skill {
    private final int feedNormal;
    private final int feedMounted;
    private final int feedFlyMounted;

    public FeedPet(StatsSet set) {
        super(set);
        final int feedValues[] = set.getIntegerArray("feedValues", null);
        if (feedValues == null || feedValues.length != 3) {
            throw new Error("Invalid feed value in skill " + this);
        }

        feedNormal = feedValues[0];
        feedMounted = feedValues[1];
        feedFlyMounted = feedValues[2];
    }

    @Override
    public final boolean checkCondition(SkillEntry skillEntry, final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!activeChar.isPlayable()) {
            return false;
        }

        if (!activeChar.isPet() && activeChar.isPlayer() && !activeChar.getPlayer().isMounted()) {
            final int consumedItemId = getItemConsumeId()[skillEntry.getLevel() - 1];
            if (consumedItemId != 0) {
                activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(consumedItemId));
                return false;
            }
        }
        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public final void useSkill(SkillEntry skillEntry, Creature caster, List<Creature> targets) {
        if (!caster.isPlayable()) {
            return;
        }

        final Player player = caster.getPlayer();
        if (player.isMounted()) {
            final int maxFed = player.getMountMaxFed();
            final int feed = player.isFlying() ? feedFlyMounted : feedMounted;
            int currentFeed = player.getMountCurrentFed() + feed;
            if (feed > maxFed) {
                currentFeed = player.getMountMaxFed();
            }

            player.setMountCurrentFed(currentFeed);
            player.sendPacket(new SetupGauge(player, SetupGauge.GREEN, currentFeed * 10000, maxFed * 10000));
        } else {
            final PetInstance pet = (PetInstance) player.getServitor();
            final int maxFed = pet.getMaxFed();
            final int feed = feedNormal;
            int currentFed = pet.getCurrentFed() + feed;
            if (currentFed > maxFed) {
                currentFed = maxFed;
            }

            pet.setCurrentFed(currentFed);
            pet.sendStatusUpdate();
            if (pet.getNpcState() == 100) {
                pet.setNpcState(101);
            }
            if (pet.isHungryPet()) {
                pet.getPlayer().sendPacket(SystemMsg.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY);
            }
        }
    }
}