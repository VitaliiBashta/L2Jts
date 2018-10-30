package org.mmocore.gameserver.scripts.npc.model.pts.gracia.seed_of_annihilation;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author KilRoy
 */
public class SeyoInstance extends NpcInstance {
    private final String fnBusy = "pts/gracia/seed_of_annihilation/seyo003.htm";
    private final String fnNoToken0 = "pts/gracia/seed_of_annihilation/seyo002a.htm";
    private final String fnNoToken1 = "pts/gracia/seed_of_annihilation/seyo002b.htm";
    private final String fnNoToken2 = "pts/gracia/seed_of_annihilation/seyo002c.htm";
    private final int a_broken_piece_of_soul_stone = 15486;
    private int i_ai0;

    public SeyoInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        i_ai0 = 0;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (i_ai0 == 1) {
            showChatWindow(player, fnBusy);
            return;
        }

        if (command.startsWith("menu_select?ask=-415&")) {
            if (command.endsWith("reply=1")) {
                if (ItemFunctions.getItemCount(player, a_broken_piece_of_soul_stone) > 0) {
                    ItemFunctions.removeItem(player, a_broken_piece_of_soul_stone, 1);
                    i_ai0 = 1;
                    final int i0 = Rnd.get(100) + 1;
                    if (i0 > 99) {
                        ItemFunctions.addItem(player, a_broken_piece_of_soul_stone, 100);
                        Functions.npcSay(this, NpcString.AMAZING_S1_TOOL_100_OF_THESE_SOUL_STONE_FRAGMENT_WHAT_A_COMPLETE_SWINDLER, player.getName());
                    } else {
                        Functions.npcSay(this, NpcString.HMM_HEY_DID_YOU_GIVE_S1_SOMETHING_BUT_IT_WAS_JUST_1_HAHA, player.getName());
                    }
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            timerFiredEx();
                        }
                    }, 5000L);
                } else {
                    showChatWindow(player, fnNoToken0);
                }
            } else if (command.endsWith("reply=5")) {
                if (ItemFunctions.getItemCount(player, a_broken_piece_of_soul_stone) >= 5) {
                    ItemFunctions.removeItem(player, a_broken_piece_of_soul_stone, 5);
                    i_ai0 = 1;
                    final int i0 = Rnd.get(100) + 1;
                    if (i0 > 80) {
                        final int i1 = Rnd.get(3) + 5 * 2;
                        ItemFunctions.addItem(player, a_broken_piece_of_soul_stone, i1);
                        Functions.npcSay(this, NpcString.S1_PULLED_ONE_WITH_S2_DIGITS_LUCKY_NOT_BAD, player.getName(), String.valueOf(i1));
                    } else if (i0 > 20 && i0 <= 80) {

                        ItemFunctions.addItem(player, a_broken_piece_of_soul_stone, 1);
                        Functions.npcSay(this, NpcString.IT_S_BETTER_THAN_LOSING_IT_ALL_RIGHT_OR_DOES_THIS_FEEL_WORSE);
                    } else {
                        Functions.npcSay(this, NpcString.AHEM_S1_HAS_NO_LUCK_AT_ALL_TRY_PRAYING, player.getName());
                    }
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            timerFiredEx();
                        }
                    }, 5000L);
                } else {
                    showChatWindow(player, fnNoToken1);
                }
            } else if (command.endsWith("reply=20")) {
                if (ItemFunctions.getItemCount(player, a_broken_piece_of_soul_stone) >= 20) {
                    ItemFunctions.removeItem(player, a_broken_piece_of_soul_stone, 20);
                    i_ai0 = 1;
                    final int i0 = Rnd.get(10000) + 1;
                    if (i0 == 10000) {
                        ItemFunctions.addItem(player, a_broken_piece_of_soul_stone, i0);
                        Functions.npcSay(this, NpcString.AH_ITS_OVER_WHAT_KIND_OF_GUY_IT_THAT_DAMN_FINE_YOU_S1_TAKE_IT_AND_GET_OUTTA_HERE, player.getName());
                    } else if (i0 > 10 && i0 <= 9999) {
                        ItemFunctions.addItem(player, a_broken_piece_of_soul_stone, 1);
                        Functions.npcSay(this, NpcString.A_BIG_PIECE_IS_MADE_UP_OF_LITTLE_PIECES_SO_HERE_S_A_LITTLE_PIECE);

                    } else if (i0 <= 10) {
                        final int i1 = Rnd.get(100);
                        ItemFunctions.addItem(player, a_broken_piece_of_soul_stone, i1);
                        Functions.npcSay(this, NpcString.YOU_DON_T_FEEL_BAD_RIGHT_ARE_YOU_SAD_BUT_DON_T_CRY);
                    }
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            timerFiredEx();
                        }
                    }, 5000L);
                } else {
                    showChatWindow(player, fnNoToken2);
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void timerFiredEx() {
        if (i_ai0 == 1) {
            i_ai0 = 0;
            final int i0 = Rnd.get(5);
            switch (i0) {
                case 1:
                    Functions.npcSay(this, NpcString.OK_WHO_S_NEXT_IT_ALL_DEPENDS_ON_YOUR_FATE_AND_LUCK_RIGHT_AT_LEAST_COME_AND_TAKE_A_LOOK);
                    break;
                case 2:
                    Functions.npcSay(this, NpcString.NO_ONE_ELSE_DONT_WORRY_I_DON_T_BITE_HAHA);
                    break;
                case 3:
                    Functions.npcSay(this, NpcString.THERE_WAS_SOMEONE_WHO_WON_10000_FROM_ME_A_WARRIOR_SHOULDN_T_JUST_BE_GOOD_AT_FIGHTING_RIGHT_YOU_VE_GOTTA_BE_GOOD_IN_EVERYTHING);
                    break;
                case 4:
                    Functions.npcSay(this, NpcString.OK_MASTER_OF_LUCK_THAT_S_YOU_HAHA_WELL_ANYONE_CAN_COME_AFTER_ALL);
                    break;
                case 5:
                    Functions.npcSay(this, NpcString.SHEDDING_BLOOD_IS_A_GIVEN_ON_THE_BATTLEFIELD_AT_LEAST_IT_S_SAFE_HERE);
                    break;
            }
        }
    }
}