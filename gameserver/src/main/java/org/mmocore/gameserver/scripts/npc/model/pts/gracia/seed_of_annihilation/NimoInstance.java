package org.mmocore.gameserver.scripts.npc.model.pts.gracia.seed_of_annihilation;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author pchayka, KilRoy
 */
public final class NimoInstance extends NpcInstance {
    private static final int machine_for_marguene = 15487;
    private static final String fnMaxNpc = "pts/gracia/seed_of_annihilation/nimo004b.htm";
    private static final String Hadit = "pts/gracia/seed_of_annihilation/nimo003b.htm";
    private static final String InvenFull = "pts/gracia/seed_of_annihilation/nimo003c.htm";
    private static final String GiveitSuccess = "pts/gracia/seed_of_annihilation/nimo003a.htm";
    private static final String SpawnSuccess = "pts/gracia/seed_of_annihilation/nimo004a.htm";
    private int npc_count;

    public NimoInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        npc_count = 0;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("menu_select?ask=-415&")) {
            if (command.endsWith("reply=1")) {
                if (ItemFunctions.getItemCount(player, machine_for_marguene) > 0) {
                    showChatWindow(player, Hadit);
                } else if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                    showChatWindow(player, InvenFull);
                } else {
                    showChatWindow(player, GiveitSuccess);
                    ItemFunctions.addItem(player, machine_for_marguene, 1);
                }
            } else if (command.endsWith("reply=2")) {
                if (!player.getPlayerVariables().getBoolean(PlayerVariables.MAGUEN_PARAM)) {
                    if (npc_count > 5) {
                        showChatWindow(player, fnMaxNpc);
                    } else {
                        npc_count++;
                        player.getPlayerVariables().set(PlayerVariables.MAGUEN_PARAM, "true", -1);
                        final NpcInstance npc = NpcUtils.spawnSingle(18839, Location.findPointToStay(getSpawnedLoc(), 40, 100, getGeoIndex()), getReflection()); // wild maguen
                        npc.setTitle(player.getName());
                        npc.broadcastCharInfo();
                        showChatWindow(player, SpawnSuccess);
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                npc_count--;
                                if (npc_count < 0) {
                                    npc_count = 0;
                                }
                                player.getPlayerVariables().remove(PlayerVariables.MAGUEN_PARAM);
                            }
                        }, 6000L);
                    }
                } else {
                    showChatWindow(player, fnMaxNpc);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}