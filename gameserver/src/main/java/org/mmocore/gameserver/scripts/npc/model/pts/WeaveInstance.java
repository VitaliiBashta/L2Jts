package org.mmocore.gameserver.scripts.npc.model.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.instances.MerchantInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author Mangol
 */
public class WeaveInstance extends MerchantInstance {
    private static final int adena = 57;
    // c pinn
    private static final int sealed_magic_pin_c = 13898;
    private static final int magic_pin_c_highest = 13905;
    private static final int magic_pin_c_higher = 13904;
    private static final int magic_pin_c_general = 13903;
    private static final int magic_pin_c_low = 13902;
    // b pin
    private static final int sealed_magic_pin_b = 13899;
    private static final int magic_pin_b_highest = 13909;
    private static final int magic_pin_b_higher = 13908;
    private static final int magic_pin_b_general = 13907;
    private static final int magic_pin_b_low = 13906;
    // a pin
    private static final int sealed_magic_pin_a = 13900;
    private static final int magic_pin_a_highest = 13913;
    private static final int magic_pin_a_higher = 13912;
    private static final int magic_pin_a_general = 13911;
    private static final int magic_pin_a_low = 13910;
    // s pin
    private static final int sealed_magic_pin_s = 13901;
    private static final int magic_pin_s_highest = 13917;
    private static final int magic_pin_s_higher = 13916;
    private static final int magic_pin_s_general = 13915;
    private static final int magic_pin_s_low = 13914;
    // c pouch
    private static final int sealed_magic_pouch_c = 13918;
    private static final int magic_pouch_c_highest = 13925;
    private static final int magic_pouch_c_higher = 13924;
    private static final int magic_pouch_c_general = 13923;
    private static final int magic_pouch_c_low = 13922;
    // b pouch
    private static final int sealed_magic_pouch_b = 13919;
    private static final int magic_pouch_b_highest = 13929;
    private static final int magic_pouch_b_higher = 13928;
    private static final int magic_pouch_b_general = 13927;
    private static final int magic_pouch_b_low = 13926;
    // a pouch
    private static final int sealed_magic_pouch_a = 13920;
    private static final int magic_pouch_a_highest = 13933;
    private static final int magic_pouch_a_higher = 13932;
    private static final int magic_pouch_a_general = 13931;
    private static final int magic_pouch_a_low = 13930;
    // s pouch
    private static final int sealed_magic_pouch_s = 13921;
    private static final int magic_pouch_s_highest = 13937;
    private static final int magic_pouch_s_higher = 13936;
    private static final int magic_pouch_s_general = 13936;
    private static final int magic_pouch_s_low = 13934;
    // a plip
    private static final int sealed_magic_rune_clip_a = 14902;
    private static final int magic_rune_clip_a_highest = 14909;
    private static final int magic_rune_clip_a_higher = 14908;
    private static final int magic_rune_clip_a_general = 14907;
    private static final int magic_rune_clip_a_low = 14906;
    // s plip
    private static final int sealed_magic_rune_clip_s = 14903;
    private static final int magic_rune_clip_s_highest = 14913;
    private static final int magic_rune_clip_s_higher = 14912;
    private static final int magic_rune_clip_s_general = 14911;
    private static final int magic_rune_clip_s_low = 14910;
    // a deco
    private static final int sealed_magic_deco_a = 14904;
    private static final int magic_deco_a_highest = 14917;
    private static final int magic_deco_a_higher = 14916;
    private static final int magic_deco_a_general = 14915;
    private static final int magic_deco_a_low = 14914;
    // s deco
    private static final int sealed_magic_deco_s = 14905;
    private static final int magic_deco_s_highest = 14921;
    private static final int magic_deco_s_higher = 14920;
    private static final int magic_deco_s_general = 14919;
    private static final int magic_deco_s_low = 14918;
    // count
    private static final int fee_for_release_pin_c = 3200;
    private static final int fee_for_release_pin_b = 11800;
    private static final int fee_for_release_pin_a = 26500;
    private static final int fee_for_release_pin_s = 136600;
    private static final int fee_for_release_pou_c = 3200;
    private static final int fee_for_release_pou_b = 11800;
    private static final int fee_for_release_pou_a = 26500;
    private static final int fee_for_release_pou_s = 136600;
    private static final int fee_for_release_rune_a = 26500;
    private static final int fee_for_release_rune_s = 136600;
    private static final int fee_for_release_deco_a = 26500;
    private static final int fee_for_release_deco_s = 136600;
    // String
    private static final String fnNotHaveItem = "pts/npc_weave/weaver_wolf_adams005.htm";
    private static final String fnNotHaveAdena = "pts/npc_weave/weaver_wolf_adams006.htm";
    // multisell
    private static final int belt_trade1 = 649;
    private static final int cloak_trade = 650;

    public WeaveInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=23010&")) {
            if (ItemFunctions.getItemCount(player, adena) > 0) {
                if (command.endsWith("reply=1")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pin_c) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pin_c, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pin_c);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pin_c_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pin_c_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pin_c_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pin_c_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=2")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pin_b) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pin_b, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pin_b);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pin_b_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pin_b_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pin_b_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pin_b_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=3")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pin_a) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pin_a, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pin_a);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pin_a_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pin_a_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pin_a_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pin_a_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=4")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pin_s) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pin_s, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pin_s);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pin_s_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pin_s_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pin_s_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pin_s_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                }
            } else {
                showChatWindow(player, fnNotHaveAdena);
            }
        }
        // ////////
        else if (command.startsWith("menu_select?ask=23020&")) {
            if (ItemFunctions.getItemCount(player, adena) > 0) {
                if (command.endsWith("reply=1")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pouch_c) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pouch_c, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pou_c);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pouch_c_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pouch_c_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pouch_c_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pouch_c_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=2")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pouch_b) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pouch_b, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pou_b);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pouch_b_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pouch_b_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pouch_b_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pouch_b_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=3")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pouch_a) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pouch_a, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pou_a);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pouch_a_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pouch_a_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pouch_a_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pouch_a_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=4")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_pouch_s) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_pouch_s, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_pou_s);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_pouch_s_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_pouch_s_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_pouch_s_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_pouch_s_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                }
            } else {
                showChatWindow(player, fnNotHaveAdena);
            }
        } else if (command.startsWith("menu_select?ask=23030&")) {
            if (ItemFunctions.getItemCount(player, adena) > 0) {
                if (command.endsWith("reply=1")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_rune_clip_a) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_rune_clip_a, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_rune_a);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_rune_clip_a_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_rune_clip_a_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_rune_clip_a_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_rune_clip_a_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=2")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_rune_clip_s) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_rune_clip_s, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_rune_s);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_rune_clip_s_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_rune_clip_s_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_rune_clip_s_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_rune_clip_s_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                }
            } else {
                showChatWindow(player, fnNotHaveItem);
            }
        } else if (command.startsWith("menu_select?ask=23040&")) {
            if (ItemFunctions.getItemCount(player, adena) > 0) {
                if (command.endsWith("reply=1")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_deco_a) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_deco_a, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_deco_a);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_deco_a_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_deco_a_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_deco_a_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_deco_a_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                } else if (command.endsWith("reply=2")) {
                    int i0 = Rnd.get(200);
                    if (ItemFunctions.getItemCount(player, sealed_magic_deco_s) > 0) {
                        ItemFunctions.removeItem(player, sealed_magic_deco_s, 1);
                        ItemFunctions.removeItem(player, adena, fee_for_release_deco_s);
                        if (i0 <= 1) {
                            ItemFunctions.addItem(player, magic_deco_s_highest, 1);
                        } else if (i0 <= 10) {
                            ItemFunctions.addItem(player, magic_deco_s_higher, 1);
                        } else if (i0 <= 40) {
                            ItemFunctions.addItem(player, magic_deco_s_general, 1);
                        } else if (i0 <= 100) {
                            ItemFunctions.addItem(player, magic_deco_s_low, 1);
                        } else {
                            ChatUtils.say(this, NpcString.WHAT_A_PREDICAMENT_MY_ATTEMPTS_WERE_UNSUCCESSUFUL);
                        }
                    } else {
                        showChatWindow(player, fnNotHaveItem);
                    }
                }
            } else {
                showChatWindow(player, fnNotHaveItem);
            }
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=649")) {
            MultiSellHolder.getInstance().SeparateAndSend(belt_trade1, player, getObjectId(), 0);
        } else if (command.equalsIgnoreCase("menu_select?ask=-303&reply=650")) {
            MultiSellHolder.getInstance().SeparateAndSend(cloak_trade, player, getObjectId(), 0);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
