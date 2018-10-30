package org.mmocore.gameserver.scripts.npc.model.pts.giran;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.MerchantInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author : Mangol
 */
public class AlexandriaInstance extends MerchantInstance {
    private static final int goods_ex1 = 6471;
    private static final int goods_ex2 = 5094;
    private static final int goods_ex3 = 9814;
    private static final int goods_ex4 = 9816;
    private static final int goods_ex5 = 9817;
    private static final int goods_ex6 = 9815;
    private static final int goods_ex7 = 57;
    private static final int num_ex1 = 25;
    private static final int num_ex2 = 50;
    private static final int num_ex3 = 4;
    private static final int num_ex4 = 5;
    private static final int num_ex5 = 5;
    private static final int num_ex6 = 3;
    private static final int num_ex7 = 7500000;
    private static final int refund_rate = 90;
    private static final int agathion_failed = 10408;
    private static final int agathion_devil1 = 10321;
    private static final int agathion_devil2 = 10322;
    private static final int agathion_devil3 = 10323;
    private static final int agathion_devil4 = 10324;
    private static final int agathion_devil5 = 10325;
    private static final int agathion_devil = 10326;
    private static final int prob_devil1 = 600;
    private static final int prob_devil2 = 10;
    private static final int prob_devil3 = 10;
    private static final int prob_devil4 = 5;
    private static final int prob_devil5 = 5;
    private static final int prob_devil = 370;
    private static final int agathion_angel1 = 10315;
    private static final int agathion_angel2 = 10316;
    private static final int agathion_angel3 = 10317;
    private static final int agathion_angel4 = 10318;
    private static final int agathion_angel5 = 10319;
    private static final int agathion_angel = 10320;
    private static final int prob_angel1 = 600;
    private static final int prob_angel2 = 10;
    private static final int prob_angel3 = 10;
    private static final int prob_angel4 = 5;
    private static final int prob_angel5 = 5;
    private static final int prob_angel = 370;

    private static final int armor_by = 5001;
    private static final int groceries_by = 565;

    public AlexandriaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "pts/giran/alexandria001.htm");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-1901")) {
            int i0 = Rnd.get(1000);
            int i1 = 0;
            int i2 = prob_devil1;
            int i3 = agathion_devil1;
            if (command.endsWith("reply=1")) {
                if (!player.isWeightLimit(true)) {
                    return;
                }
                if (!player.isInventoryLimit(true)) {
                    return;
                }
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (agathion_failed == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, agathion_failed, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_devil1;
                i2 = i2 + prob_devil2;
                i3 = agathion_devil2;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_devil2;
                i2 = i2 + prob_devil3;
                i3 = agathion_devil3;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_devil3;
                i2 = i2 + prob_devil4;
                i3 = agathion_devil4;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_devil4;
                i2 = i2 + prob_devil5;
                i3 = agathion_devil5;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_devil5;
                i2 = i2 + prob_devil;
                i3 = agathion_devil;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
            } else if (command.endsWith("reply=2")) {
                if (!player.isWeightLimit(true)) {
                    return;
                }
                if (!player.isInventoryLimit(true)) {
                    return;
                }
                i0 = Rnd.get(1000);
                i1 = 0;
                i2 = prob_angel1;
                i3 = agathion_angel1;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (agathion_failed == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, agathion_failed, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_angel1;
                i2 = i2 + prob_angel2;
                i3 = agathion_angel2;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_angel2;
                i2 = i2 + prob_angel3;
                i3 = agathion_angel3;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_angel3;
                i2 = i2 + prob_angel4;
                i3 = agathion_angel4;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_angel4;
                i2 = i2 + prob_angel5;
                i3 = agathion_angel5;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
                i1 = i1 + prob_angel5;
                i2 = i2 + prob_angel;
                i3 = agathion_angel;
                if (i1 <= i0 && i0 < i2) {
                    if (ItemFunctions.getItemCount(player, goods_ex1) >= num_ex1 && ItemFunctions.getItemCount(player, goods_ex2) >= num_ex2 && ItemFunctions.getItemCount(player, goods_ex3) >= num_ex3 && ItemFunctions.getItemCount(player, goods_ex4) >= num_ex4 && ItemFunctions.getItemCount(player, goods_ex5) >= num_ex5 && ItemFunctions.getItemCount(player, goods_ex6) >= num_ex6 && ItemFunctions.getItemCount(player, goods_ex7) >= num_ex7) {
                        ItemFunctions.removeItem(player, goods_ex1, num_ex1);
                        ItemFunctions.removeItem(player, goods_ex2, num_ex2);
                        ItemFunctions.removeItem(player, goods_ex3, num_ex3);
                        ItemFunctions.removeItem(player, goods_ex4, num_ex4);
                        ItemFunctions.removeItem(player, goods_ex5, num_ex5);
                        ItemFunctions.removeItem(player, goods_ex6, num_ex6);
                        ItemFunctions.removeItem(player, goods_ex7, num_ex7);
                        ItemFunctions.addItem(player, i3, 1);
                        if (0 == 0) {
                            showChatWindow(player, "pts/giran/alexandria003.htm");
                        } else {
                            ItemFunctions.addItem(player, 0, 1);
                            showChatWindow(player, "pts/giran/alexandria003a.htm");
                        }
                    } else {
                        showChatWindow(player, "pts/giran/alexandria004.htm");
                    }
                    return;
                }
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=2")) {
            MultiSellHolder.getInstance().SeparateAndSend(armor_by, player, getObjectId(), 0);
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=565")) {
            MultiSellHolder.getInstance().SeparateAndSend(groceries_by, player, getObjectId(), 0);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
