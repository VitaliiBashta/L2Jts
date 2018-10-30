package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class BuronInstance extends NpcInstance {
    private static final int permit_of_darion = 9674;
    private static final int helmet_of_natives = 9669;
    private static final int coat_of_natives = 9670;
    private static final int pants_of_natives = 9671;

    public BuronInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int hellboundLevel = HellboundManager.getHellboundLevel();
        if (hellboundLevel <= 1) {
            showChatWindow(talker, "pts/hellbound/buron001.htm");
        } else if (hellboundLevel > 1 && hellboundLevel < 5) {
            showChatWindow(talker, "pts/hellbound/buron002.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/buron001a.htm");
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (command.startsWith("menu_select?ask=-1006&")) {
            final int i0 = HellboundManager.getHellboundLevel();
            if (command.endsWith("reply=1")) {
                if (i0 >= 2) {
                    if (ItemFunctions.getItemCount(player, permit_of_darion) >= 10) {
                        ItemFunctions.addItem(player, helmet_of_natives, 1);
                        ItemFunctions.removeItem(player, permit_of_darion, 10);
                    } else {
                        showChatWindow(player, "pts/hellbound/buron002a.htm");
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/buron002b.htm");
                }
            } else if (command.endsWith("reply=2")) {
                if (i0 >= 2) {
                    if (ItemFunctions.getItemCount(player, permit_of_darion) >= 10) {
                        ItemFunctions.addItem(player, coat_of_natives, 1);
                        ItemFunctions.removeItem(player, permit_of_darion, 10);
                    } else {
                        showChatWindow(player, "pts/hellbound/buron002a.htm");
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/buron002b.htm");
                }
            } else if (command.endsWith("reply=3")) {
                if (i0 >= 2) {
                    if (ItemFunctions.getItemCount(player, permit_of_darion) >= 10) {
                        ItemFunctions.addItem(player, pants_of_natives, 1);
                        ItemFunctions.removeItem(player, permit_of_darion, 10);
                    } else {
                        showChatWindow(player, "pts/hellbound/buron002a.htm");
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/buron002b.htm");
                }
            } else if (command.endsWith("reply=4")) {
                if (i0 == 1) {
                    showChatWindow(player, "pts/hellbound/buron003a.htm");
                } else if (i0 == 2) {
                    showChatWindow(player, "pts/hellbound/buron003b.htm");
                } else if (i0 == 3) {
                    showChatWindow(player, "pts/hellbound/buron003c.htm");
                } else if (i0 == 4) {
                    showChatWindow(player, "pts/hellbound/buron003h.htm");
                } else if (i0 == 5) {
                    showChatWindow(player, "pts/hellbound/buron003d.htm");
                } else if (i0 == 6) {
                    showChatWindow(player, "pts/hellbound/buron003i.htm");
                } else if (i0 == 7) {
                    showChatWindow(player, "pts/hellbound/buron003e.htm");
                } else if (i0 == 8) {
                    showChatWindow(player, "pts/hellbound/buron003f.htm");
                } else if (i0 == 9) {
                    showChatWindow(player, "pts/hellbound/buron003g.htm");
                } else if (i0 == 10) {
                    showChatWindow(player, "pts/hellbound/buron003j.htm");
                } else if (i0 == 11) {
                    showChatWindow(player, "pts/hellbound/buron003k.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}