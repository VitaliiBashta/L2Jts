package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class BernardeInstance extends NpcInstance {
    private static final int permit_of_darion = 9674;
    private static final int holy_water = 9673;
    private static final int treasure_of_natives = 9684;

    public BernardeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();

        if (talker.getTransformationId() == 101) {
            if (i0 == 1) {
                showChatWindow(talker, "pts/hellbound/bernarde001a.htm");
            } else if (i0 == 2) {
                showChatWindow(talker, "pts/hellbound/bernarde002.htm");
            } else if (i0 == 3) {
                showChatWindow(talker, "pts/hellbound/bernarde001c.htm");
            } else if (i0 == 4) {
                showChatWindow(talker, "pts/hellbound/bernarde001d.htm");
            } else {
                showChatWindow(talker, "pts/hellbound/bernarde001f.htm");
            }
        } else if (i0 < 2) {
            showChatWindow(talker, "pts/hellbound/bernarde001.htm");
        } else {
            showChatWindow(talker, "pts/hellbound/bernarde003.htm");
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
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "pts/hellbound/bernarde002a.htm");
            } else if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, permit_of_darion) >= 5) {
                    ItemFunctions.addItem(player, holy_water, 1);
                    ItemFunctions.removeItem(player, permit_of_darion, 5);
                    showChatWindow(player, "pts/hellbound/bernarde002b.htm");
                    if (!ServerVariables.getBool("HB_judesBoxes")) {
                        ServerVariables.set("HB_judesBoxes", true);
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/bernarde002c.htm");
                }
            } else if (command.endsWith("reply=3")) {
                final int i0 = HellboundManager.getHellboundLevel();

                if (i0 == 1) {
                    showChatWindow(player, "pts/hellbound/bernarde003a.htm");
                } else if (i0 == 2) {
                    showChatWindow(player, "pts/hellbound/bernarde003b.htm");
                } else if (i0 == 3) {
                    showChatWindow(player, "pts/hellbound/bernarde003c.htm");
                } else if (i0 == 4) {
                    showChatWindow(player, "pts/hellbound/bernarde003h.htm");
                } else if (i0 == 5) {
                    showChatWindow(player, "pts/hellbound/bernarde003d.htm");
                } else if (i0 == 6) {
                    showChatWindow(player, "pts/hellbound/bernarde003i.htm");
                } else if (i0 == 7) {
                    showChatWindow(player, "pts/hellbound/bernarde003e.htm");
                } else if (i0 == 8) {
                    showChatWindow(player, "pts/hellbound/bernarde003f.htm");
                } else if (i0 == 9) {
                    showChatWindow(player, "pts/hellbound/bernarde003g.htm");
                } else if (i0 == 10) {
                    showChatWindow(player, "pts/hellbound/bernarde003j.htm");
                } else if (i0 == 11) {
                    showChatWindow(player, "pts/hellbound/bernarde003k.htm");
                }
            } else if (command.endsWith("reply=4")) {
                final long i1 = ItemFunctions.getItemCount(player, treasure_of_natives);
                if (i1 >= 1) {
                    HellboundManager.addConfidence(i1 * 1000);
                    ItemFunctions.removeItem(player, treasure_of_natives, i1);
                    showChatWindow(player, "pts/hellbound/bernarde002d.htm");
                    if (!ServerVariables.getBool("HB_bernardBoxes", false)) {
                        ServerVariables.set("HB_bernardBoxes", true);
                    }
                } else {
                    showChatWindow(player, "pts/hellbound/bernarde002e.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}