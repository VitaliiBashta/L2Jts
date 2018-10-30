package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.EpicBossState.State;
import org.mmocore.gameserver.scripts.bosses.SailrenManager;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author Mangol
 * @version PTS Freya
 */
public class sailren_quest_npc extends DefaultAI {
    public sailren_quest_npc(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (ask == 314) {
            switch (reply) {
                case 1: {
                    if (player.isInParty()) {
                        if (player.getParty().isLeader(player)) {
                            if (SailrenManager.isEnterToLair()) {
                                if (SailrenManager.getPartyEnter() == 0) {
                                    if (ItemFunctions.getItemCount(player, 8784) >= 1) {
                                        ItemFunctions.removeItem(player, 8784, 1, true);
                                        SailrenManager.setPartyEnter();
                                        for (Player member : player.getParty().getPartyMembers()) {
                                            if (member.isInRange(actor, 1000)) {
                                                member.teleToLocation(27549, -6638, -2008);
                                            }
                                        }
                                        SailrenManager.StateSailren(State.NOTSPAWN);
                                        ThreadPoolManager.getInstance().schedule(new SailrenManager.StartAttack(1), 60000);
                                    } else {
                                        actor.showChatWindow(player, "pts/statue_of_shilen/statue_of_shilen003.htm");
                                    }
                                } else {
                                    actor.showChatWindow(player, "pts/statue_of_shilen/statue_of_shilen006.htm");
                                }
                            } else {
                                actor.showChatWindow(player, "pts/statue_of_shilen/statue_of_shilen005.htm");
                            }
                        } else {
                            actor.showChatWindow(player, "pts/statue_of_shilen/statue_of_shilen004.htm");
                        }
                    } else {
                        actor.showChatWindow(player, "pts/statue_of_shilen/statue_of_shilen002.htm");
                    }
                }
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}
