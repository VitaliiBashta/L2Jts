package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.bosses.BaylorManager;
import org.mmocore.gameserver.scripts.instances.CrystalCaverns;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * @author pchayka
 */
public class CrystalCavernControllerInstance extends NpcInstance {
    public CrystalCavernControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String htmlpath = null;
        if (val == 0) {
            if (player.isInParty() && player.getParty().getGroupLeader() == player) {
                if (getNpcId() == 32276) {
                    htmlpath = "pts/crystalcave/a_telecube_balor001.htm";
                } else if (getNpcId() == 32277) {
                    htmlpath = "pts/crystalcave/b_telecube_balor001.htm";
                } else if (getNpcId() == 32278) {
                    htmlpath = "pts/crystalcave/c_telecube_balor001.htm";
                } else if (getNpcId() == 32279) {
                    if (player.getQuestState(131) != null && !player.getQuestState(131).isCompleted() && player.getQuestState(131).getInt("the_bird_of_cage") == 3) {
                        htmlpath = "pts/crystalcave/telecube_balor001a.htm";
                    } else {
                        htmlpath = "pts/crystalcave/telecube_balor001.htm";
                    }
                } else if (getNpcId() == 32280) {
                    htmlpath = "pts/crystalcave/telecube_parme001.htm";
                }
            } else {
                htmlpath = "pts/crystalcave/a_telecube_balor002.htm";
            }
        } else {
            htmlpath = "pts/crystalcave/a_telecube_balor002.htm";
        }
        return htmlpath;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_next")) {
            if (player.isInParty()) {
                if (!player.getParty().isLeader(player)) {
                    showChatWindow(player, "pts/crystalcave/c_telecube_balor002.htm");
                    return;
                }
                for (Player p : player.getParty().getPartyMembers()) {
                    if (ItemFunctions.getItemCount(p, 9697) < 1) {
                        ChatUtils.shout(this, NpcString.S1_YOU_DONT_HAVE_CLEAR_CRYSTAL, p.getName());
                        showChatWindow(player, "pts/crystalcave/c_telecube_balor001a.htm");
                        return;
                    }
                    if (!isInRange(p, 400)) {
                        ChatUtils.shout(this, NpcString.S1_IF_YOU_ARE_TOO_FAR_AWAY_FROM_ME__I_CANT_LET_YOU_GO, p.getName());
                        return;
                    }
                }
                for (Player p : player.getParty().getPartyMembers()) {
                    ItemFunctions.removeItem(p, 9697, 1, true);
                    p.teleToLocation(new Location(142649, 142657, -15658));
                }
                deleteMe();
            }
        }
        if (command.equalsIgnoreCase("request_emerald")) {
            ((CrystalCaverns) getReflection()).notifyEmeraldRequest();
        } else if (command.equalsIgnoreCase("request_coral")) {
            ((CrystalCaverns) getReflection()).notifyCoralRequest();
        } else if (command.equalsIgnoreCase("request_baylor")) {
            int state = BaylorManager.canIntoBaylorLair(player);
            if (state == 1 || state == 2) {
                showChatWindow(player, "default/32276-1.htm");
                return;
            } else if (state == 4) {
                showChatWindow(player, "default/32276-2.htm");
                return;
            } else if (state == 3) {
                showChatWindow(player, "default/32276-3.htm");
                return;
            }
            if (player.isInParty()) {
                if (!player.getParty().isLeader(player)) {
                    showChatWindow(player, "pts/crystalcave/a_telecube_balor002.htm");
                    return;
                }
                for (Player p : player.getParty().getPartyMembers()) {
                    if (ItemFunctions.getItemCount(p, 9695) < 1) {
                        ChatUtils.shout(this, NpcString.S1_YOU_DONT_HAVE_BLUE_CRYSTAL, p.getName());
                        showChatWindow(player, "pts/crystalcave/a_telecube_balor001a.htm");
                        return;
                    }
                    if (ItemFunctions.getItemCount(p, 9696) < 1) {
                        ChatUtils.shout(this, NpcString.S1_YOU_DONT_HAVE_RED_CRYSTAL, p.getName());
                        showChatWindow(player, "pts/crystalcave/a_telecube_balor001a.htm");
                        return;
                    }
                    if (ItemFunctions.getItemCount(p, 9697) < 1) {
                        ChatUtils.shout(this, NpcString.S1_YOU_DONT_HAVE_CLEAR_CRYSTAL, p.getName());
                        showChatWindow(player, "pts/crystalcave/a_telecube_balor001a.htm");
                        return;
                    }
                    if (!isInRange(p, 400)) {
                        ChatUtils.shout(this, NpcString.S1_IF_YOU_ARE_TOO_FAR_AWAY_FROM_ME__I_CANT_LET_YOU_GO, p.getName());
                        return;
                    }
                }
                ItemFunctions.addItem(player, 10015, 1, true);
                for (Player p : player.getParty().getPartyMembers()) {
                    ItemFunctions.removeItem(p, 9695, 1, true);
                    ItemFunctions.removeItem(p, 9696, 1, true);
                    ItemFunctions.removeItem(p, 9697, 1, true);
                    p.teleToLocation(new Location(153526, 142172, -12736));
                }
                BaylorManager.entryToBaylorLair(player);
                deleteMe();
            }
        } else if (command.equalsIgnoreCase("request_parme")) {
            randomParmeTeleport(player); // Offlike random teleport
        } else if (command.equalsIgnoreCase("request_exit")) {
            if (getReflection().getInstancedZoneId() == 10) {
                player.teleToLocation(getReflection().getInstancedZone().getReturnCoords(), ReflectionManager.DEFAULT);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void randomParmeTeleport(final Player player) {
        final int randomRequest = Rnd.get(100);
        if (randomRequest < 12)
            player.teleToLocation(new Location(153768, 142132, -9756));
        else if (randomRequest < 24)
            player.teleToLocation(new Location(153704, 142248, -9756));
        else if (randomRequest < 36)
            player.teleToLocation(new Location(153752, 141980, -9756));
        else if (randomRequest < 48)
            player.teleToLocation(new Location(153488, 141880, -9756));
        else if (randomRequest < 60)
            player.teleToLocation(new Location(153392, 141984, -9756));
        else if (randomRequest < 72)
            player.teleToLocation(new Location(153384, 142120, -9756));
        else if (randomRequest < 84)
            player.teleToLocation(new Location(153448, 142240, -9756));
        else if (randomRequest < 94)
            player.teleToLocation(new Location(153564, 142272, -9756));
        else
            player.teleToLocation(new Location(153636, 141872, -9756));
    }
}