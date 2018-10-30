package org.mmocore.gameserver.scripts.npc.model.pts.hellbound;

import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class KiefInstance extends NpcInstance {
    private static final int permit_of_darion = 9674;
    private static final int weak_magic_energy = 9680;
    private static final int magic_energy = 9681;
    private static final int conservation_magic_energy = 9682;
    private static final int poison_sting_of_scorpion = 10012;
    private static final int magic_calabash = 9672;

    public KiefInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player talker, int val, Object... arg) {
        final int i0 = HellboundManager.getHellboundLevel();

        if (i0 < 2) {
            showChatWindow(talker, "pts/hellbound/kief001.htm");
        } else if (i0 >= 2 && i0 <= 3) {
            showChatWindow(talker, "pts/hellbound/kief001a.htm");
        } else if (i0 == 4) {
            showChatWindow(talker, "pts/hellbound/kief001e.htm");
        } else if (i0 == 5) {
            showChatWindow(talker, "pts/hellbound/kief001d.htm");
        } else if (i0 == 6) {
            showChatWindow(talker, "pts/hellbound/kief001b.htm");
        } else if (i0 == 7) {
            showChatWindow(talker, "pts/hellbound/kief001c.htm");
        } else if (i0 >= 8) {
            showChatWindow(talker, "pts/hellbound/kief001f.htm");
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
                final long i0 = ItemFunctions.getItemCount(player, permit_of_darion);
                if (i0 >= 1) {
                    HellboundManager.addConfidence(i0 * 10);
                    ItemFunctions.removeItem(player, permit_of_darion, i0);
                    showChatWindow(player, "pts/hellbound/kief010.htm");
                } else {
                    showChatWindow(player, "pts/hellbound/kief010a.htm");
                }
            } else if (command.endsWith("reply=2")) {
                final long i0 = ItemFunctions.getItemCount(player, weak_magic_energy);
                if (i0 >= 1) {
                    HellboundManager.addConfidence(i0 * 20);
                    ItemFunctions.removeItem(player, weak_magic_energy, i0);
                    showChatWindow(player, "pts/hellbound/kief011a.htm");
                } else {
                    showChatWindow(player, "pts/hellbound/kief011b.htm");
                }
            } else if (command.endsWith("reply=3")) {
                final long i0 = ItemFunctions.getItemCount(player, magic_energy);
                if (i0 >= 1) {
                    HellboundManager.addConfidence(i0 * 80);
                    ItemFunctions.removeItem(player, magic_energy, i0);
                    showChatWindow(player, "pts/hellbound/kief011c.htm");
                } else {
                    showChatWindow(player, "pts/hellbound/kief011d.htm");
                }
            } else if (command.endsWith("reply=4")) {
                final long i0 = ItemFunctions.getItemCount(player, conservation_magic_energy);
                if (i0 >= 1) {
                    HellboundManager.addConfidence(i0 * 200);
                    ItemFunctions.removeItem(player, conservation_magic_energy, i0);
                    showChatWindow(player, "pts/hellbound/kief011e.htm");
                } else {
                    showChatWindow(player, "pts/hellbound/kief011f.htm");
                }
            } else if (command.endsWith("reply=5")) {
                showChatWindow(player, "pts/hellbound/kief011g.htm");
            } else if (command.endsWith("reply=6")) {
                final long i0 = ItemFunctions.getItemCount(player, poison_sting_of_scorpion);
                if (i0 >= 20) {
                    ItemFunctions.addItem(player, magic_calabash, 1);
                    ItemFunctions.removeItem(player, poison_sting_of_scorpion, 20);
                    showChatWindow(player, "pts/hellbound/kief011h.htm");
                } else {
                    showChatWindow(player, "pts/hellbound/kief011i.htm");
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}