package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FestivalMonsterInstance extends MonsterInstance {
    protected int _bonusMultiplier = 1;

    /**
     * Constructor<?> of L2FestivalMonsterInstance (use L2Character and L2NpcInstance constructor).<BR><BR>
     * <p/>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to set the _template of the L2FestivalMonsterInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR) </li>
     * <li>Set the name of the L2MonsterInstance</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it </li><BR><BR>
     *
     * @param objectId Identifier of the object to initialized
     * @param template to apply to the NPC
     */
    public FestivalMonsterInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);

        _hasRandomWalk = false;
    }

    public void setOfferingBonus(final int bonusMultiplier) {
        _bonusMultiplier = bonusMultiplier;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        final List<Player> pl = World.getAroundPlayers(this);
        if (pl.isEmpty()) {
            return;
        }
        final List<Player> alive = new ArrayList<>(9);
        for (final Player p : pl) {
            if (!p.isDead()) {
                alive.add(p);
            }
        }
        if (alive.isEmpty()) {
            return;
        }

        final Player target = alive.get(Rnd.get(alive.size()));
        getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
    }

    /**
     * Actions:
     * <li>Check if the killing object is a player, and then find the party they belong to.</li>
     * <li>Add a blood offering item to the leader of the party.</li>
     * <li>Update the party leader's inventory to show the new item addition.</li>
     */
    @Override
    public void rollRewards(final Map.Entry<RewardType, RewardList> entry, final Creature lastAttacker, final Creature topDamager) {
        super.rollRewards(entry, lastAttacker, topDamager);

        if (entry.getKey() != RewardType.RATED_GROUPED) {
            return;
        }
        if (!topDamager.isPlayable()) {
            return;
        }

        final Player topDamagerPlayer = topDamager.getPlayer();
        final Party associatedParty = topDamagerPlayer.getParty();

        if (associatedParty == null) {
            return;
        }

        final Player partyLeader = associatedParty.getGroupLeader();
        if (partyLeader == null) {
            return;
        }

        final ItemInstance bloodOfferings = ItemFunctions.createItem(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);

        bloodOfferings.setCount(_bonusMultiplier);
        partyLeader.getInventory().addItem(bloodOfferings);
        partyLeader.sendPacket(SystemMessage.obtainItems(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING, _bonusMultiplier, 0));
    }

    @Override
    public boolean isAggressive() {
        return true;
    }

    @Override
    public int getAggroRange() {
        return 1000;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}