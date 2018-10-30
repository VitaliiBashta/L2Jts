package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

import java.util.List;

/**
 * @author SYS
 * @author KilRoy
 */
public final class MoonlightTombstoneInstance extends NpcInstance {
    private static final int key_base_tower_main = 9714;
    private int i_ai3;

    public MoonlightTombstoneInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        i_ai3 = 0;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1006&")) {
            if (command.endsWith("reply=1")) {
                if (player.getParty() != null) {
                    if (i_ai3 == 0) {
                        if (ItemFunctions.getItemCount(player, key_base_tower_main) > 0) {
                            final List<Player> partyMembers = player.getParty().getPartyMembers();
                            for (final Player partyMember : partyMembers) {
                                if (!isInRangeZ(partyMember, getInteractDistance(partyMember) * 2)) {
                                    showChatWindow(player, "pts/hellbound/tombstone_main_tower002b.htm");
                                    return;
                                }
                            }

                            ItemFunctions.removeItem(player, key_base_tower_main, 1);
                            i_ai3 = 1;
                            player.getReflection().startCollapseTimer(5 * 60 * 1000L);
                            broadcastPacketToOthers(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                            player.getReflection().setCoreLoc(player.getReflection().getReturnLoc());
                            player.getReflection().setReturnLoc(new Location(16280, 283448, -9704));
                            showChatWindow(player, "pts/hellbound/tombstone_main_tower002d.htm");
                        } else {
                            showChatWindow(player, "pts/hellbound/tombstone_main_tower002a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/hellbound/tombstone_main_tower002c.htm");
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/tombstone_main_tower002.htm");
                }
            }
        }
        super.onBypassFeedback(player, command);
    }
}