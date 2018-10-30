package org.mmocore.gameserver.scripts.npc.model.pts.dragon_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author KilRoy
 * Ai: ai_seven_raid_summoner
 */
public final class DragonVortexInstance extends NpcInstance {
    private static final String my_loc_1 = "[dragon_valey_vortex1]";
    private static final String my_loc_2 = "[dragon_valey_vortex2]";
    private static final String my_loc_3 = "[dragon_valey_vortex3]";
    private static final String my_loc_4 = "[dragon_valey_vortex4]";
    private static final int spot_1_x = 92744;
    private static final int spot_1_y = 114045;
    private static final int spot_1_z = -3072;
    private static final int spot_2_x = 110112;
    private static final int spot_2_y = 124976;
    private static final int spot_2_z = -3624;
    private static final int spot_3_x = 121637;
    private static final int spot_3_y = 113657;
    private static final int spot_3_z = -3792;
    private static final int spot_4_x = 109346;
    private static final int spot_4_y = 111849;
    private static final int spot_4_z = -3040;
    private static final int[] bosses = {25718, 25719, 25720, 25721, 25722, 25723, 25724};

    private boolean i_ai2 = true;

    public DragonVortexInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "pts/dragon_valley/seven_raid_summoner001.htm");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("menu_select?ask=-1&reply=1")) {
            if (!i_ai2) {
                showChatWindow(player, "pts/dragon_valley/seven_raid_summoner003.htm");
                return;
            }

            if (ItemFunctions.getItemCount(player, 17248) > 0) {
                ItemFunctions.removeItem(player, 17248, 1, true);
                if (isInZone(my_loc_1)) {
                    NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], spot_1_x, spot_1_y, spot_1_z, 0);
                } else if (isInZone(my_loc_2)) {
                    NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], spot_2_x, spot_2_y, spot_2_z, 0);
                } else if (isInZone(my_loc_3)) {
                    NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], spot_3_x, spot_3_y, spot_3_z, 0);
                } else if (isInZone(my_loc_4)) {
                    NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], spot_4_x, spot_4_y, spot_4_z, 0);
                }
                i_ai2 = false;
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        i_ai2 = true;
                    }
                }, 60 * 1000L);
            } else {
                showChatWindow(player, "pts/dragon_valley/seven_raid_summoner002.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}