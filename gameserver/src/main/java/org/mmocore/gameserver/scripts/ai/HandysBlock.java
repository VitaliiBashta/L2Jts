package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import org.mmocore.gameserver.model.entity.BlockCheckerEngine;
import org.mmocore.gameserver.model.instances.BlockInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBlockUpSetState;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * @author n0nam3
 */
public class HandysBlock extends DefaultAI {
    public HandysBlock(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        BlockInstance actor = (BlockInstance) getActor();
        if (caster == null) {
            return;
        }
        if (!caster.isPlayer()) {
            return;
        }
        Player player = caster.getPlayer();
        int arena = player.getBlockCheckerArena();
        if (arena == -1 || arena > 3) {
            return;
        }

        if (player.getTarget() == actor) {
            if (skill.getId() == 5852 || skill.getId() == 5853) {
                ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);

                if (holder.getPlayerTeam(player) == 0 && !actor.isRed()) {
                    actor.changeColor();
                    increaseTeamPointsAndSend(player, holder.getEvent());
                } else if (holder.getPlayerTeam(player) == 1 && actor.isRed()) {
                    actor.changeColor();
                    increaseTeamPointsAndSend(player, holder.getEvent());
                } else {
                    return;
                }

                // 30% chance to drop the event items
                int random = Rnd.get(100);
                // Bond
                if (random > 69 && random <= 84) {
                    dropItem(actor, 13787, holder.getEvent(), player);
                }
                // Land Mine
                else if (random > 84) {
                    dropItem(actor, 13788, holder.getEvent(), player);
                }
            }
        }
    }

    private void increaseTeamPointsAndSend(Player player, BlockCheckerEngine eng) {
        int team = eng.getHolder().getPlayerTeam(player);
        eng.increasePlayerPoints(player, team);

        int timeLeft = (int) ((eng.getStarterTime() - System.currentTimeMillis()) / 1000);
        boolean isRed = eng.getHolder().getRedPlayers().contains(player);

        ExBlockUpSetState changePoints = new ExBlockUpSetState(timeLeft, eng.getBluePoints(), eng.getRedPoints());
        ExBlockUpSetState secretPoints = new ExBlockUpSetState(timeLeft, eng.getBluePoints(), eng.getRedPoints(), isRed, player, eng.getPlayerPoints(player, isRed));

        eng.getHolder().broadCastPacketToTeam(changePoints);
        eng.getHolder().broadCastPacketToTeam(secretPoints);
    }

    private void dropItem(NpcInstance block, int id, BlockCheckerEngine eng, Player player) {
        ItemInstance drop = ItemFunctions.createItem(id);
        drop.dropToTheGround(block, Location.findPointToStay(block, 50));
        eng.addNewDrop(drop);
    }
}