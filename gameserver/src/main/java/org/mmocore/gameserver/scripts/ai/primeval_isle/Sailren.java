package org.mmocore.gameserver.scripts.ai.primeval_isle;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.SailrenManager;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author Mangol
 */
public class Sailren extends Fighter {
    public Sailren(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        Player player = attacker.getPlayer();
        if (!player.isInParty()) {
            player.teleToLocation(23575, -7727, -1272);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        Player player = killer.getPlayer();
        for (Player member : player.getParty().getPartyMembers()) {
            if (member != null && !member.isDead() && SailrenManager.getZone().checkIfInZone(member)) {
                if (member.getInventoryLimit() * 0.8 < member.getInventory().getSize()) {
                    member.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.WHEN_INVENTORY_WEIGHTNUMBER_ARE_MORE_THAN_80_THE_LIFE_STONE_FROM_THE_BEGINNING_CANNOT_BE_ACQUIRED));
                } else {
                    ItemFunctions.addItem(member, 14828, 1);
                    member.sendPacket(new SystemMessage(SystemMsg.S1).addNpcString(NpcString.LIFE_STONE_FROM_THE_BEGINNING_ACQUIRED));
                }
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected boolean maybeMoveToHome() {
        NpcInstance actor = getActor();
        if (actor != null && !SailrenManager.getZone().checkIfInZone(actor)) {
            teleportHome();
        }
        return false;
    }
}
